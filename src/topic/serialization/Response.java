package topic.serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static topic.serialization.TopicConstants.*;

/**
 * Response message class
 * @version 1.0
 * @author David Beggs
 */
public class Response extends Message {
    private long queryID;           //received query id
    private ErrorCode errorCode;    //current errorcode
    private List<String> posts;     //list of posts to return
    //TODO: Make constants
    //TODO: printable range, validation error changes


    /**
     * Resonse Constructor
     * @param queryID long identify query
     * @param errorCode ErrorCode to display message status
     * @param posts List of posts
     * @throws TopicException thrown if errorcode triggered
     */
    //TODO:FLIP BYTES
    public Response(long queryID, ErrorCode errorCode, List<String> posts) throws IllegalArgumentException{

        verifyQuery(queryID);
        verifyErrorCode(errorCode);
        verifyPosts(posts);

        this.errorCode = errorCode;
        this.posts = posts;
        this.setQueryID(queryID);
    }

    /**
     * Response construtor
     * @param buffer byte []
     * @throws TopicException thrown if errorcode triggered
     */
    public Response(byte[] buffer) throws TopicException {
        //compensate for null array
        if (buffer == null){
            buffer = new byte[0];       //issues with c-style?
        }
        this.posts = new ArrayList<>();

        ByteBuffer buff = ByteBuffer.wrap(buffer);  //wrap buffer for additional func
        buff.order(ByteOrder.BIG_ENDIAN);           //set byte order

        //test min length of received header
        if (buffer.length < MIN_HEADER){
            //too small
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }

        //get first byte
        byte first = buff.get();

        for (int i = 0 ;i < RESERVE_MAX; i++){    //read reserved
            if (((first >> i) & 1) == 1){
                //network error
                throw new TopicException(ErrorCode.NETWORKERROR);
            }
        }

        if (((first >> QFLAG_BIT) & 1) == 0){   //read qr
            //unexpected packet
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }

        //read version
        if (((first >> VERSION_FOUR) & 1) == 1 || ((first >> VERSION_THREE) & 1) == 1 ||
                ((first >> VERSION_TWO) & 1) == 0 || ((first >> VERSION_ONE) & 1) == 1){
            //wrong version
            throw new TopicException(ErrorCode.BADVERSION);
        }

        byte second = buff.get();   //get second byte

        if (second < 0 || second > 8 || second == 6){
            //unexpected error code
            throw new TopicException(ErrorCode.UNEXPECTEDERRORCODE);
        }
        else {
            this.errorCode = ErrorCode.getErrorCode(second);
        }

        this.setQueryID(Integer.toUnsignedLong(buff.getInt()));   //read query id and postlist length
        int length = Short.toUnsignedInt(buff.getShort());

        for (int i = 0; i < length; i++){ //for every post
            int stringLength = Short.toUnsignedInt(buff.getShort());
            byte [] temp = new byte[stringLength];

            for (int j = 0; j < stringLength; j++){
                if (buff.hasRemaining()){
                    temp[j] = buff.get();
                    if ((int)temp[j] < 32 || (int)temp[j] >= 127){
                        throw new TopicException(ErrorCode.VALIDATIONERROR);
                    }
                }
                else {
                    throw new TopicException(ErrorCode.PACKETTOOSHORT);
                }
            }

            //add loop to test for printable chars in string
            this.posts.add(new String(temp, StandardCharsets.US_ASCII));
        }

        if (buff.hasRemaining()){       //throw exception for excess bytes
            //too long
            throw new TopicException(ErrorCode.PACKETTOOLONG);
        }
    }

    /**
     * return current ErrorCode
     * @return ErrorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * sets current errorcode
     * @param errorCode ErrorCode
     * @return this Response
     * @throws IllegalArgumentException thrown if errorcode is out of bounds
     */
    public Response setErrorCode(ErrorCode errorCode) throws IllegalArgumentException{
        verifyErrorCode(errorCode);

        this.errorCode = errorCode;
        return this;
    }

    /**
     * Return post list
     * @return List of String
     */
    public List<String> getPosts() {
        return posts;
    }

    /**
     * set current list of posts
     * @param posts ListString list of posts
     * @return this Response
     * @throws IllegalArgumentException thrown if arguments out of range
     */
    public final Response setPosts(List<String> posts) throws IllegalArgumentException{
        if (null == posts){
            throw new IllegalArgumentException("CANNOT PROCESS NULL POSTS");
        }

        verifyPosts(posts);

        this.posts = posts;
        return this;
    }

    /**
     * Create string from object
     * @return string
     */
    @Override
    public String toString() {
        String display;
        if (!this.posts.isEmpty()){
            display = this.posts.get(0);
            for (int i = 1; i < this.posts.size(); i++){
                display += ", " + this.posts.get(i);
            }
        }
        else {
            display = "";
        }

        return  "Response: QueryID=" + this.getQueryID() + " Posts=" + this.posts.size() + ": " + display;
    }

    /**
     * Encodes object to message header
     * @return byte []
     */
    @Override
    public byte[] encode() {
        int headerSize = MIN_HEADER;     //set static header size

        for (String s: this.posts){ //scan for header length
            headerSize += POST_LENGTH_LENGTH;
            headerSize += s.length();
        }

        byte [] buffer = new byte[headerSize];  //create byte buffer

        ByteBuffer buff = ByteBuffer.wrap(buffer);  //wrap and set byte order
        buff.order(ByteOrder.BIG_ENDIAN);

        byte first = 0;         //set first byte to 0 and set version num
        first |= 1 << VERSION_TWO;
        first |= 1 << QFLAG_BIT;

        buff.put(first);        //place first byte

        byte second = (byte)this.errorCode.getErrorCodeValue();        //initialize second byte and place errorcode
        buff.put(second);       //place second byte

        buff.putInt(((Long)this.getQueryID()).intValue());   //place id

        buff.putShort((short)this.posts.size());    //place post size

        for (int i = 0; i < this.posts.size(); i++){    //place list of posts
            buff.putShort((short)this.posts.get(i).length());

            for (int j = 0; j < this.posts.get(i).length(); j++){
                buff.put((byte) this.posts.get(i).charAt(j));
            }
        }

        return buffer;
    }

    /**
     * Determines equality
     * @param o object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Response response = (Response) o;
        return queryID == response.queryID &&
                errorCode == response.errorCode &&
                Objects.equals(posts, response.posts);
    }

    /**
     * Produces hashcode from object
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), queryID, errorCode, posts);
    }


    /**
     * verifies post list and post length are within bounds
     * @param posts list of posts
     * @throws IllegalArgumentException thrown if out of bounds
     */
    public void verifyPosts(List<String> posts) throws IllegalArgumentException{

        if (posts == null){
            throw new IllegalArgumentException("NULL LIST PASSED");
        }

        if (posts.size() > MAX_REQUESTS){
            throw new IllegalArgumentException("POST LENGTH TOO LONG");
        }

        for (String s: posts){
            testPost(s);
        }
    }

    private void testPost(String s) throws IllegalArgumentException {
        if (null == s){
            throw new IllegalArgumentException("POST IS NULL");
        }

        if (s.length() > MAX_REQUESTS){
            throw new IllegalArgumentException("POST TOO LONG");
        }

        for (char b: s.toCharArray()){
            if ((int)b < 32 || (int)b >= 127){
                throw new IllegalArgumentException("POST FORMAT ERROR");
            }
        }
    }

    /**
     * Verifies valid errorcode
     * @param errorCode ErrorCode
     * @throws IllegalArgumentException thrown if invalid
     */
    public void verifyErrorCode(ErrorCode errorCode) throws IllegalArgumentException{
        if (null == errorCode){
            throw  new IllegalArgumentException("CANNOT PROCESS NULL ERRORCODE");
        }
    }
}
