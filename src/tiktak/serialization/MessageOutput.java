package tiktak.serialization;

import java.io.OutputStream;

public class MessageOutput {

    private OutputStream out;

    public MessageOutput(OutputStream out){
        this.out = out;
    }

    public OutputStream getOut() {
        return out;
    }

}
