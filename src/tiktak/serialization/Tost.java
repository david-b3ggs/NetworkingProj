package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Tost extends Message {
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        out.getOut().write("TOST\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public String toString() {
        return "TOST";
    }
}
