package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.ID;
import tiktak.serialization.MessageOutput;
import tiktak.serialization.ValidationException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@DisplayName("All IDTests")
public class IDTests {

    private ID id;
    private static final byte[] IDENC = "ID user\r\n".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    void testsetIDValid() throws ValidationException{
        id = new ID("123");
        Assertions.assertDoesNotThrow(() -> id.setID("1a"));
    }

    @Test
    void testsetIDNonAlphNumeric1() throws ValidationException{
        final String testID = "%Hi[";
        id = new ID("abc");
        Assertions.assertThrows(ValidationException.class, () -> id.setID(testID));
    }

    @Test
    void testsetIDNonAlphNumeric2() throws ValidationException{
        final String testID = "asdfsdlfdsaljk1231u9$";
        id = new ID("abc");
        Assertions.assertThrows(ValidationException.class, () -> id.setID(testID));
    }

    @Test
    void testsetIDNonAlphNumeric3() throws ValidationException{
        final String testID = "asdfsdlfd/saljk1231u9";
        id = new ID("abc");
        Assertions.assertThrows(ValidationException.class, () -> id.setID(testID));
    }

    @Test
    void testsetIDBlank() throws ValidationException{
        final String testID = " ";
        id = new ID("abc");
        Assertions.assertThrows(ValidationException.class, () -> id.setID(testID));
    }

    @Test
    void testGetID() throws ValidationException{
        final String test = "testid4";
        id = new ID(test);
        Assertions.assertEquals("testid4", id.getID());
    }


    @Nested
    @DisplayName("EncodeTests")
    class EncodeTests{
        private String USER = "user";
        private ID id;

        @Test
        void testEncodeThrowsNull(){
            Assertions.assertThrows(NullPointerException.class, () ->
                    id.encode(new MessageOutput(null)));
        }

        @Test
        void testEncodeThrowsIO(){
            Assertions.assertThrows(IOException.class, () ->
                    id.encode(new MessageOutput(new FileOutputStream(""))));
        }

        @Test
        void testEncode() throws NullPointerException, IOException, ValidationException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new ID(USER).encode(new MessageOutput(bout));
            Assertions.assertArrayEquals(IDENC, bout.toByteArray());
        }
    }

}
