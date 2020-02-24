/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Tost.java
 * Description: Tost message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import static tiktak.serialization.TikTakConstants.*;

/**
 * Tost message subclass for tiktak application protocol
 * @version  1.0
 * @author David Beggs
 */
public class Tost extends Message {
    /**
     * Encodes current object to ISO_8859_1 charset to outputstream
     * @param out MessageOutput
     * @throws IOException Thrown if IO Problem
     * @throws NullPointerException Thrown if Stream is Null
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        out.getOut().write("TOST\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Generates String of object
     * @return String
     */
    @Override
    public String toString() {
        return "Tost";
    }

    /**
     * Generates hash code for individual instances
     * @return hashed integer
     */
    @Override
    public int hashCode() {
        return HASH_ACK_TOST;
    }

    /**
     * Tests equivalence for Tost Objects
     * @param o other Ack Object
     * @return whether two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {return false;}
        else {
            return true;
        }
    }
}
