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

    private ID id = new ID("USER");
    private static final byte[] IDENC = "ID user\r\n".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    void testSetIDValid(){
        Assertions.assertDoesNotThrow(() -> id.setId("1a"));
    }

    @Test
    void testSetIDNonAlphNumeric1(){
        final String testID = "%Hi[";

        Assertions.assertThrows(ValidationException.class, () -> id.setId(testID));
    }

    @Test
    void testSetIDNonAlphNumeric2(){
        final String testID = "asdfsdlfdsaljk1231u9$";

        Assertions.assertThrows(ValidationException.class, () -> id.setId(testID));
    }

    @Test
    void testSetIDNonAlphNumeric3(){
        final String testID = "asdfsdlfd/saljk1231u9";

        Assertions.assertThrows(ValidationException.class, () -> id.setId(testID));
    }

    @Test
    void testSetIDBlank(){
        final String testID = " ";

        Assertions.assertThrows(ValidationException.class, () -> id.setId(testID));
    }

    @Test
    void testGetID() throws ValidationException{
        final String test = "testid4";
        id.setId(test);

        Assertions.assertEquals("testid4", id.getID());
    }


    @Nested
    @DisplayName("EncodeTests")
    class EncodeTests{
        private String USER = "user";
        private ID id = new ID(USER);

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
        void testEncode() throws NullPointerException, IOException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new ID(USER).encode(new MessageOutput(bout));
            Assertions.assertArrayEquals(IDENC, bout.toByteArray());
        }
    }

}
