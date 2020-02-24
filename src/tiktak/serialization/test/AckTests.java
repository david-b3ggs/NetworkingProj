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

@DisplayName("Ack Tests")
public class AckTests {
    private static final byte[] GOODINPUT = "ACK\r\n".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    @DisplayName("toString Test")
    void testToString(){
        Assertions.assertEquals("Ack", (new Ack ()).toString());
    }
    @Test
    @DisplayName("Equals Test")
    void equalsString(){
        Assertions.assertEquals(new Ack(), new Ack());
    }
    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString(){
        Ack a = new Ack();
        Ack b = new Ack();
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
            new Ack().encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new Ack()).encode(new MessageOutput(null)));
        }

        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new Ack()).encode(null));
        }

    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //based on test from provided example
            Ack ack = (Ack) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals("ACK", ack.getOperation());
        }



        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "ACK 123\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

    }


}