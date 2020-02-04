package tiktak.serialization;

import java.io.OutputStream;

public class MessageOutput {

    private OutputStream out;

    public MessageOutput(OutputStream out){
        if (out == null){
            throw new NullPointerException("NULL OUTPUT STREAM");
        }
        else {
            this.out = out;
        }
    }

    public OutputStream getOut() {
        return out;
    }

}
