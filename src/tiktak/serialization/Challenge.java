/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Challenge.java
 * Description: Challenge message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static tiktak.serialization.TikTakConstants.*;

/**
 * Challenge message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class Challenge extends Message {

    private String nonce;

    /**
     * Get nonce from object
     * @return nonce string from Challenge Object
     */
    public String getNonce(){
        return this.nonce;
    }

    /**
     * Verifies nonce structure and sets it
     * @param nonceArg Numeric String
     * @return Current Object
     * @throws ValidationException When nonce is incorrect format
     */
    public Challenge setNonce(String nonceArg) throws ValidationException{
        //throws exception if nonce is illegally formatted
        if (verifyNonce(nonceArg)){
            this.nonce = nonceArg;
        }

        return this;
    }

    /**
     * Creates challenge object with nonce argument set to object nonce.
     * @param nonce Numeric String
     * @throws ValidationException When nonce is incorrect format
     */
    public Challenge(String nonce) throws ValidationException {
        //calls super constructor for getOperation function
        super();

        //verifies nonce or throws exception
        if (verifyNonce(nonce)){
            this.nonce = nonce;
        }
    }

    /**
     * Verifies nonce is numeric
     * @param nonce Numeric String
     * @return boolean true for no exception thrown
     * @throws ValidationException when string is not strictly numeric
     */
    private boolean verifyNonce(String nonce) throws ValidationException{
        if (nonce == null || !nonce.matches(NUMERIC_REGEX) || nonce.isEmpty() || nonce.isBlank()){
            throw new ValidationException("NONCE FORMAT ERROR", nonce);
        }
        return true;
    }

    /**
     * Creates string out of Challenge object
     * @return String
     */
    @Override
    public String toString(){
        return "Challenge: nonce=" + this.nonce;
    }


    /**
     * Encodes current object to ISO_8859_1 charset to outputstream
     * @param out MessageOutput
     * @throws IOException Thrown if IO Problem
     * @throws NullPointerException Thrown if Stream is Null
     */
    @Override
    public void encode(MessageOutput out) throws IOException, NullPointerException {

        //check for null parameters and output stream then encode to ISO8859_1
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        String returnString = "CLNG " + this.getNonce() + "\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
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
        Challenge challenge = (Challenge) o;
        return Objects.equals(nonce, challenge.nonce);
    }

    /**
     * Generates Hash Code for Class
     * @return Hash code as 32 bit Integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(nonce);
    }
}
