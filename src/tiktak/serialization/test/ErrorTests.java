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
import tiktak.serialization.Error;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Error Tests")
class ErrorTests {
    //from provided example
    private static final byte[] GOODINPUT = "ERROR 500 BadError\r\n"
            .getBytes(StandardCharsets.ISO_8859_1);
    private static final int code = 500;
    private static final String message = "BadError";

    @Test
    @DisplayName("toString Test")
    void testToString() throws ValidationException {
        assertEquals("Error: code=" + code +" message=" +message,
                (new tiktak.serialization.Error(code,message) ).toString());
    }

    @Test
    @DisplayName("getOperation Test")
    void testGetOperation() throws ValidationException {
        assertEquals("ERROR",
                (new tiktak.serialization.Error(code,message)).getOperation());
    }

    @Test
    @DisplayName("Get Code")
    void testGetCode() throws ValidationException {
        assertEquals(code,
                new tiktak.serialization.Error(code,message).getCode());
    }
    @Test
    @DisplayName("Get Message")
    void testGetMessage() throws ValidationException {
        assertEquals(message,
                new tiktak.serialization.Error(code,message).getMessage());
    }
    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new tiktak.serialization.Error(code,message),  new tiktak.serialization.Error(code,message));
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        tiktak.serialization.Error a =  new tiktak.serialization.Error(code,message);
        tiktak.serialization.Error b =  new tiktak.serialization.Error(code,message);
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }
    @Nested
    @DisplayName("Set Code Tests")
    class SetCodeTests{
        @Test
        @DisplayName("Set Code Valid")
        void testSetCodeValid(){
            int testCode = 200;
            assertDoesNotThrow(() -> 
                    new tiktak.serialization.Error(code,message).setCode(testCode));
        }

        @Test
        @DisplayName("Set Code Negative")
        void testSetCodeNegative(){
            int testCode = -123;
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(code,message).setCode(testCode));
        }

        @Test
        @DisplayName("Set Code Zero")
        void testSetCodeZero(){
            int testCode = 0;
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(code,message).setCode(testCode));
        }
    }
    @Nested
    @DisplayName("Set Message Tests")
    class SetMessageTests{
        @Test
        @DisplayName("Set Message Valid")
        void testSetMessageValid(){
            assertDoesNotThrow(() ->  (
                    new tiktak.serialization.Error(code,message)).setMessage(message));
        }

        @Test
        @DisplayName("Set Message Null")
        void testSetMessageNull(){
            String testMessage = null;
            assertThrows(ValidationException.class, () -> (
                    new tiktak.serialization.Error(code,message)).setMessage(testMessage));
        }

        @Test
        @DisplayName("Set Message Blank")
        void testSetMessageBlank(){
            String testMessage = "";
            assertThrows(ValidationException.class, () -> (
                    new tiktak.serialization.Error(code,message)).setMessage(testMessage));
        }
    }


    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests{
        @Test
        @DisplayName("Valid Parameters")
        void testConstructorValidParameters(){
            assertDoesNotThrow(() ->  new tiktak.serialization.Error(code,message));
        }
        @Test
        @DisplayName("Code Zero")
        void testConstructorCodeZero(){
            int testCode = 0;
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(testCode,message));
        }

        @Test
        @DisplayName("Code Negative")
        void testConstructorCodeNegative(){
            int testCode = -123;
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(testCode,message));
        }

        @Test
        @DisplayName("Message blank")
        void testConstructorMessageBlank(){
            String testMessage = "";
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(code,testMessage));
        }

        @Test
        @DisplayName("Message null")
        void testConstructorMessageNull(){
            String testMessage = null;
            assertThrows(ValidationException.class, () ->
                    new tiktak.serialization.Error(code,testMessage));
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
            new tiktak.serialization.Error(code,message).encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new tiktak.serialization.Error(code,message)).encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new tiktak.serialization.Error(code,message)).encode(null));
        }



    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            Error error = (Error) Message.decode(
                    new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals(code, error.getCode());
            assertEquals(message, error.getMessage());
            assertEquals("ERROR", error.getOperation());
        }

        @Test
        @DisplayName("Wrong Order")
        void testWrongOrder(){
            String input = "ERROR BadError 500\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }


        @Test
        @DisplayName("No Message")
        void testNoMessage(){
            String input = "ERROR 500\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("No Code")
        void testNoCode(){
            String input = "ERROR BadError\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Starts With Whitespace")
        void testStartWithWhitespace(){
            String input = " ERROR 500 BadError\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Code Special Character")
        void testInvalidCodeSpecialCharacter(){
            String input = "ERROR % BadError\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "eRRor 500 BadError\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }
    }
}
