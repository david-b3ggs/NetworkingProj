/*
 * Name: David Beggs
 * Assignment: Program 2
 * FileName: Client.java
 * Description: Client to interact with tiktak Server
 * Last Modified: 2/24/2020
 */
package tiktak.app.client;

import tiktak.serialization.Error;
import tiktak.serialization.ID;
import tiktak.serialization.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.System.exit;
import static tiktak.serialization.TikTakConstants.*;

/**
 * Client for tiktak application protocol
 *
 * @author David Beggs
 * @version 1.0
 */
public class Client {

    /**
     * Main method in Client object Interfaces with Server using tiktak protocol
     *
     * @param args Command line arguments sent to the program
     * @throws IOException              Thrown if socket IO error happens when creating the socket
     * @throws NoSuchAlgorithmException Thrown if md5 hash is not recognized in digester
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        if ((args.length < 5) || (args.length > 7)) {  // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> <port> <userid> <password> <request...> | <category> <image file>");
        }

        Queue<String> orderQueue = new LinkedList<>();

        orderQueue.add(VERSION_GET_OP);
        orderQueue.add(CHALLENGE_GET_OP);
        orderQueue.add(ACK);
        orderQueue.add(ACK);

        String serverString = args[0];
        int portNum = Integer.parseInt(args[1]);
        boolean expect = true;
        String userString = args[2];
        String passString = args[3];
        String reqString = args[4];

        if (!reqString.equals(TOST) && !reqString.equals(LTSRL)){
            System.err.println("Validation Failed: " + reqString + " is INCORRECT");
            exit(-1);
        }

        Socket socket = new Socket(serverString, portNum);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Message lastMessageSent = null;

        while (socket.isConnected() && !orderQueue.isEmpty()) {
            Message currentMessage = null;

            try {
                currentMessage = Message.decode(new MessageInput(in));
                System.out.println(currentMessage.toString());

            } catch (IOException e) {
                System.err.println("Unable to communicate: " + "Interrupted Stream Error.");
                System.err.println(lastMessageSent + " " + e.getMessage());

                exit(-1);
            } catch (ValidationException e) {
                System.err.println("Invalid Message: " + "Invalid Message Error");
                exit(-1);
            } catch (NullPointerException e) {
            }

            if (currentMessage == null) {        //incase while loop outpaces message stream, could produce deadlock
                if (!expect) {
                    break;
                }
            } else {
                String op = currentMessage.getOperation();

                try {
                    switch (op) {
                        case VERSION_GET_OP:
                            if (orderQueue.peek().equals(VERSION_GET_OP)) {
                                lastMessageSent = responseVersion(out, userString);
                                orderQueue.remove();
                            } else {
                                unexpectedError(currentMessage);
                            }
                            break;
                        case CHALLENGE_GET_OP:
                            if (orderQueue.peek().equals(CHALLENGE_GET_OP)) {
                                lastMessageSent = responseChallenge(out, (Challenge) currentMessage, passString);
                                orderQueue.remove();
                            } else {
                                unexpectedError(currentMessage);
                            }
                            break;
                        case ACK:
                            if (TOST.equals(lastMessageSent.getOperation()) ||
                                    LTSRL.equals(lastMessageSent.getOperation())) {
                                if (orderQueue.peek().equals(ACK)) {
                                    socket.close();
                                    expect = false;
                                    orderQueue.remove();
                                } else {
                                    unexpectedError(currentMessage);
                                }

                            } else if (CREDENTIASL_GET_OP.equals(lastMessageSent.getOperation())) {
                                if (orderQueue.peek().equals(ACK)) {
                                    if (reqString.equals(TOST)) {
                                        lastMessageSent = responseAckTOST(out);
                                        orderQueue.remove();
                                    } else if (reqString.equals(LTSRL)) {
                                        lastMessageSent = responseAckLTSRL(out, args[5], args[6]);
                                        orderQueue.remove();
                                    }
                                } else {
                                    unexpectedError(currentMessage);
                                }
                            }
                            break;
                        case ERROR:
                            System.err.println(currentMessage.toString());
                            exit(((Error)currentMessage).getCode());
                        default:
                            System.err.println("Unexpected message: " + currentMessage.toString());
                    }
                } catch (IOException e) {
                    System.err.println("Unable to communicate: " + e.getMessage());
                    exit(-1);
                } catch (ValidationException e) {
                    System.err.println("Validation failed: " + currentMessage.toString());
                    exit(-1);
                }
            }
        }

        socket.close();
    }

    /**
     * Responds to version message from server with tiktak protocol
     *
     * @param out  output stream from network
     * @param user Username passed by user in command line arguments
     * @return Message to record last sent message
     * @throws ValidationException thrown if encode detects grammer violation
     * @throws IOException         thrown if eofexception is thrown in encode
     */
    private static Message responseVersion(OutputStream out, String user) throws ValidationException, IOException {
        Message response = new ID(user);
        response.encode(new MessageOutput(out));

        return response;
    }

    /**
     * Response to challenge message with credentails message
     *
     * @param out     output stream to network
     * @param message Challenge message containing
     * @param pass    user password
     * @return response message to record last message
     * @throws NoSuchAlgorithmException Thrown if md5 hash is not recognized in digester
     * @throws ValidationException      thrown if encode detects grammer violation
     * @throws IOException              thrown if eofexception is thrown in encode
     */
    private static Message responseChallenge(OutputStream out, Challenge message, String pass) throws NoSuchAlgorithmException,
            ValidationException, IOException {

        MessageDigest md = MessageDigest.getInstance(HASH_TYPE);
        String hashComponents = message.getNonce() + pass;
        //specify encoding?
        byte[] mDigest = md.digest(hashComponents.getBytes(StandardCharsets.ISO_8859_1));

        BigInteger bigBoi = new BigInteger(1, mDigest);

        String hashActual = bigBoi.toString(16);

        while (hashActual.length() < 32) {
            hashActual = "0" + hashActual;
        }

        hashActual = hashActual.toUpperCase();

        Message response = new Credentials(hashActual);
        response.encode(new MessageOutput(out));

        return response;
    }

    /**
     * Response from client to ack with ltsrl message
     *
     * @param out      output stream to network
     * @param category Category specified by user input for image
     * @param image    Base64 encoded image file
     * @return response message to record last message
     * @throws ValidationException thrown if encode detects grammer violation
     * @throws IOException         thrown if eofexception is thrown in encode
     */
    private static Message responseAckLTSRL(OutputStream out, String category, String image) throws ValidationException, IOException {
        File imageFile = new File(image);
        byte[] pleaseGodBestowMercy = Files.readAllBytes(imageFile.toPath());
        Message response = new LtsRL(category, pleaseGodBestowMercy);
        response.encode(new MessageOutput(out));

        return response;
    }

    /**
     * Response from client to ack with tost message
     *
     * @param out output stream to server
     * @return Response to record last message
     * @throws IOException Thrown if encode detects eofexception
     */
    private static Message responseAckTOST(OutputStream out) throws IOException {
        Message response = new Tost();
        response.encode(new MessageOutput(out));

        return response;
    }

    /**
     * Handle Unexpected messages error
     *
     * @param m Current unexpected message
     */
    private static void unexpectedError(Message m) {
        System.err.println("Unexpected message: " + m.toString());
    }
}