package topic.app.client;

import topic.serialization.Query;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Arrays;

import static topic.serialization.TopicConstants.*;

import static java.lang.System.exit;

/**
 * Topic Client Class
 * @version 1.0
 * @author David Beggs
 */
public class Client {

    /**
     * Main method
     * @param args Command line arguments
     * @throws IOException thrown if wrong amount of arguments
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 3) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> <Port> <Requested Number of Responses>");
        }

        //parse arguments
        String serverString = args[0];
        Integer portNum = Integer.parseInt(args[1]);
        Integer responseNum = Integer.parseInt(args[2]);

        //test port number argument
        testPort(portNum, EPHEMERAL_MIN, EPHEMERAL_MAX, "INCORRECT PORT NUMBER ARGUMENT");

        //test response number argument
        if (responseNum < 0 || responseNum > MAX_REQUESTS){
            System.err.println("INCORRECT RESPONSE NUMBER ARGUMENT");
            exit(-1);
        }

        InetAddress addr = InetAddress.getByName(serverString); //generate ip address from args
        DatagramSocket socket = new DatagramSocket();       //generate new UDP socket
        socket.setSoTimeout(TIMEOUT);               //set packet timeout

        long leftLimit = 1L;                    //generate random queryID
        long rightLimit = MAX_QUERYID;
        long generatedID = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

        Query sender = new Query(generatedID, responseNum); //Initialize query from args

        byte [] encoded = sender.encode();                  //encode and send query
        byte [] received = new byte [MAX_REQUESTS];
        DatagramPacket sendPacket = new DatagramPacket(encoded, encoded.length, addr, portNum);
        DatagramPacket receivePacket = new DatagramPacket(received, MAX_REQUESTS, addr, portNum);

        int tries = 0;      // Packets may be lost, so we have to keep trying
        boolean receivedResponse = false;
        Response response = null;

        do {
            socket.send(sendPacket);

            try {
                socket.receive(receivePacket);      //attempt to receive packet from server
                if (!receivePacket.getAddress().equals(addr)){          //verify response ip address
                    System.err.println("INCORRECT RESPONSE ORIGIN");
                    exit(-1);
                }

                try{                                //attempt to create response from response message array
                    response = new Response(Arrays.copyOfRange(receivePacket.getData(), 0,
                            receivePacket.getLength()));
                    receivedResponse = true;

                } catch (TopicException e){
                    System.err.println(e.getErrorCode().getErrorMessage());
                    exit(-1);
                }

                if (response.getQueryID() != generatedID){      //verify queryID
                    receivedResponse = false;
                    tries++;
                }
                else if (response.getErrorCode().getErrorCodeValue() != 0) {
                        //verify error code
                    System.err.println(response.getErrorCode().getErrorMessage());
                    exit(-1);
                }

            } catch (InterruptedIOException e){     //increment number of tries
                tries++;
                System.out.println("Timeout");
            }
        } while ((!receivedResponse) && (tries < MAXTRIES));        //attempt until max tries hit

        if (receivedResponse){          //print response
            System.out.println(response.toString());
        }
        else {
            System.out.println("TIME OUT, COULD NOT RECEIVE RESPONSE");
        }
        socket.close();
    }

    private static void testPort(Integer portNum, Integer ephemeralMin, Integer ephemeralMax, String s) {
        if (portNum < ephemeralMin || portNum > ephemeralMax) {
            System.err.println(s);
            exit(-1);
        }
    }
}