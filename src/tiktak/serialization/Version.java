/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: Version.java
 * Description: Version message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import static tiktak.serialization.TikTakConstants.*;

/**
 * Version message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class Version extends Message {

    private final String version = VERSION_NUMBER;      //current protocol version number

    public Version(){
        super();
    }

    @Override
    public String toString() {
        return "TikTak";
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

        String returnString = "TIKTAK 1.0" + "\r\n";
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
        return true;
    }

    /**
     * Generate Hash code of current object
     * @return 32 bit hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}
