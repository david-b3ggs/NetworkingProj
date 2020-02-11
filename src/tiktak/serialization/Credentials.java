/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Credentials.java
 * Description: Credentials message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static tiktak.serialization.TikTakConstants.*;

/**
 * Credentials message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class Credentials extends Message{

    private String hash;

    /**
     * Constructor
     * @param hash MD5 Hash String
     * @throws ValidationException Thrown when parameter is not MD%
     */
    public Credentials(String hash) throws ValidationException{
        super();
        hashValidation(hash);
        this.hash = hash;
    }

    /**
     * Converts Object to string
     * @return String from current object
     */
    @Override
    public String toString() {
        return "Credentials: hash=" + this.getHash();
    }

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

        String returnString = "CRED " + this.getHash() +"\r\n";
        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Returns MD5 Hash member variable
     * @return String of hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set Hash member variable to hash argument
     * @param hash MD5 Hash
     * @return This Object
     * @throws ValidationException Thrown when hash is Not MD5 Compatible
     */
    public Credentials setHash(String hash) throws ValidationException{
        if (hashValidation(hash)){
            this.hash = hash;
        }

        return this;
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
        Credentials that = (Credentials) o;
        return Objects.equals(hash, that.hash);
    }

    /**
     * Generates Hashcode for object
     * @return 32 bit hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    /**
     * Validates Hash is MD5
     * @param hash String of 32 Hex Chars
     * @return boolean true if MD5 Hash
     * @throws ValidationException when not MD5 hash
     */
    private boolean hashValidation(String hash) throws ValidationException {
        if (hash == null){
            throw new ValidationException("RECEIVED A NULL HASH", "Null Value");
        }

        if (hash.length() != 32){
            throw new ValidationException("HASH LENGTH INCORRECT FORMAT", hash);
        }

        if (!hash.matches(HEX_REGEX)){
            throw new ValidationException("HASH MUST CONTAIN ONLY HEX CHARACTERS", hash);
        }

        return true;
    }
}
