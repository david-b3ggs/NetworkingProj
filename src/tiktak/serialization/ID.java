/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: ID.java
 * Description: ID message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static tiktak.serialization.TikTakConstants.*;

/**
 * ID message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class ID extends Message {

    private String id;  //Id for client identification

    /**
     * Constructor
     * @param id AlphaNumeric String
     * @throws ValidationException thrown if grammar is violated
     */
    public ID(String id) throws ValidationException{
        super();

        if (verifyID(id)){
            this.id = id;
        }
    }

    /**
     * returns current ID member variable
     * @return String
     */
    public String getID(){
        return this.id;
    }

    /**
     * Sets member variable id to argument variable
     * @param id Alphanumeric String
     * @return This object
     * @throws ValidationException thrown if grammar is violated
     */
    public ID setID(String id)throws ValidationException{
        if (verifyID(id)){
            this.id = id;
        }

        return this;
    }

    /**
     * Verifies argument is alphanumeric
     * @param id String
     * @return boolean true if exception not thrown
     * @throws ValidationException Thrown if grammer is violated
     */
    private boolean verifyID(String id) throws ValidationException{
        if (id == null){
            throw  new ValidationException("DO NOT SEND NULL STRINGS", "STRING WAS NULL");
        }

        if (id.isEmpty() || id.isBlank() || !id.matches(ZERO_OR_MORE_ALPHANUMERIC_REGEX) ){
            throw new ValidationException("ID FORMAT ERROR", id);
        }

        return true;
    }

    /**
     * Generates 32 bit hash out of current object
     * @return 32 bit hash
     */
    @Override
    public String toString() {
        return "ID: id=" + this.id;
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

        String returnString = "ID " + this.id + "\r\n";
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
        ID id1 = (ID) o;
        return Objects.equals(id, id1.id);
    }

    /**
     * Generates Hashcode from current instance
     * @return 32 bit hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
