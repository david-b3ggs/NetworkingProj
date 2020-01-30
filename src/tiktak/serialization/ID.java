package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ID extends Message {

    String id = null;

    public ID(String s){
        this.id = s;
    }

    public String getID(){
        return this.id;
    }

    public ID setId(String id)throws ValidationException{

        if (!id.isEmpty() && !id.isBlank() && id.matches("[\\w]*(?!.\\W*)") ){
            this.id = id;
            return this;
        }
        else {
            throw new ValidationException("Nonce format error");
        }
    }

    @Override
    public String toString() {
        return "ID " + this.id + "\r\n";
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        out.getOut().write(this.toString().getBytes(StandardCharsets.ISO_8859_1));
    }
}
