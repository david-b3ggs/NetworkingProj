package tiktak.app.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static java.lang.System.exit;
import static tiktak.serialization.TikTakConstants.*;

/**
 * Server class hosts server for tiktak protocol
 * @version 1.0
 * @author David Beggs
 */
public class Server {

    /**
     * Main Server method
     * @param args string arguments to the program
     * @throws IOException Thrown if port issue found on start up
     */
    public static void main(String[] args) throws IOException {
        Logger LOGGER = Logger.getLogger("LOGGER.log");     //initialize logger
        Handler LUMBERMAN;                                  //initiailize file handler

        try {
            LOGGER.setUseParentHandlers(false); //removes console logging for info level

            //sets file handler, and human readable format
            LUMBERMAN = new FileHandler("connections.log",true);
            LUMBERMAN.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(LUMBERMAN);
        } catch (IOException e){
            LOGGER.log(Level.WARNING, "ERROR: COULD NOT INITIALIZE LOGGER");
        }

        if (args.length != SERVER_ARG_COUNT) {      //test for argument length
            LOGGER.log(Level.SEVERE,"Unable to start: Incorrect argument count");
            exit(-1);
        }
                            //initialize port and threadcount
        int portNumber = 0;
        int threadCount = 0;

        try {               //attempt to parse args
            portNumber = Integer.parseInt(args[0]);
            threadCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE,"Unable to start: port number or thread count is not readable");
            exit(-1);
        }
                            //make sure port is ephemeral
        if (portNumber < EPHEMERAL_MIN && portNumber > EPHEMERAL_MAX) {
            LOGGER.log(Level.SEVERE,"Unable to start: port number is not ephemeral");
            exit(-1);
        }
                            //make sure thread count is non negative
        if (threadCount < 1) {
            LOGGER.log(Level.SEVERE,"Unable to start: thread count must be greater or equal to 1");
            exit(-1);
        }
                            //initialize args for threads
        File passFile = new File(args[2]);
        Scanner scanMan = null;

        Map<String, Integer> tostMap = new HashMap<>();
        Map<String, String> userData = new HashMap<>();
        CopyOnWriteArrayList<String> archive = new CopyOnWriteArrayList<>();


        try {               //create scanner to parse passwordFile
            scanMan = new Scanner(new FileInputStream(passFile), StandardCharsets.ISO_8859_1);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,"Unable to start: could not find file");
            exit(-1);
        }
                            //Parse password file
        if (!parsePassFile(scanMan, userData)){
            LOGGER.log(Level.SEVERE, "Unable to start: password file format error");
            exit(-1);
        }
                            //Create threadpool
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount + 1);

        //initialize udp thread and execute continuously
        topic.app.server.Server udpServer = new topic.app.server.Server(archive, portNumber);
        threadPool.execute(udpServer);

        ServerSocket sock = null;       //initialize socket

        try {
            sock = new ServerSocket(portNumber);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Unable to start: port number issue");
            exit(-1);
        }
                            //while server is running accept client connections
        while (true) {
            Socket client = null;
            try {
                client = sock.accept();     //accept incoming client connection

                client.setSoTimeout(MAX_TIMEOUT);   //set 20s timeout

                LOGGER.log(Level.INFO, "New client at : " + client.getLocalAddress().toString() + " : " +
                        client.getLocalPort());

                //Create thread and pass relevant data
                ClientHandler currHandler = new ClientHandler();
                currHandler.setClientSocket(client);
                currHandler.setUserMap(userData);
                currHandler.setTostMap(tostMap);
                currHandler.setArchive(archive);
                currHandler.setLOGGER(LOGGER);
                //execute thread
                threadPool.execute(currHandler);

            } catch (IOException e){
                LOGGER.log(Level.WARNING, "Unable to communicate: could not maintain connection" +
                        "Error in Thread:");
                client.close();
            }
        }

    }

    /**
     * Parses password file to see if correct format
     * @param scanMan Scanner object to pull line by line
     * @param userData Hash map of User to password key value pairs
     * @return boolean if file is in correct format
     */
    public static boolean parsePassFile(Scanner scanMan, Map<String, String> userData){
        scanMan.useDelimiter(SCANNER_DELIMITER);        //Create inclusive \r\n scan
        String currentLine, pass, ID;
        boolean ret = true;
        while (scanMan.hasNext()) {                     //scan entire file for correct format
            currentLine = scanMan.next();

            ID = currentLine.substring(0, currentLine.indexOf(':'));
            pass = currentLine.substring(currentLine.indexOf(':') + 1);
            pass = pass.substring(0, pass.length() - DELIMITER_COUNT);
                                                        //check format of id and password
            if (!ID.matches(ID_RAW) || !pass.matches(ZERO_OR_MORE_ALPHANUMERIC_REGEX)) {
                ret = false;
                break;
            } else {
                userData.put(ID, pass);
            }
        }

        return ret;
    }


}
