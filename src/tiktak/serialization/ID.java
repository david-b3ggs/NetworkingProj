package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ID extends Message {

    String id = null;

    public ID(String s) throws ValidationException{
        super();
        Objects.requireNonNull(s, "DO NOT SEND NULL STRINGS");

        if (!s.isEmpty() && !s.isBlank() && s.matches("([0-9a-zA-Z]*)") ){
            this.id = s;
        }
        else {
            throw new ValidationException("NONCE FORMAT ERROR");
        }
    }

    public String getID(){
        return this.id;
    }

    public ID setID(String id)throws ValidationException{

        if (id == null){
            throw new ValidationException("NONCE FORMAT ERROR");
        }

        if (!id.isEmpty() && !id.isBlank() && id.matches("([0-9a-zA-Z]*)") ){
            this.id = id;
            return this;
        }
        else {
            throw new ValidationException("NONCE FORMAT ERROR");
        }
    }

    @Override
    public String toString() {
        return "ID: id=" + this.id;
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        String returnString = "ID " + this.id + "\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id1 = (ID) o;
        return Objects.equals(id, id1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
