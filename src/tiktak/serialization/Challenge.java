package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Challenge extends Message {

    private String nonce = null;

    public String getNonce(){
        return this.nonce;
    }

    public Challenge setNonce(String nonce) throws ValidationException{

        if (!nonce.isEmpty() && !nonce.isBlank() && nonce.matches("(^[0-9]{0,}$)") ){
            this.nonce = nonce;
            return this;
        }
        else {
            throw new ValidationException("Nonce format error");
        }
    }

    public Challenge(String s){
        this.nonce = s;
    }

    @Override
    public String toString(){
        return "CLNG " + this.nonce + "\r\n";
    }


    @Override
    public void encode(MessageOutput out) throws IOException {
        out.getOut().write(this.toString().getBytes(StandardCharsets.ISO_8859_1));
    }
}
