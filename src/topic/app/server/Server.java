package topic.app.server;

import topic.serialization.ErrorCode;
import topic.serialization.Query;
import topic.serialization.Response;
import topic.serialization.TopicException;

import static topic.serialization.TopicConstants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.*;

/**
 * Server host for udp topic client
 * @version 1.0
 * @author David Beggs
 */
public class Server implements Runnable {

    private CopyOnWriteArrayList<String> archive;   //Archive of messages in chron order
    private int portNumber; //server host port number
    private Logger LOGGER; //Logger

    //TODO: AND LOGGING

    /**
     * Main thread execution
     */
    @Override
    public void run() {
        Logger LOGGER = Logger.getLogger("topic.log");     //initialize logger
        Handler LUMBERMAN;                                  //initiailize file handler

        try {
            LOGGER.setUseParentHandlers(false); //removes console logging for info level

            //sets file handler, and human readable format
            LUMBERMAN = new FileHandler("topic.log",true);
            LUMBERMAN.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(LUMBERMAN);
        } catch (IOException e){
            LOGGER.log(Level.WARNING, "ERROR: COULD NOT INITIALIZE LOGGER");
        }
        this.setLOGGER(LOGGER);
        //Create buffer to place received message
        byte [] receivedMessage = new byte[MAX_REQUESTS];
        //initialize socket/packet
        DatagramSocket sock = null;
        DatagramPacket pack = null;

        //Create socket and packet to receive
        try {
            sock = new DatagramSocket(this.portNumber);
            pack = new DatagramPacket(receivedMessage, MAX_REQUESTS);
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "ERROR: DATAGRAM INITIALIZATION FAILURE");
        }

        while (true){
            try {
                //Receive packet
                sock.receive(pack);
                LOGGER.log(Level.INFO, "Received Packet from: " + pack.getAddress() + ":" + pack.getPort());
                //Respond to client message
                respond(sock, pack);
                //reset receive length to avoid shrinking
                pack.setLength(MAX_REQUESTS);

            } catch (IOException e){
                LOGGER.log(Level.WARNING, "ERROR: FAILURE TO SEND PACKET");
            }
        }
    }

    /**
     * Responds to Client query
     * @param sock DatagramSocket
     * @param pack DatagramPacket
     * @throws IOException Thrown if sending packet fails
     */
    private void respond(DatagramSocket sock, DatagramPacket pack) throws IOException {
        try {
            //create query from received message, and generate a response
            Query message = new Query(Arrays.copyOfRange(pack.getData(), 0, pack.getLength()));
            Response response = new Response(message.getQueryID(),
                    ErrorCode.NOERROR, getPosts(message.getRequestedPosts()));
            //send packet if no query issues
            sendPacket(sock, pack, response);

        } catch (TopicException e){
            this.LOGGER.log(Level.WARNING, "ERROR: " + e.getMessage());
            //generate error query if necessary
            Response response = new Response(0, e.getErrorCode(), new ArrayList<>());
            sendPacket(sock, pack, response);
        }
    }

    /**
     * Sends packet to client
     * @param sock DatagramSocket
     * @param pack DatagramPacket
     * @param response Response message
     * @throws IOException Thrown in sending fails
     */
    private void sendPacket(DatagramSocket sock, DatagramPacket pack, Response response) throws IOException {
        //Create array from encoded response
        byte[] responseArray = response.encode();

        //test for MTU length fitting
        if (responseArray.length > MAX_PAYLOAD){
            //calculate
            int difference = responseArray.length - MAX_PAYLOAD;
            int offTheTop = 0;

            //Calculate number of posts to remove
            for (int i = this.archive.size() - 1; offTheTop < difference; i-- ){
                offTheTop += archive.get(i).length() + 1;
            }
            //remove necessary bytes
            responseArray = Arrays.copyOfRange(responseArray, 0, responseArray.length - offTheTop);
        }

        //Send response
        DatagramPacket responsePacket = new DatagramPacket(responseArray, responseArray.length,
                pack.getAddress(), pack.getPort());
        sock.send(responsePacket);
    }

    /**
     * Returns number of posts requested from the archive
     * @param numPosts number of posts to return
     * @return Synchronized list
     */
    public CopyOnWriteArrayList<String> getPosts(int numPosts){
        if (numPosts > this.archive.size()){
            return this.archive;
        } else {
            return new CopyOnWriteArrayList<>(this.archive.subList(this.archive.size() - numPosts, this.archive.size()));
        }
    }

    /**
     * Constructor
     * @param archive Archive of past posts
     * @param portNumber Port number to host server
     */
    public Server(CopyOnWriteArrayList<String> archive, int portNumber) {
        this.archive = archive;
        this.portNumber = portNumber;
    }

    /**
     * returns archive
     * @return CopyOn
     */
    public CopyOnWriteArrayList<String> getArchive() {
        return archive;
    }

    /**
     * Sets the archive variable
     * @param archive archive of past messages
     * @return current object
     */
    public Server setArchive(CopyOnWriteArrayList<String> archive) {
        this.archive = archive;
        return this;
    }


    /**
     * fetches port number
     * @return int
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * sets port number
     * @param portNumber int
     * @return this object
     */
    public Server setPortNumber(int portNumber) {
        this.portNumber = portNumber;
        return this;
    }

    /**
     * fetches logger
     * @return Logger
     */
    public Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * sets Logger
     * @param LOGGER logger
     * @return this object
     */
    public Server setLOGGER(Logger LOGGER) {
        this.LOGGER = LOGGER;
        return this;
    }
}
