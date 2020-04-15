import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import topic.serialization.Query;
import topic.serialization.TopicException;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {
    private byte[] encodedGoodQuery = new byte[] {0x20, 0, 0, 0, 0, 10, 0, 2};
    private byte[] shortPacket = new byte[] {0x20, 0, 0, 0, 0, 10, 0};
    private byte[] longPacket = new byte[] {0x20, 0, 0, 0, 0, 10, 0, 2, 0};
    private byte[] badQRPacket = new byte[] {0x28, 0, 0, 0, 0, 10, 0, 2};
    private byte[] badVersionPacket = new byte[] {8, 0, 0, 0, 0, 10, 0, 2};
    private byte[] badRSVDPacket = new byte[] {8, 0, 0, 0, 0, 10, 0, 2};
    private byte[] nonZeroErrorCodePacket = new byte[] {0x20, 1, 0, 0, 0, 10, 0, 2};
    private long queryid = 10;
    private int reqpost = 2;

    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests{
        @Test
        @DisplayName("Basic Working Case")
        void testBasicWorkingCase() {
            Assertions.assertArrayEquals(encodedGoodQuery, new Query(queryid, reqpost).encode());
        }


    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Basic Working Case")
        void testBasicWorkingCase() throws TopicException {
            Query q = new Query(encodedGoodQuery);
            assertEquals(queryid, q.getQueryID());
            assertEquals(reqpost, q.getRequestedPosts());
        }

        @Test
        @DisplayName("Short Packet")
        void testShortPacket() {
            assertThrows(TopicException.class, () -> new Query(shortPacket));
            assertThrows(TopicException.class, () -> new Query(null));
        }

        @Test
        @DisplayName("Long Packet")
        void testLongPacket() {
            assertThrows(TopicException.class, () -> new Query(longPacket));
        }

        @Test
        @DisplayName("Bad QR")
        void testBadQR() {
            assertThrows(TopicException.class, () -> new Query(badQRPacket));
        }
        @Test
        @DisplayName("Bad Version")
        void testBadVersion() {
            assertThrows(TopicException.class, () -> new Query(badVersionPacket));
        }

        @Test
        @DisplayName("Bad Reserved Bits")
        void testReservedBits() {
            assertThrows(TopicException.class, () -> new Query(badRSVDPacket));
        }

        @Test
        @DisplayName("Non-zero error code")
        void testNonZeroError() {
            assertThrows(TopicException.class, () -> new Query(nonZeroErrorCodePacket));
        }

        @Test
        @DisplayName("Null Value")
        void testNullValue(){
            assertThrows(TopicException.class, () -> new Query(null));
        }
    }
}
