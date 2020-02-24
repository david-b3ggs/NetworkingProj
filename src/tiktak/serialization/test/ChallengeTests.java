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
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Challenge Tests")
public class ChallengeTests {

    private static final byte[] GOODINPUT = "CLNG 5000\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final String NONCE = "5000";


    @Test
    @DisplayName("toString Test")
    void testToString() throws ValidationException {
        assertEquals("Challenge: nonce=" + NONCE,  (new Challenge(NONCE)).toString());
    }

    @Test
    @DisplayName("getOperation Test")
    void testGetOperation() throws ValidationException {
        assertEquals("CLNG", (new Challenge(NONCE)).getOperation());
    }

    @Test
    @DisplayName("Get ID")
    void testGetNonce() throws ValidationException {
        assertEquals(NONCE,  (new Challenge(NONCE)).getNonce());
    }
    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new Challenge(NONCE),  new Challenge(NONCE));
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        Challenge a =  new Challenge(NONCE);
        Challenge b =  new Challenge(NONCE);
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests{
        @Test
        @DisplayName("Constructor Valid")
        void testConstructorValid(){
            String testNonce = "123";
            assertDoesNotThrow(() ->  new Challenge(testNonce));
        }

        @Test
        @DisplayName("Constructor Non numeric")
        void testConstructorNonNumeric1(){
            String testNonce = "Hi";
            assertThrows(ValidationException.class, () ->   new Challenge(testNonce));
        }

        @Test
        @DisplayName("Constructor Non numeric 2")
        void testConstructorNonNumeric2(){
            String testNonce = "asdfsdlfdsaljk1231u9$";
            assertThrows(ValidationException.class, () ->   new Challenge(testNonce));
        }

        @Test
        @DisplayName("Constructor Non numeric 3")
        void testConstructorNonNumeric3(){
            String testNonce = "asdfsdlfd/saljk1231u9";
            assertThrows(ValidationException.class, () ->   new Challenge(testNonce));
        }

        @Test
        @DisplayName("Constructor Blank")
        void testConstructorBlank(){
            String testNonce = "";
            assertThrows(ValidationException.class, () ->   new Challenge(testNonce));
        }
    }



    @Nested
    @DisplayName("SetNonce Tests")
    class SetNonceTests{
        @Test
        @DisplayName("SetNonce Valid")
        void testSetNonceValid(){
            String testNonce = "123";
            assertDoesNotThrow(() ->    new Challenge(NONCE).setNonce(testNonce));
        }

        @Test
        @DisplayName("SetNonce Non numeric")
        void testSetNonceNonNumeric1(){
            String testNonce = "Hi";
            assertThrows(ValidationException.class, () ->    new Challenge(NONCE).setNonce(testNonce));
        }

        @Test
        @DisplayName("SetNonce Non numeric 2")
        void testSetNonceNonNumeric2(){
            String testNonce = "asdfsdlfdsaljk1231u9$";
            assertThrows(ValidationException.class, () ->    new Challenge(NONCE).setNonce(testNonce));
        }

        @Test
        @DisplayName("SetNonce Non numeric 3")
        void testSetNonceNonNumeric3(){
            String testNonce = "asdfsdlfd/saljk1231u9";
            assertThrows(ValidationException.class, () ->    new Challenge(NONCE).setNonce(testNonce));
        }

        @Test
        @DisplayName("SetNonce Blank")
        void testSetNonceBlank(){
            String testNonce = "";
            assertThrows(ValidationException.class, () ->   new Challenge(NONCE).setNonce(testNonce));
        }
    }


    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests{

        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new Challenge(NONCE).encode (new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new Challenge(NONCE)).encode (new MessageOutput(null)));
        }

        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new Challenge(NONCE)).encode(null));
        }

    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //based on test from provided example
            Challenge challenge = (Challenge) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals(NONCE, challenge.getNonce());
            assertEquals("CLNG", challenge.getOperation());
        }

        @Test
        @DisplayName("Special Character")
        void testSpecialCharacter(){
            String input = "CLNG %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Alphanumeric")
        void testAlphanumeric(){
            String input = "CLNG abc123\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Empty Nonce")
        void testEmptyNonce(){
            String input = "CLNG \r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "NG 123\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            Assertions.assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

    }

}
