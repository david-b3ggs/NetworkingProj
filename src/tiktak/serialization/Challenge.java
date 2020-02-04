package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Challenge extends Message {

    private String nonce = null;

    public String getNonce(){
        return this.nonce;
    }

    public Challenge setNonce(String nonceArg) throws ValidationException{

        if (nonceArg == null || nonceArg.matches("([^0-9]*)")){
            throw new ValidationException("Nonce format error");
        }

        if (!nonceArg.isEmpty() && !nonceArg.isBlank() && nonceArg.matches("(^[0-9]*$)") ){
            this.nonce = nonceArg;
            return this;
        }
        else {
            throw new ValidationException("NONCE FORMAT ERROR");
        }
    }

    public Challenge(String s) throws ValidationException, NullPointerException{
        super();

        if (s == null){
            throw new ValidationException("DO NOT SEND NULL STRINGS");
        }

        if (!s.isEmpty() && !s.isBlank() && s.matches("([0-9]*)") ){
            this.nonce = s;
        }
        else {
            throw new ValidationException("NONCE FORMAT ERROR");
        }
    }

    @Override
    public String toString(){
        return "Challenge: nonce=" + this.nonce;
    }


    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        String returnString = "CLNG " + this.getNonce() + "\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(nonce, challenge.nonce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonce);
    }
}
