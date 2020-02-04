package tiktak.serialization;

import java.io.InputStream;

public class MessageInput {

    private InputStream i;

    public MessageInput(InputStream in){
        if (in == null){
            throw new NullPointerException("NULL INPUT STREAM");
        }
        else {
            i = in;
        }
    }

    public InputStream getInStream() {
        return i;
    }
}
