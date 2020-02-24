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

@DisplayName("Version Tests")
public class VersionTests {
    private static final byte[] GOODINPUT = "TIKTAK 1.0\r\n".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    @DisplayName("toString Test")
    void testToString(){
        Assertions.assertEquals("TikTak", (new Version ()).toString());
    }
    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new Version (),  new Version ());
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        Version a =  new Version ();
        Version b =  new Version ();
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
            new Version().encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new Version()).encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new Version()).encode(null));
        }
    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //based on test from provided example
            Version version = (Version) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals("TIKTAK", version.getOperation());
        }


        @Test
        @DisplayName("Whole Number")
        void testWholeNumber(){
            String input = "TIKTAK 1\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(EOFException.class, () ->Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Missing Decimal Point")
        void testMissingDecimalPoint(){
            String input = "TIKTAK 10\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(EOFException.class, () ->Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "TikTak 1.0\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

    }


}