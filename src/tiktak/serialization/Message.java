package tiktak.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            else {
                throw new ValidationException("Invalid Message", message);
            }
        }
    }

    public abstract void encode(MessageOutput out) throws IOException;

    public String getOperation(){
        String out = this.getClass().getName().toUpperCase();
        String[] array = out.split("[.]");
        return this.operationMap.get(array[array.length - 1]);
    }

}
