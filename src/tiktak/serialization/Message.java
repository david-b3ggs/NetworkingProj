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
import java.util.Base64;
import java.util.HashMap;
import static tiktak.serialization.TikTakConstants.*;

import java.util.Scanner;

/**
 * Message parent class for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public abstract class Message {

    private final HashMap<String, String> operationMap = new HashMap<>();

    /**
     * Constructor initializes getOperation Map
     */
    Message(){
        this.operationMap.put("CHALLENGE", "CLNG");
        this.operationMap.put("VERSION", "TIKTAK");
        this.operationMap.put("ID", "ID");
        this.operationMap.put("ACK", "ACK");
        this.operationMap.put("TOST", "TOST");
        this.operationMap.put("ERROR", "ERROR");
        this.operationMap.put("CREDENTIALS", "CRED");
        this.operationMap.put("LTSRL", "LTSRL");
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

        if (in == null){
            throw new NullPointerException("NULL MESSAGE INPUT DETECTED");
        }

        Scanner s = new Scanner(in.getInStream(), StandardCharsets.ISO_8859_1);
        s.useDelimiter(SCANNER_DELIMITER);

        if (!s.hasNext()){
            throw new EOFException("PREMATURE END OF STREAM REACHED");
        }

        String message = s.next();
        Message ret = checkSimpleMessages(message);

        if (ret == null){
            ret = checkLongMessages(message);
            if (ret == null){
                throw new ValidationException("INCORRECT MESSAGE FORMAT", message);
            }
            else {
                return ret;
            }
        }
        else {
            return ret;
        }
    }

    /**
     * Checks for simple 0 or 1 argument objects in message
     * @param message String
     * @return Message according to input message
     * @throws ValidationException thrown if grammar is broken
     * @throws EOFException Thrown if stream is interrupted
     */
    private static Message checkSimpleMessages(String message) throws ValidationException, EOFException {
        if (message.startsWith(VERSION_START)){
            if (message.equals(VERSION_MESSAGE)){
                return new Version();
            }
            else {
                throw new EOFException("Invalid Message");
            }
        }
        else if (message.equals(ACK_MESSAGE)){
            return new Ack();
        }
        else if (message.equals(TOST_MESSAGE)){
            return new Tost();
        }
        else if (message.endsWith(SPACE) || message.endsWith(SPACE_MESSAGE_DELIM) ||
                !message.endsWith(MESSAGE_DELIM)){

            throw new EOFException("PREMATURE END OF STREAM");
        }
        else {
            message = message.substring(0, message.length() - 2);

            if(message.matches(ID_REGEX)){
                return new ID(message.split(SPACE)[1]);
            }
            else if (message.matches(CHALLENGE_REGEX)){
                return new Challenge(message.split(SPACE)[1]);
            }
            else if (message.matches(CREDENTIALS_REGEX)){
                if(message.length() == 32){
                    return new Credentials(message.split(SPACE)[1]);
                }
            }
            else {
                throw new ValidationException("Invalid Message", message);
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
    private static Message checkLongMessages(String message) throws ValidationException {
        if (message.startsWith(ERROR_START)){
            message = message.substring(0, message.length() - 2);

            String [] components = message.split(SPACE);

            if (components.length != EXPECTED_MESSAGE_SPLIT){
                throw new ValidationException("INCORRECT MESSAGE FORMAT", message);
            }

            Integer errorCode = Integer.parseInt(components[1]);

            if (errorCode <= ERROR_CODE_MINIMUM || errorCode >= ERROR_CODE_MAXIMUM ){
                throw new ValidationException("ERROR CODE NOT BETWEEN 99 AND 1000", message);
            }
            if (components[2].substring(0, components[2].length() - 1).matches(ALPHANUMERIC_OR_WHITESPACE_REGEX)){
                return new Error(errorCode, components[2]);
            }
        }
        else if (message.startsWith(LTSRL_START)){
            message = message.substring(0, message.length() - 2);

            String [] components = message.split(SPACE);

            if (!components[1].matches(ZERO_OR_MORE_ALPHANUMERIC_REGEX) || components[1].isBlank() || components[1].isEmpty()){
                throw new ValidationException("INCORRECT CATEGORY DEFINITION", message);
            }

            if (!components[2].matches(BASE_64_COMPATABLE_REGEX)){
                throw new ValidationException("NOT BASE64 ENCODED IMAGE", message);
            }

            return new LtsRL(components[1], Base64.getDecoder().decode(components[2].substring(0,
                    components[2].length() - 2)));

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
