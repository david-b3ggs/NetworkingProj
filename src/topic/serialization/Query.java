package topic.serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import static topic.serialization.TopicConstants.*;

/**
 * Query message class
 * @version 1.0
 * @author David Beggs
 */
public class Query extends Message{

    private int requestedPosts;     //Number of requested posts

    /**
     * Query constructor
     * @param buffer byte [] to read from
     * @throws TopicException thrown to signify message error
     */
    public Query(byte[] buffer) throws TopicException {

        if (buffer == null){
            buffer = new byte[0];       //issues with c-style?
        }

        ByteBuffer buff = ByteBuffer.wrap(buffer);  //wrap and set byte order
        buff.order(ByteOrder.BIG_ENDIAN);

        if (buffer.length < MIN_HEADER){
            //too small
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }
        else if (buffer.length > MIN_HEADER){
            throw new TopicException(ErrorCode.PACKETTOOLONG);
        }

        byte first = buff.get();        //read first byte

        for (int i = 0 ;i < RESERVE_MAX; i++){    //read reserved
            if (((first >> i) & 1) == 1){
                //network error
                throw new TopicException(ErrorCode.NETWORKERROR);
            }
        }

        if (((first >> QFLAG_BIT) & 1) == 1){   //read qr
            //unexpected packet
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }

        //read version
        if (((first >> VERSION_FOUR) & 1) == 1 || ((first >> VERSION_THREE) & 1) == 1 ||
                ((first >> VERSION_TWO) & 1) == 0 || ((first >> VERSION_ONE) & 1) == 1){
            //wrong version
            throw new TopicException(ErrorCode.BADVERSION);
        }

        byte second = buff.get();   //read errorcode
        if (second != 0){
            throw new TopicException(ErrorCode.UNEXPECTEDERRORCODE);
        }

        this.setQueryID(Integer.toUnsignedLong(buff.getInt()));   //read id

        this.requestedPosts = Short.toUnsignedInt(buff.getShort()); //read post length
    }

    /**
     * Query constructor
     * @param queryID long to identify query
     * @param requestedPosts integer
     * @throws IllegalArgumentException thrown if id or requestedposts are out of bounds
     */
    public Query(long queryID, int requestedPosts) throws IllegalArgumentException {
        verifyQID(queryID);
        verifyRequestPost(requestedPosts);

        this.setQueryID(queryID);
        this.requestedPosts = requestedPosts;
    }

    /**
     * Encode creates serialized packet
     * @return byte array
     */
    @Override
    public byte[] encode() {
        byte [] buffer = new byte[8];

        ByteBuffer buff = ByteBuffer.wrap(buffer);      //wrap and set byte order
        buff.order(ByteOrder.BIG_ENDIAN);

        byte first = 0;     //set version
        first |= 1 << VERSION_TWO;

        buff.put(first);        //write first bit

        byte second = 0;        //write 0 error byte
        buff.put(second);

        buff.putInt(((Long)this.getQueryID()).intValue());     //write queryid
        buff.putShort((short)this.requestedPosts);  //write post length

        return buffer;
    }

    /**
     * Returns number of requests
     * @return int
     */
    public int getRequestedPosts(){
        return this.requestedPosts;
    }

    /**
     * Sets the num of requestedPosts
     * @param requestedPosts int
     * @return this object
     * @throws IllegalArgumentException if int out of bounds
     */
    public final Query setRequestedPosts(int requestedPosts) throws IllegalArgumentException{
        if (requestedPosts > MAX_REQUESTS || requestedPosts < 0){
            throw new IllegalArgumentException("REQUESTED POSTS OUT OF BOUNDS");
        }

        this.requestedPosts = requestedPosts;

        return this;
    }

    /**
     * Convert object to string representation
     * @return String
     */
    @Override
    public String toString() {
        return "Query: QueryID=" + this.getQueryID() + " ReqPosts=" + this.requestedPosts;
    }

    /**
     * Determines equivalence
     * @param o object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Query query = (Query) o;
        return requestedPosts == query.requestedPosts;
    }

    /**
     * returns hash code equivalent
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestedPosts);
    }

    /**
     * Ensures requested post number within bounds
     * @param requestedPosts int
     * @throws IllegalArgumentException thrown if out of bounds
     */
    private void verifyRequestPost(int requestedPosts) throws IllegalArgumentException {
        if (requestedPosts > MAX_REQUESTS || requestedPosts < 0){
            throw new IllegalArgumentException("REQUESTED POSTS OUT OF BOUNDS");
        }
    }

    /**
     * Ensures id within bounds
     * @param queryID long
     * @throws IllegalArgumentException thrown if out of bounds
     */
    private void verifyQID(long queryID) throws IllegalArgumentException{
        if (queryID > MAX_QUERYID || queryID < 0){
            throw new IllegalArgumentException("QUERY ID OUTSIDE OF BOUNDS");
        }
    }
}
