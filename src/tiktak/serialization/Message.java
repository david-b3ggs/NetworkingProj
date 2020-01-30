package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class Message {

    public static Message decode(MessageInput in) throws IOException, NullPointerException, ValidationException {


        //parse string to determine what subclass to pass
        byte [] messageBytes = in.getInStream().readAllBytes();
        String message = new String(messageBytes, StandardCharsets.ISO_8859_1);
        String sub = message.substring(0, message.length() - 2);

        if (message.startsWith("TIKTAK ")){
            if (message.equals("TIKTAK 1.0\r\n")){
                return new Version();
            }
            else {
                throw new ValidationException("Invalid Message");
            }
        }
        else if (message.endsWith(" ") || message.endsWith(" \r\n") || !message.endsWith("\r\n")){
            throw new ValidationException("Invalid Message");
        }
        else {
            if(sub.matches("(ID [0-9a-zA-Z]*)")){
                return new ID(message.split(" ")[1]);
            }
            else if (sub.matches("(CLNG [0-9]*)")){
                return new Challenge(message.split(" ")[1]);
            }
            else {
                throw new ValidationException("Invalid Message");
            }
        }
    }

    public abstract void encode(MessageOutput out) throws IOException;

    public String getOperation(){
        String out = this.getClass().getName().toUpperCase();
        String[] array = out.split("[.]");
        return array[array.length - 1];
    }

}
