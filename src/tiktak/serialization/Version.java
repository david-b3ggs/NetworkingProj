package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Version extends Message {

    private final String version = "1.0";

    @Override
    public String toString() {
        return "TIKTAK " + version + "\r\n";
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        out.getOut().write(this.toString().getBytes(StandardCharsets.ISO_8859_1));
    }
}
