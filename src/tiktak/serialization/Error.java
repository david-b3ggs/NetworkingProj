package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Error extends Message {

    private int code;
    private String message;

    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        String returnString = "ERROR code=" + this.getCode() + " message=" + this.message + "\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    public Error(int code, String message) throws ValidationException{
        super();
        verifyCode(code);
        verifyMessage(message);

        this.message = message;
        this.code = code;
    }


    @Override
    public String toString() {
        return "Error: code=" + code +  " message=" + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return code == error.code &&
                Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    public int getCode() {
        return code;
    }

    public Error setCode(int code) throws ValidationException {
        if (code <= 99 || code >= 1000){
            Integer badCode = code;
            throw new ValidationException("INVALID CODE FORMAT", badCode.toString());
        }
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Error setMessage(String message) throws ValidationException {


        return this;
    }

    private void verifyCode(int code) throws ValidationException{
        if (code <= 99 || code >= 1000){
            Integer badCode = code;
            throw new ValidationException("INVALID CODE FORMAT", badCode.toString());
        }
    }

    private void verifyMessage(String message) throws ValidationException {
        if (message == null){
            throw new ValidationException("NULL MESSAGE SENT TO ERROR", "Null String");
        }

        if (message.isEmpty() || message.matches("([0-9a-zA-Z]*)")){
            throw new ValidationException("NO SPECIAL CHARACTERS IN MESSAGE", message);
        }
    }
}
