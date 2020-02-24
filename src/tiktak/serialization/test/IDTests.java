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

@DisplayName("ID Tests")
class IDTests {
    //from provided example
    private static final byte[] GOODINPUT = "ID user\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final String USER = "user";


    @Test
    @DisplayName("toString Test")
    void testToString() throws ValidationException {
        assertEquals("ID: id="+USER, (new ID(USER)).toString());
    }

    @Test
    @DisplayName("getOperation Test")
    void testGetOperation() throws ValidationException {
        assertEquals("ID", (new ID(USER)).getOperation());
    }

    @Test
    @DisplayName("Get ID")
    void testGetID() throws ValidationException {
        assertEquals(USER, new ID(USER).getID());
    }

    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new ID(USER),  new ID(USER));
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        ID a =  new ID(USER);
        ID b =  new ID(USER);
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests{
        @Test
        @DisplayName("Constructor Valid")
        void testConstructorValid(){
            assertDoesNotThrow(() ->  new ID("123abc"));
        }

        @Test
        @DisplayName("Constructor Non Alphanumeric")
        void testConstructorNonAlphNumeric1(){
            String testID = "%Hi[";
            assertThrows(ValidationException.class, () ->  new ID(testID));
        }

        @Test
        @DisplayName("Constructor Non Alphanumeric 2")
        void testConstructorNonAlphNumeric2(){
            String testID = "asdfsdlfdsaljk1231u9$";
            assertThrows(ValidationException.class, () ->  new ID(testID));
        }

        @Test
        @DisplayName("Constructor Non Alphanumeric 3")
        void testConstructorNonAlphNumeric3(){
            String testID = "asdfsdlfd/saljk1231u9";
            assertThrows(ValidationException.class, () ->  new ID(testID));
        }

        @Test
        @DisplayName("Constructor Blank")
        void testConstructorBlank(){
            String testID = "";
            assertThrows(ValidationException.class, () ->  new ID(testID));
        }
    }

    @Nested
    @DisplayName("Set ID Tests")
    class SetIDTests{
        @Test
        @DisplayName("Set ID Valid")
        void testSetIDValid(){
            assertDoesNotThrow(() ->  (new ID(USER)).setID("123abc"));
        }

        @Test
        @DisplayName("Set ID Non Alphanumeric")
        void testSetIDNonAlphNumeric1(){
            String testID = "%Hi[";
            assertThrows(ValidationException.class, () ->  (new ID(USER)).setID(testID));
        }

        @Test
        @DisplayName("Set ID Non Alphanumeric 2")
        void testSetIDNonAlphNumeric2(){
            String testID = "asdfsdlfdsaljk1231u9$";
            assertThrows(ValidationException.class, () -> (new ID(USER)).setID(testID));
        }

        @Test
        @DisplayName("Set ID Non Alphanumeric 3")
        void testSetIDNonAlphNumeric3(){
            String testID = "asdfsdlfd/saljk1231u9";
            assertThrows(ValidationException.class, () -> (new ID(USER)).setID(testID));
        }

        @Test
        @DisplayName("Set ID Blank")
        void testSetIDBlank(){
            String testID = "";
            assertThrows(ValidationException.class, () -> (new ID(USER)).setID(testID));
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
            new ID (USER).encode(new MessageOutput(bout));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }

        @Test
        @DisplayName("Null MessageOutput")
        void testNull(){
            assertThrows(NullPointerException.class, () ->
                    (new ID(USER)).encode(new MessageOutput(null)));
        }

        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new ID(USER)).encode(null));
        }


    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            ID id = (ID) Message.decode(new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals(USER, id.getID());
            assertEquals("ID", id.getOperation());
        }

        @Test
        @DisplayName("Invalid ID")
        void testInvalidId(){
            String input = " ID %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }


        @Test
        @DisplayName("Blank ID")
        void testBlankID(){
            String input = "ID \r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(EOFException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Starts With Whitespace.")
        void testStartWithWhitespace(){
            String input = " ID a\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Special Character")
        void testSpecialCharacter(){
            String input = "ID %\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand(){
            String input = "iD a12)\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }


    }

}
