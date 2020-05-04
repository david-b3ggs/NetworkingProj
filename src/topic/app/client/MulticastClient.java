package topic.app.client;

import topic.serialization.Response;
import topic.serialization.TopicException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;
import static topic.serialization.TopicConstants.*;

/**
 * Multicast Client class to receive updates on topics
 * @version 1.0
 * @author David Beggs
 */
public class MulticastClient {

    /**
     * Main Execution of client
     * @param args Command line argument array of strings
     * @throws IOException thrown if input is out of bounds
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Address> <Port>");
        }

        //parse arguments
        String serverString = args[0];
        Integer portNum = Integer.parseInt(args[1]);

        //test port number argument
        testPort(portNum);

        InetAddress addr = InetAddress.getByName(serverString); //generate ip address from args

        if (!addr.isMulticastAddress()) { // Test if multicast address
            throw new IllegalArgumentException("ERROR: NOT A MULTICAST ADDRESS");
        }

        MulticastSocket socket = new MulticastSocket(portNum);       //generate new UDP socket
        //Create networking objects
        socket.joinGroup(addr);
        boolean quit = false;
        byte [] received = new byte [MAX_REQUESTS];
        DatagramPacket receivePacket = new DatagramPacket(received, MAX_REQUESTS, addr, portNum);

        //Create scanner wrapper around standard in
        Scanner scanman = new Scanner(System.in);

        MultiClientThread m = new MultiClientThread();
        //pass appropriate variables to thread
        m.setReceivePacket(receivePacket);
        m.setSocket(socket);
        MultiClientThread.setQuit(quit);
        Thread mClient = new Thread(m);
        //Launch client thread
        mClient.start();

        //Wait for quit to be entered by runner
        while(!quit){
            if (scanman.nextLine().toLowerCase().contains("quit")){
                quit = true;
                MultiClientThread.setQuit(quit);
            }
        }
        //Close socket
        socket.close();
    }


    /**
     * Tests port for ephemeral status
     * @param portNum int
     */
    private static void testPort(Integer portNum) {
        if (portNum < EPHEMERAL_MIN || portNum > EPHEMERAL_MAX) {
            System.err.println("INCORRECT PORT NUMBER ARGUMENT");
            exit(-1);
        }
    }
}

/**
 * Multicast Client class to receive updates on topics
 * @version 1.0
 * @author David Beggs
 */
class MultiClientThread implements Runnable {
    private DatagramPacket receivePacket;   //Packet received from server
    private MulticastSocket socket; //socket created to receive the packet
    private static boolean quit;    //quit variable


    /**
     * Run method to execute thread logic
     */
    @Override
    public void run() {

        //Create response object
        Response message;
        //execute until user quits
        while(!quit){
            try{
                socket.receive(receivePacket);      //attempt to receive packet from server

                //Create response from received packet
                message = new Response(Arrays.copyOfRange(receivePacket.getData(), 0,
                        receivePacket.getLength()));

                //display response
                System.out.println(message.toString());

                //catch exceptions
            } catch(IOException e){
                if (!quit){
                    System.err.println("ERROR: INTERRUPTEDIO");
                }
            } catch (TopicException e){
                System.err.println("ERROR: TOPIC EXCEPTION - " + e.getErrorCode() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Sets member variable
     * @param receivePacket Datagram Packet
     */
    public void setReceivePacket(DatagramPacket receivePacket) {
        this.receivePacket = receivePacket;
    }

    /**
     * Sets member variable
     * @param socket MulticastSocket
     */
    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    /**
     * Sets static quit variable
     * @param quit boolean
     */
    public static void setQuit(boolean quit) {
        MultiClientThread.quit = quit;
    }
}