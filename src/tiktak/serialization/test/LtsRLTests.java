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

@DisplayName("LtsRL Tests")
class LtsRLTests {
    //from provided example
    private static final byte[] GOODINPUT = "LTSRL movie Imagecontents\r\n"
            .getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] image = "Imagecontents"
            .getBytes(StandardCharsets.ISO_8859_1);
    private static final String category = "movie";

    @Test
    @DisplayName("toString Test")
    void testToString() throws ValidationException {
        assertEquals("LtsRL: category=" + category + " image=" +
                        image.length + " bytes",
                (new LtsRL(category, image)).toString());
    }

    @Test
    @DisplayName("getOperation Test")
    void testGetOperation() throws ValidationException {
        assertEquals("LTSRL",
                (new LtsRL(category, image)).getOperation());
    }

    @Test
    @DisplayName("Get Category")
    void testGetCategory() throws ValidationException {
        assertEquals(category,
                (new LtsRL(category, image)).getCategory());
    }

    @Test
    @DisplayName("Get image")
    void testGetImage() throws ValidationException {
        assertEquals(image,
                new LtsRL(category, image).getImage());
    }

    @Test
    @DisplayName("Equals Test")
    void equalsString() throws ValidationException {
        Assertions.assertEquals(
                new LtsRL(category, image), new LtsRL(category, image));
    }

    @Test
    @DisplayName("Hashcode Test")
    void hashcodeString() throws ValidationException {
        LtsRL a = new LtsRL(category, image);
        LtsRL b = new LtsRL(category, image);
        Assertions.assertTrue(a.equals(b) && b.equals(a));
        Assertions.assertTrue(a.hashCode() == b.hashCode());
    }

    @Nested
    @DisplayName("Set Category Tests")
    class SetCategoryTests {
        @Test
        @DisplayName("Set Category Valid")
        void testSetCategoryValid() {
            String testCategory = "media";
            assertDoesNotThrow(() ->
                    new LtsRL(category, image).setCategory(testCategory));
        }

        @Test
        @DisplayName("Set Category Null")
        void testSetCategoryNull() {
            String testCategory = null;
            assertThrows(ValidationException.class, () ->
                    new LtsRL(category, image).setCategory(testCategory));
        }

        @Test
        @DisplayName("Set Category Special Character")
        void testSetCategorySpecialChar() {
            assertThrows(ValidationException.class, () ->
                    new LtsRL(category, image).setCategory("Movie$"));
        }
    }
    @Nested
    @DisplayName("Set Image Tests")
    class SetImageTests {
        @Test
        @DisplayName("Set Image Valid")
        void testSetMessageValid() {
            assertDoesNotThrow(() -> (
                    new LtsRL(category, image)).setImage(image));
        }

        @Test
        @DisplayName("Set Image Null")
        void testSetImageNull() {
            assertThrows(ValidationException.class, () -> (
                    new LtsRL(category, image)).setImage(null));
        }

    }


    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Valid Parameters")
        void testConstructorValidParameters() {
            assertDoesNotThrow(() -> new LtsRL(category, image));
        }

        @Test
        @DisplayName("Category Null")
        void testConstructorCategoryNull() {
            assertThrows(ValidationException.class, () ->
                    new LtsRL(null, image));
        }

        @Test
        @DisplayName("Category Special Character")
        void testConstructorCategorySpecialChar() {
            assertThrows(ValidationException.class, () ->
                    new LtsRL("Movie$", image));
        }

        @Test
        @DisplayName("Image Null")
        void testConstructorImageNull() {
            assertThrows(ValidationException.class, () ->
                    new LtsRL(category, null));
        }
    }

    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests {

        /*@Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            new LtsRL(category, image).encode(new MessageOutput(bout));
            System.out.println(new String(GOODINPUT, StandardCharsets.ISO_8859_1));
            System.out.println(new String(bout.toByteArray(), StandardCharsets.ISO_8859_1));
            assertArrayEquals(GOODINPUT, bout.toByteArray());
        }*/

        @Test
        @DisplayName("Null MessageOutput")
        void testNull() {
            assertThrows(NullPointerException.class, () ->
                    (new LtsRL(category, image)).encode(new MessageOutput(null)));
        }

        @Test
        @DisplayName("Encode to no outputstream")
        void testNoStream(){
            assertThrows(NullPointerException.class, () ->
                    (new LtsRL(category, image)).encode(null));
        }


    }

    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests {
        /*@Test
        @DisplayName("Byte Array")
        void testByteArray() throws NullPointerException, IOException, ValidationException {
            //test is from provided example
            LtsRL lts = (LtsRL) Message.decode(
                    new MessageInput(new ByteArrayInputStream(GOODINPUT)));
            assertEquals(category, lts.getCategory());
            assertArrayEquals(image,lts.getImage());
            assertEquals("LTSRL", lts.getOperation());
        }*/

        @Test
        @DisplayName("No image")
        void testNoImage() {
            String input = "LTSRL movie\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("No Category")
        void testNoCategory() {
            String input = "LTSRL imageContents\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Starts With Whitespace")
        void testStartWithWhitespace() {
            String input = " LTSRL movie imageContents\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Category Special Character")
        void testInvalidCategorySpecialCharacter() {
            String input = "LTSRL % imageContents\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }

        @Test
        @DisplayName("Invalid Command")
        void testInvalidCommand() {
            String input = "LtSrL movie imageContents\r\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            assertThrows(ValidationException.class, () -> Message.decode(new MessageInput(in)));
        }
    }
}
