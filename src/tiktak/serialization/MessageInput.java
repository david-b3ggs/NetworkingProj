package tiktak.serialization;

import java.io.InputStream;

public class MessageInput {

    private InputStream i;

    public MessageInput(InputStream in){
        i = in;
    }

    public InputStream getInStream() {
        return i;
    }
}
