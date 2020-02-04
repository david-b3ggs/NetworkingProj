package tiktak.serialization.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Version;
import tiktak.serialization.MessageOutput;
import java.io.FileOutputStream;
import java.io.IOException;

@DisplayName("Version Tests")
public class VersionTests {

    private Version version = new Version();

    @Test
    void testToString(){
        Assertions.assertEquals("TikTak", version.toString());
    }

    @Nested
    @DisplayName("EncodeTests")
    class EncodeTests{

        Version version = new Version();

        @Test
        void testEncodeThrowsNull(){
            Assertions.assertThrows(NullPointerException.class, () ->
                    version.encode(new MessageOutput(null)));
        }

        @Test
        void testDecodeThrowsIO(){
            Assertions.assertThrows(IOException.class, () ->
                    version.encode(new MessageOutput(new FileOutputStream(""))));
        }
    }

}