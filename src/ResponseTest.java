import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import topic.serialization.ErrorCode;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {

    private byte[] encodedGoodPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] shortPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o'};
    private byte[] longPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b','z'};
    private byte[] badQRPacket = new byte[] {0x20,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] badVersionPacket = new byte[] {8,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] badRSVDPacket = new byte[] {0x29,
            0, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] bigErrorCodePacket = new byte[] {0x28,
            20, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] negativeErrorCodePacket = new byte[] {0x28,
            -1, 0, 0, 0, 10, 0, 2,0,5,'B','o','b','b','y',0,3,'B','o','b'};
    private byte[] badPostPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 2,0,5,'B',25,'b','b','y',0,3,'B','o','b'};
    private byte[] emptyListPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 0};
    private byte[] listOfEmptyPostsPacket = new byte[] {0x28,
            0, 0, 0, 0, 10, 0, 3,0,0,0,0,0,0};
    private byte[] donaTestFailOne = new byte[] {40, 0, 0, 0, 0, 0, 0, 0};

    private long goodQueryID = 10;


    ArrayList<String> goodPostsList = new ArrayList<String>(
            Arrays.asList("Bobby", "Bob"));
    ArrayList<String> emptyList = new ArrayList<String>();
    ArrayList<String> listOfEmptyPosts = new ArrayList<String>(
            Arrays.asList("", "", ""));


    @Nested
    @DisplayName("Encode Tests")
    class EncodeTests{
        @Test
        @DisplayName("Basic Working Case")
        void testBasicWorkingCase() throws TopicException {
            Assertions.assertArrayEquals(encodedGoodPacket, new Response(
                    goodQueryID, ErrorCode.NOERROR, goodPostsList).encode());
        }

        @Test
        @DisplayName("Test Empty List")
        void testEmptyList() {
            assertArrayEquals(emptyListPacket, new Response(
                    goodQueryID, ErrorCode.NOERROR, emptyList).encode());
        }

        @Test
        @DisplayName("Test Empty Post")
        void testEmptyPost() {
            assertArrayEquals(listOfEmptyPostsPacket, new Response(
                    goodQueryID, ErrorCode.NOERROR, listOfEmptyPosts).encode());
        }

    }
    @Nested
    @DisplayName("Decode Tests")
    class DecodeTests{
        @Test
        @DisplayName("Basic Working Case")
        void testBasicWorkingCase() throws TopicException {
            Response r = new Response(encodedGoodPacket);
            assertEquals(goodQueryID, r.getQueryID());
            assertEquals(ErrorCode.NOERROR, r.getErrorCode());
            assertEquals(goodPostsList,r.getPosts());
        }

        @Test
        @DisplayName("Empty List")
        void testEmptyValidPostList() throws TopicException {
            Response r = new Response(emptyListPacket);
            assertEquals(goodQueryID, r.getQueryID());
            assertEquals(ErrorCode.NOERROR, r.getErrorCode());
            assertEquals(emptyList,r.getPosts());
        }

        @Test
        @DisplayName("Empty Post")
        void testEmptyPost() throws TopicException {
            Response r = new Response(listOfEmptyPostsPacket);
            assertEquals(goodQueryID, r.getQueryID());
            assertEquals(ErrorCode.NOERROR, r.getErrorCode());
            assertEquals(listOfEmptyPosts,r.getPosts());
        }

        @Test
        @DisplayName("Short Packet")
        void testShortPacket() {
            assertThrows(TopicException.class, () -> new Response(shortPacket));
            assertThrows(TopicException.class, () -> new Response(null));
        }

        @Test
        @DisplayName("Long Packet")
        void testLongPacket() {
            assertThrows(TopicException.class, () -> new Response(longPacket));
        }

        @Test
        @DisplayName("Bad QR")
        void testBadQR() {
            assertThrows(TopicException.class, () -> new Response(badQRPacket));
        }

        @Test
        @DisplayName("Null Value")
        void testNullValue(){
            assertThrows(TopicException.class, () -> new Response(null));
        }

        @Test
        @DisplayName("Bad Version")
        void testBadVersion() {
            assertThrows(TopicException.class, () -> new Response(badVersionPacket));
        }

        @Test
        @DisplayName("Bad Reserved Bits")
        void testReservedBits() {
            assertThrows(TopicException.class, () -> new Response(badRSVDPacket));
        }

        @Test
        @DisplayName("Bad error code")
        void testBadError() {
            assertThrows(TopicException.class, () -> new Response(bigErrorCodePacket));
            assertThrows(TopicException.class, () -> new Response(negativeErrorCodePacket));
        }

        @Test
        @DisplayName("Good error code")
        void testGoodError() {
            byte [] testArray = encodedGoodPacket;
            for (ErrorCode errorCode: ErrorCode.values()){
                testArray [1]= (byte)errorCode.getErrorCodeValue();
                assertDoesNotThrow( () -> {
                    new Response(testArray);
                });
            }
        }

        @Test
        @DisplayName("Bad Post")
        void testBadPost() {
            assertThrows(TopicException.class, () -> new Response(badPostPacket));
        }

        @Test
        @DisplayName("Null Post")
        void testNullPost() {
            assertThrows(IllegalArgumentException.class, () -> new Response(1, ErrorCode.NOERROR, null));
        }

    }

    @Test
    @DisplayName("Test toString")
    void testToString(){
        assertEquals(new Response(1, ErrorCode.NOERROR, goodPostsList).toString(),
                "Response: QueryID=1 Posts=2: Bobby, Bob");
    }
}
