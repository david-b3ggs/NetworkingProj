package tiktak.app.server;

import tiktak.app.yipper.Yipper;
import tiktak.serialization.Error;
import tiktak.serialization.ID;
import tiktak.serialization.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tiktak.serialization.TikTakConstants.*;

/**
 * Clienthandler is the thread to handle client connections from the server
 * @version 1.0
 * @author David Beggs
 */
class ClientHandler implements Runnable {

    private Socket clientSocket;                //client socket sent to thread
    private Map<String, String> userMap;        //map of users to passwords

                                                //ensure no repeat nonce
    private HashSet<Integer> usedNonces = new HashSet<>();
    private Map<String, Integer> tostMap;       //create for tost sequence numbers
    private String pass;                        //current client password
    private String user;                        //current client username
    private Integer nonce;                      //current calculated nonce
    private String hash;                        //generated hash for id verification
    private Logger LOGGER;                      //Logger
    private CopyOnWriteArrayList<String> archive;

    /**
     * Run function immplements runnable and starts thread control flow
     */
    @Override
    public void run() {
                            //establish queue for flow control, and create yipper file
        Queue<String> messageFlow = new LinkedList<>();
        messageFlow.add(ID);
        messageFlow.add(CREDENTIASL_GET_OP);
        Yipper yip = null;

        try {               //initialize yipper file
            yip = new Yipper("./outputFile.html");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Warning: could not find yipper file, attempting to create");
        }
                            //Advertise to new connection with version message
        Message advertise = new Version();

        try {               //encode and send version message
            advertise.encode(new MessageOutput(clientSocket.getOutputStream()));     //advertise
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to communicate: error with outputstream. In thread:" + Thread.currentThread().getId());
            try {
                clientSocket.close();
            } catch (IOException w) {
                LOGGER.log(Level.SEVERE, "Unable to communicate: Cannot close. In thread:" + Thread.currentThread().getId());
            }
        }
                            //while the socket is open, run main threadflow
        while (!this.clientSocket.isClosed()) {
            Message currentMessage = null;

            try {           //decode message and get operation
                currentMessage = Message.decode(new MessageInput(clientSocket.getInputStream()));
                String op = currentMessage.getOperation();
                LOGGER.log(Level.INFO, "Message received: " + currentMessage.toString());


                if (currentMessage != null) {
                    try {
                        switch (op) {
                            case ID:        //verify id message and reply with challenge
                                if (messageFlow.peek().equals(ID)) {
                                    responseID((tiktak.serialization.ID) currentMessage);
                                    messageFlow.remove();       //remove from flow control loop
                                } else {
                                    LOGGER.log(Level.SEVERE, "Unexpected message: " + currentMessage.toString() +
                                            ". In thread:" + Thread.currentThread().getId());
                                    clientSocket.close();
                                }
                                break;
                            case CREDENTIASL_GET_OP:    //verify CRED message and verify identity, then send ack
                                if (messageFlow.peek().equals(CREDENTIASL_GET_OP)) {
                                    responseCred((Credentials) currentMessage);
                                    messageFlow.remove();       //remove flow control loop
                                } else {
                                    LOGGER.log(Level.SEVERE, "Unexpected message: " + currentMessage.toString() +
                                            ". In thread:" + Thread.currentThread().getId());
                                    clientSocket.close();
                                }
                                break;
                            case TOST:                  //verify tost message and reply with ack
                                if (messageFlow.isEmpty()) {
                                    responseTost((Tost) currentMessage, yip);
                                    clientSocket.close();       //close connection on transaction completion
                                } else {
                                    LOGGER.log(Level.SEVERE, "Unexpected message: " + currentMessage.toString()+
                                            ". In thread:" + Thread.currentThread().getId());
                                    clientSocket.close();
                                }
                                break;
                            case LTSRL:                         //verify letsroll message and reply with ack
                                if (messageFlow.isEmpty()) {
                                    responseLtsrl((LtsRL) currentMessage, yip);
                                    clientSocket.close();        //close connection on transaction
                                } else {
                                    LOGGER.log(Level.SEVERE, "Unexpected message: " + currentMessage.toString()+
                                            ". In thread:" + Thread.currentThread().getId());
                                    clientSocket.close();
                                }
                                break;
                            case ERROR:                         //print error messages as they come
                                LOGGER.log(Level.SEVERE, "Received error: " + currentMessage.toString()+
                                        ". In thread:" + Thread.currentThread().getId());
                                clientSocket.close();
                                break;
                            default:                            //handle unexpected message and close
                                System.err.println("Unexpected message: " + currentMessage.toString()+
                                        ". In thread:" + Thread.currentThread().getId());
                                clientSocket.close();
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Unable to communicate: could not recognize validate stream" +
                                ". In thread: " + Thread.currentThread().getId());
                        clientSocket.close();
                    } catch (ValidationException e){
                        LOGGER.log(Level.SEVERE, "Invalid message: message format invalid"+
                                ". In thread: " + Thread.currentThread().getId());
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                try {
                    LOGGER.log(Level.SEVERE, "Unexpected message: Unexpected message type"+
                            ". In thread: " + Thread.currentThread().getId());
                    clientSocket.close();
                } catch (IOException r) {}

            } catch (NoSuchAlgorithmException e){
                LOGGER.log(Level.WARNING, "Could not select MD5");
            } catch (ValidationException e){
                try {
                    LOGGER.log(Level.SEVERE, "Invalid message: Decoded message not valid"+
                            ". In thread: " + Thread.currentThread().getId());
                    clientSocket.close();
                } catch (IOException r) {}
            }
        }
    }

    /**
     * Responds to received ID message
     * @param i ID message from client
     * @throws NoSuchAlgorithmException thrown if MD5 hash is not found
     * @throws ValidationException thrown if created challenge is incorrect
     * @throws IOException thrown if client connection is compromised
     */
    private void responseID(ID i) throws NoSuchAlgorithmException, ValidationException, IOException {
        user = i.getID();       //set client username
        Random rand = new Random(); //generate random for nonce

        //generate nonce and test if unused
        boolean unused = usedNonces.add(nonce = Math.abs(rand.nextInt()));

        if ((pass = userMap.get(user)) != null) {        //Test if user exists
            while (!unused) {                            //test if nonce is available
                //regenerate if needed
                unused = usedNonces.add(nonce = Math.abs(rand.nextInt()));
            }
                //generate md5 hash
            hash = generateHash(nonce, pass);
            Challenge show = new Challenge(this.nonce.toString());  //create and display challenge
            show.encode(new MessageOutput(clientSocket.getOutputStream()));

            LOGGER.log(Level.INFO, "Message Sent: " + show.toString());
        } else {
                                //display error if user not found
            Error show = new Error(403, "No such user " + i.getID());
            show.encode(new MessageOutput(clientSocket.getOutputStream()));

            LOGGER.log(Level.SEVERE, show.toString()+
                    ". In thread: " + Thread.currentThread().getId());
            clientSocket.close();
        }
    }

    /**
     * Responds to received credential message from client
     * @param cred Incoming credential message
     * @throws IOException thrown if client connection is compromised
     * @throws ValidationException thrown if created ack is incorrect
     */
    private void responseCred(Credentials cred) throws IOException, ValidationException {
        String clientHash = cred.getHash(); //initialize client hash
        if (clientHash.equals(hash)) {      //test against known hash
            Ack show = new Ack();           //acknowlegde if correct
            show.encode(new MessageOutput(clientSocket.getOutputStream()));
            LOGGER.log(Level.INFO, "Message Sent: " + show.toString());
        } else {                            //send error if fail to authenticate
            Error show = new Error(403, "Unable to authenticate");
            show.encode(new MessageOutput(clientSocket.getOutputStream()));

            LOGGER.log(Level.SEVERE, show.toString()+
                    ". In thread: " + Thread.currentThread().getId());
            clientSocket.close();
        }
    }

    /**
     * Respond to incoming tost message
     * @param toast received tost message
     * @param yip yipper object to accept yips
     * @throws IOException thrown if client connection is compromised
     */
    private void responseTost(Tost toast, Yipper yip) throws IOException {
        if (tostMap.containsKey(user)){         //test if user exists
            int count = tostMap.get(user);      //fetch user, and increment tost number
            tostMap.put(user, ++count);         //display to yip
            yip.update(user + ": " + toast.toString() + " " + count);
        } else {
            tostMap.put(user, 1);               //create in map if didn't exist
                                                //display to yip
            yip.update(user + ": " + toast.toString() + " " + 1);
        }

        //archive Message
        this.archive.add(user + ": " + toast.toString() + " " + tostMap.get(user));

        Ack show = new Ack();                   //send ack after yip display
        show.encode(new MessageOutput(clientSocket.getOutputStream()));
        LOGGER.log(Level.INFO, "Message Sent: " + show.toString());
    }

    /**
     * Responds to received LtsRL message
     * @param roller received message
     * @param yip yipper to yip to
     * @throws IOException thrown if client connection is compromised
     */
    private void responseLtsrl(LtsRL roller, Yipper yip) throws IOException {
                                //display ltsrl to yip
        yip.updateWithImage(user + ": LtsRL #" + roller.getCategory(), roller.getImage());

        this.archive.add(user + ": LtsRL #" + roller.getCategory());
        Ack show = new Ack();                  //send ack after yip display
        show.encode(new MessageOutput(clientSocket.getOutputStream()));
        LOGGER.log(Level.INFO, "Message Sent: " + show.toString());
    }

    /**
     * Generates hash using nonce and user password
     * @param nonce int
     * @param pass string
     * @return String
     * @throws NoSuchAlgorithmException thrown if MD5 hash can't be found
     */
    private String generateHash(int nonce, String pass) throws NoSuchAlgorithmException {
        Integer noncy = nonce;
                                    //generate message digest and to create hash
        MessageDigest md = MessageDigest.getInstance(HASH_TYPE);
        String hashComponents = noncy.toString() + pass;
                                    //use digest
        byte[] mDigest = md.digest(hashComponents.getBytes(StandardCharsets.ISO_8859_1));

        BigInteger bigBoi = new BigInteger(1, mDigest);
        String hashActual = bigBoi.toString(16);

        while (hashActual.length() < 32) {  //impose length
            hashActual = "0" + hashActual;
        }

        hashActual = hashActual.toUpperCase();  //set to uppercase
        return hashActual;
    }

    /**
     * returns thread client socket
     * @param s Socket
     */
    public void setClientSocket(Socket s) {
        this.clientSocket = s;
    }

    /**
     * returns thread userMap
     * @param userMap HashMap
     */
    public void setUserMap(Map<String, String> userMap) {
        this.userMap = userMap;
    }

    /**
     * returns Logger for thread
     * @param LOGGER Logger
     */
    public void setLOGGER(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    /**
     * returns tostMap for thread
     * @param tostMap HashMap
     */
    public void setTostMap(Map<String, Integer> tostMap) {
        this.tostMap = tostMap;
    }

    /**
     * Returns object archive
     * @return List
     */
    public CopyOnWriteArrayList<String> getArchive() {
        return archive;
    }

    /**
     * Sets object Archive
     * @param archive CopyOnWriteArrayList
     * @return current object
     */
    public ClientHandler setArchive(CopyOnWriteArrayList<String> archive) {
        this.archive = archive;
        return this;
    }
}