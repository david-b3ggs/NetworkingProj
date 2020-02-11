package tiktak.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Message {

    private final HashMap<String, String> operationMap = new HashMap<>();

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

    public static Message decode(MessageInput in) throws IOException, NullPointerException, ValidationException {

        if (in == null){
            throw new NullPointerException("NULL MESSAGE INPUT DETECTED");
        }

        Scanner s = new Scanner(in.getInStream(), StandardCharsets.ISO_8859_1);
        s.useDelimiter("(?<=\\r\\n)");

        if (!s.hasNext()){
            throw new EOFException("PREMATURE END OF STREAM REACHED");
        }

        String message = s.next();

        if (message.startsWith("TIKTAK ")){
            if (message.equals("TIKTAK 1.0\r\n")){
                return new Version();
            }
            else {
                throw new EOFException("Invalid Message");
            }
        }
        else if (message.equals("ACK\r\n")){
            return new Ack();
        }
        else if (message.equals("TOST\r\n")){
            return new Tost();
        }
        else if (message.endsWith(" ") || message.endsWith(" \r\n") || !message.endsWith("\r\n")){
            throw new EOFException("PREMATURE END OF STREAM");
        }
        else {
            message = message.substring(0, message.length() - 2);

            if(message.matches("(ID [0-9a-zA-Z]*)")){
                return new ID(message.split(" ")[1]);
            }
            else if (message.matches("(CLNG [0-9]*)")){
                return new Challenge(message.split(" ")[1]);
            }
            else if (message.matches("CRED [0-9a-fA-F]*")){
                if(message.length() == 32){
                    return new Credentials(message.split(" ")[1]);
                }
            }
            else {
                throw new ValidationException("Invalid Message", message);
            }
        }

        if (message.startsWith("ERROR ")){
            message = message.substring(0, message.length() - 2);

            String [] components = message.split(" ");

            if (components.length != 3){
                throw new ValidationException("INCORRECT MESSAGE FORMAT", message);
            }

            Integer errorCode = Integer.parseInt(components[1]);

            if (errorCode < 100 || errorCode > 999 ){
                throw new ValidationException("ERROR CODE NOT BETWEEN 99 AND 1000", message);
            }
            if (components[2].substring(0, components[2].length() - 1).matches("([0-9a-zA-Z]*\\s)")){
                return new Error(errorCode, components[2]);
            }
        }
        else if (message.startsWith("LTSRL ")){
            message = message.substring(0, message.length() - 2);

            String [] components = message.split(" ");

            if (!components[1].matches("([0-9a-zA-Z]*)")){
                throw new ValidationException("INCORRECT CATEGORY DEFINITION", message);
            }

            if (!components[2].matches("([0-9a-zA-Z]*\\+*/*)")){
                throw new ValidationException("NOT BASE64 ENCODED IMAGE", message);
            }

            return new LtsRL(components[1], Base64.getDecoder().decode(components[2].substring(0,
                    components[2].length() - 2)));

        }

        throw new ValidationException("INCORRECT MESSAGE FORMAT", message);
    }

    public abstract void encode(MessageOutput out) throws IOException;

    public String getOperation(){
        String out = this.getClass().getName().toUpperCase();
        String[] array = out.split("[.]");
        return this.operationMap.get(array[array.length - 1]);
    }

}
