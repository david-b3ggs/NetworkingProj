package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Credentials extends Message{

    private String hash;

    public Credentials(String hash) throws ValidationException{
        super();
        hashValidation(hash);
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Credentials: hash=" + this.getHash();
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        String returnString = "CRED " + this.getHash() +"\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    public String getHash() {
        return hash;
    }

    public Credentials setHash(String hash) throws ValidationException{
        hashValidation(hash);
        this.hash = hash;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    void hashValidation(String hash) throws ValidationException {
        if (hash == null){
            throw new ValidationException("RECEIVED A NULL HASH", "Null Value");
        }

        if (hash.length() != 32){
            throw new ValidationException("HASH LENGTH INCORRECT FORMAT", hash);
        }

        if (!hash.matches("([0-9a-fA-F]*)")){
            throw new ValidationException("HASH MUST CONTAIN ONLY HEX CHARACTERS", hash);
        }
    }
}
