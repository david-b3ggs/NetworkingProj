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

@DisplayName("Credentials Tests")
class CredentialsTests {
    //from provided example
    private static final byte[] GOODINPUT = "CRED 000102030405060708090A0B0C0D0E0F\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final String hash = "000102030405060708090A0B0C0D0E0F";


    @Test
    @DisplayName("toString Test")
    void testToString() throws ValidationException {
        assertEquals("Credentials: hash="+hash, (new Credentials(hash)).toString());
    }

    @Test
    @DisplayName("getOperation Test")
    void testGetOperation() throws ValidationException {
        assertEquals("CRED", (new Credentials(hash)).getOperation());
    }

    @Test
    @DisplayName("Get Hash")
    void testGetHash() throws ValidationException {
        assertEquals(hash, new Credentials(hash).getHash());
    }

    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new Credentials(hash),  new Credentials(hash));
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        Credentials a =  new Credentials(hash);
        Credentials b =  new Credentials(hash);
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }
    @Nested
    @DisplayName("Set Hash Tests")
    class SetHashTests{
        @Test
        @DisplayName("Set Hash Valid")
        void testSetHashValid(){
            assertDoesNotThrow(() ->  (new Credentials(hash)).setHash(hash));
        }

        @Test
        @DisplayName("Set Hash Too Long")
        void testSetHashTooLong(){
            String testHash = "000102030405060708090A0B0C0D0E0F0000";
            assertThrows(ValidationException.class, () ->  (new Credentials(hash)).setHash(testHash));
        }

        @Test
        @DisplayName("Set Hash Too Short")
        void testSetHashTooShort(){
            String testHash = "0001020304050607";
            assertThrows(ValidationException.class, () -> (new Credentials(hash)).setHash(testHash));
        }

        @Test
        @DisplayName("Set Hash Non Hex")
        void testSetHashNonHex(){
            String testHash = "XYZ102030405060708090A0B0C0D0E0F";
            assertThrows(ValidationException.class, () -> (new Credentials(hash)).setHash(testHash));
        }

        @Test
        @DisplayName("Set Hash Null")
        void testSetHashNull(){
            String testHash = null;
            assertThrows(ValidationException.class, () -> (new Credentials(hash)).setHash(testHash));
        }

        @Test
        @DisplayName("Set Hash Blank")
        void testSetHashBlank(){
            String testHash = "";
            assertThrows(ValidationException.class, () -> (new Credentials(hash)).setHash(testHash));
        }
    }


    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests{
        @Test
        @DisplayName("Hash Valid")
        void testConstructorHashValid(){
            assertDoesNotThrow(() ->  new Credentials(hash));
        }

        @Test
        @DisplayName("Hash Too Long")
        void testConstructorHashTooLong(){
            String testHash = "000102030405060708090A0B0C0D0E0F0000";
            assertThrows(ValidationException.class, () ->  new Credentials(testHash));
        }

        @Test
        @DisplayName("Hash Too Short")
        void testConstructorHashTooShort(){
            String testHash = "0001020304050607";
            assertThrows(ValidationException.class, () ->  new Credentials(testHash));        }

        @Test
        @DisplayName("Hash Non Hex")
        void testConstructorHashNonHex(){
            String testHash = "XYZ102030405060708090A0B0C0D0E0F";
            assertThrows(ValidationException.class, () ->  new Credentials(testHash));        }

        @Test
        @DisplayName("Hash Null")
        void testConstructorHashNull(){
            String testHash = null;
            assertThrows(ValidationException.class, () ->  new Credentials(testHash));        }

        @Test
        @DisplayName("Hash Blank")
        void testConstructorHashBlank(){
            String testHash = "";
            assertThrows(ValidationException.class, () ->  new Credentials(testHash));        }
    }

    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests{

        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new Credentials (hash).encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new Credentials(hash)).encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new Credentials(hash)).encode(null));
        }



    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            Credentials credentials = (Credentials) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals(hash, credentials.getHash());
            assertEquals("CRED", credentials.getOperation());
        }

        @Test
        @DisplayName("Hash Too Short")
        void testHashTooShort(){
            String input = "CRED 0001020304050607\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Hash Too Long")
        void testHashTooLong(){
            String input = "CRED 000102030405060708090A0B0C0D0E0F0000\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Hash Non Hex")
        void testHashNonHex(){
            String input = "CRED XYZ102030405060708090A0B0C0D0E0F\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Blank Hash")
        void testBlankHash(){
            String input = "CRED \r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Starts With Whitespace")
        void testStartWithWhitespace(){
            String input = " CRED "+ hash + "\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Special Character")
        void testSpecialCharacter(){
            String input = "CRED %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "cRED " + hash + "\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }
    }
}
