package topic.serialization;

import static topic.serialization.TopicConstants.*;
import java.util.Objects;

/**
 * Query message class
 * @version 1.0
 * @author David Beggs
 */
public abstract class Message {

    private long queryID;       //current queryID

    /**
     * sets current queryID
     * @param queryID long id
     * @return this message
     * @throws IllegalArgumentException thrown if argument is out of bounds
     */
    public final Message setQueryID(long queryID) throws IllegalArgumentException {
        verifyQuery(queryID);

        this.queryID = queryID;
        return this;
    }

    /**
     * fetches current id
     * @return long
     */
    public final long getQueryID(){
        return this.queryID;
    }

    /**
     * Creates packet encoding
     * @return byte []
     */
    public abstract byte[] encode();

    /**
     * determines equality
     * @param o object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return queryID == message.queryID;
    }

    /**
     * determines hash equivalent
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(queryID);
    }

    /**
     * Converts object to string
     * @return String
     */
    @Override
    public abstract String toString();

    /**
     * Verifies queryID is in bounds
     * @param queryID long
     * @throws IllegalArgumentException thrown if out of bounds
     */
    public void verifyQuery(long queryID) throws IllegalArgumentException{
        if (queryID > MAX_QUERYID || queryID < 0){
            throw new IllegalArgumentException("QUERY ID OUTSIDE OF BOUNDS");
        }
    }
}
