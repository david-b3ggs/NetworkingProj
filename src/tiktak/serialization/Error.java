/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Error.java
 * Description: Error message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static tiktak.serialization.TikTakConstants.*;

/**
 * Error message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class Error extends Message {

    private int code;       //3 digit error code starting with 1
    private String message; //Message of error descriptions

    /**
     * Encodes current object to ISO_8859_1 charset to outputstream
     * @param out MessageOutput
     * @throws IOException Thrown if IO Problem
     * @throws NullPointerException Thrown if Stream is Null
     */
    @Override
    public void encode(MessageOutput out) throws IOException, NullPointerException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        String returnString = "ERROR " + this.getCode()+ " " + this.getMessage() + "\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Constructor
     * @param code number from 100 - 999
     * @param message String of spaces or alphanumeric characters
     * @throws ValidationException thrown when number or message violates grammer
     */
    public Error(int code, String message) throws ValidationException{
        super();
        verifyCode(code);
        verifyMessage(message);

        this.message = message;
        this.code = code;
    }


    /**
     * Converts object to string
     * @return String
     */
    @Override
    public String toString() {
        return "Error: code=" + code +  " message=" + message;
    }

    /**
     * Finds equality in objects
     * @param o Object
     * @return boolean if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return code == error.code &&
                Objects.equals(message, error.message);
    }

    /**
     * Generates hash code out of current object
     * @return 32 bit hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    /**
     * returns error code member variable
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets member variable code to argument code
     * @param code int
     * @return This current object
     * @throws ValidationException Thrown when code is not between 99 and 1000
     */
    public Error setCode(int code) throws ValidationException {

        if (verifyCode(code)){
            this.code = code;
        }

        return this;
    }

    /**
     * Returns Member variable message string
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets member string to argument string
     * @param message String
     * @return This current object
     * @throws ValidationException When grammar is violated
     */
    public Error setMessage(String message) throws ValidationException {
        if (verifyMessage(message)){
            this.message = message;
        }

        return this;
    }

    /**
     * Verifies Code is within 99 and 1000
     * @param code int
     * @return boolean true if no exception thrown
     * @throws ValidationException Thrown if grammer is violated
     */
    private boolean verifyCode(int code) throws ValidationException{
        if (code <= ERROR_CODE_MINIMUM || code >=ERROR_CODE_MAXIMUM){
            throw new ValidationException("INVALID CODE FORMAT", Integer.toString(code));
        }
        return true;
    }

    /**
     * Verifies Message arguments are space or alphanumeric
     * @param message String
     * @return boolean true if no exception thrown
     * @throws ValidationException thrown if grammar is violated
     */
    private boolean verifyMessage(String message) throws ValidationException {
        if (message == null){
            throw new ValidationException("NULL MESSAGE SENT TO ERROR", "Null String");
        }

        if (message.isEmpty() || !message.matches(MESSAGE_REGEX)){
            throw new ValidationException("NO SPECIAL CHARACTERS IN MESSAGE", message);
        }

        return true;
    }
}
