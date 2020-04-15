package topic.serialization;

import java.util.Objects;

/**
 * TopicException
 * @version 1.0
 * @author David Beggs
 */
public class TopicException extends Exception {

    private ErrorCode errorCode;    //associated errorcode with current exception case

    /**
     * constructor
     * @param errorCode initialize with given code
     * @throws NullPointerException thrown if code is null
     */
    public TopicException(ErrorCode errorCode) throws NullPointerException{
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    /**
     * constructor
     * @param errorCode initialize with code
     * @param cause initialize with throwable cause
     */
    public TopicException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getErrorMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * returns current code
     * @return ErrorCode
     */
    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
