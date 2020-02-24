/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Message.java
 * Description: Abstract Message for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static tiktak.serialization.TikTakConstants.*;

/**
 * Message parent class for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public abstract class Message {

    private final HashMap<String, String> operationMap = new HashMap<>(); //Maps object type to get operation output

    /**
     * Constructor initializes getOperation Map
     */
    Message(){
        this.operationMap.put(CHALLENGE, CHALLENGE_GET_OP);
        this.operationMap.put(VERSION, VERSION_GET_OP);
        this.operationMap.put(ID, ID);
        this.operationMap.put(ACK, ACK);
        this.operationMap.put(TOST, TOST);
        this.operationMap.put(ERROR, ERROR);
        this.operationMap.put(CREDENTIALS, CREDENTIASL_GET_OP);
        this.operationMap.put(LTSRL, LTSRL);
    }

    /**
     * Decodes messages from ISO_8859_1 charset and generates respective object
     * @param in MessageInput Stream
     * @return Message object type according to message
     * @throws IOException Thrown if IO Problem
     * @throws NullPointerException Thrown if stream is null
     * @throws ValidationException thrown if grammar is violated
     */
    public static Message decode(MessageInput in) throws IOException, NullPointerException, ValidationException {

        Objects.requireNonNull(in, "NULL MESSAGE INPUT DETECTED");

        Scanner s = new Scanner(in.getInStream(), StandardCharsets.ISO_8859_1); //create a scanner for non blocking read
        s.useDelimiter(SCANNER_DELIMITER);

        //throws exception when stream ends before delimiter
        if (!s.hasNext()){
            throw new EOFException("PREMATURE END OF STREAM REACHED");
        }

        String message = s.next();  //read next message
        //create return value based on checking messages with only one or zero parameters
        Message ret = checkSimpleMessages(message);

        //return if message found
        if (ret != null){
            return ret;
        }

        //set return value based on checking messages with greater than 1 parameter
        ret = checkLongMessages(message);
        //return if message found
        if (ret != null){
            return ret;
        }

        //Throw exception for end of stream if message is not coherant
        throw new ValidationException("INCORRECT MESSAGE FORMAT NO MESSAGE DETECTED", message);
    }

    /**
     * Checks for simple 0 or 1 argument objects in message
     * @param message String
     * @return Message according to input message
     * @throws ValidationException thrown if grammar is broken
     * @throws EOFException Thrown if stream is interrupted
     */
    private static Message checkSimpleMessages(String message) throws ValidationException, EOFException {
        //check message for version syntax
        if (message.startsWith(VERSION_START)){

            //determine if version message
            if (message.equals(VERSION_MESSAGE)){
                return new Version();
            }
            else {
                throw new EOFException("Invalid Message");
            }
        }
        else if (message.equals(ACK_MESSAGE)){  //check message for ack syntax
            return new Ack();
        }
        else if (message.equals(TOST_MESSAGE)){ //check message for tost syntax
            return new Tost();
        }
        else if (message.endsWith(SPACE) || message.endsWith(SPACE_MESSAGE_DELIM) ||
                !message.endsWith(MESSAGE_DELIM)){  //check if message ends with extrenuous spaces or no delimiter

            throw new EOFException("PREMATURE END OF STREAM");
        }
        else {
            String m = message.substring(0, message.length() - 2);  //remove the delimiter to make regex simpler

            if(m.matches(ID_REGEX)){    //check for id syntax
                return new ID(m.split(SPACE)[1]);   //return id
            }
            else if (m.matches(CHALLENGE_REGEX)){   //check for challenge syntax
                return new Challenge(m.split(SPACE)[1]);    //create new challenge
            }
            else if (m.matches(CREDENTIALS_REGEX)){ //check for credentials syntax
                String [] array = m.split(SPACE);   //split the message into components for constructor
                if(array[1].length() == 32){        //ensure valid credentail hash
                    return new Credentials(array[1]);
                }
            }
        }
        return null;
    }

    /**
     * Checks 2 argument messages
     * @param message String
     * @return Message according to input string
     * @throws ValidationException thrown if grammar is broken
     */
    private static Message checkLongMessages(String message) throws ValidationException, EOFException {

        if (message.contains(ERROR_START)){     //Detect error message syntax
            message = message.substring(0, message.length() - 2);   //remove delimiter to make regex easier

            String [] components = message.split(SPACE);            //split message into components
            ArrayList<String> retList = new ArrayList<>();          //place components into arraylist for added functionality

            if ((components.length == 2 && !message.endsWith(SPACE)) || message.startsWith(SPACE)) {
                throw new EOFException("INCORRECT ERROR MESSAGE FORMAT");
            }

            for(int i = 2; i < components.length; i++){             //place componenets into array list
                retList.add(components[i]);
            }

            if (!components[1].matches(NUMERIC_REGEX)){             //Test error code syntax
                throw new ValidationException("INCORRECT ERROR CODE FORMAT", components[1]);
            }

            if (!components[2].matches(ALPHANUMERIC_OR_WHITESPACE_REGEX)){ //test error message
                throw new ValidationException("INCORRECT ERROR MESSAGE FIELD", components[2]);
            }

            Integer errorCode = Integer.parseInt(components[1]);        //extract error code

            return new Error(errorCode, String.join(" ", retList)); //retrun new error message
        }
        else if (message.startsWith(LTSRL_START)){  //Detect ltsrl syntax
            message = message.substring(0, message.length() - 2);   //remove delimiter to make regex easier

            String [] components = message.split(SPACE);    //split message into components

            if (components.length != 3){    //tests message length to avoid indexoutofbounds exception
                throw new ValidationException("INCORRECT MESSAGE FIELDS", message);
            }

            //test category syntax
            if (!components[1].matches(ZERO_OR_MORE_ALPHANUMERIC_REGEX) || components[1].isBlank() || components[1].isEmpty()){
                throw new ValidationException("INCORRECT CATEGORY DEFINITION", message);
            }

            //test image syntax
            if (!components[2].matches(BASE_64_COMPATABLE_REGEX)){
                throw new ValidationException("NOT BASE64 ENCODED IMAGE", message);
            }

            //return new obj
            return new LtsRL(components[1], Base64.getDecoder().decode(components[2]));

        }

        return null;
    }

    /**
     * Encode must be overridden by child classes to convert charsets from input stream
     * @param out MessageOutput
     * @throws IOException Thrown if Stream IO problem
     */
    public abstract void encode(MessageOutput out) throws IOException;

    /**
     * Returns the name of the current operation
     * @return String
     */
    public String getOperation(){
        return this.operationMap.get(this.getClass().getName().toUpperCase().split(DOT_DELIMIT)[2]);
    }

}
