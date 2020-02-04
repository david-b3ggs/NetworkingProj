package tiktak.serialization;

import java.io.IOException;

public class Tost extends Message {
    @Override
    public void encode(MessageOutput out) throws IOException {

    }

    @Override
    public String toString() {
        return "TOST";
    }
}
