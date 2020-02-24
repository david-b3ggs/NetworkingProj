/********************************
 *
 * Author: Bob Rein
 * Assignment: Program 1
 * Class: CSI 4321 (Networking)
 *
 *******************************/

package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.*;


import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tost Tests")
public class TostTests {
    private static final byte[] GOODINPUT = "TOST\r\n".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    @DisplayName("toString Test")
    void testToString(){
        Assertions.assertEquals("Tost", (new Tost ()).toString());
    }
    @Test
    @DisplayName("Equals Test")
    void equalsString(){
        Assertions.assertEquals(new Tost(), new Tost());
    }
    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString(){
        Tost a = new Tost();
        Tost b = new Tost();
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }


    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests{

        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new Tost().encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new Tost()).encode(new MessageOutput(null)));
        }

        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new Tost()).encode(null));
        }

    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //based on test from provided example
            Tost tost = (Tost) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals("TOST", tost.getOperation());
        }



        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "TOST 123\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

    }


}