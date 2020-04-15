package topic.serialization;

/**
 * ErrorCode Enum
 * @version 1.0
 * @author David Beggs
 */
public enum ErrorCode {

    //List of constant error codes, values, and messages
    NOERROR(0, "No error"),
    BADVERSION(1, "Bad version"),
    UNEXPECTEDERRORCODE(2, "Unexpected error code"),
    UNEXPECTEDPACKETTYPE(3, "Unexpected packet type"),
    PACKETTOOLONG(4, "Packet too long"),
    PACKETTOOSHORT(5, "Packet too short"),
    NETWORKERROR(7, "Network error"),
    VALIDATIONERROR(8, "Validation error");

    private int codeNumber; //enums number assigned to constant
    private String errorMessage; //enums corresponding message

    /**
     * Constructor
     * @param codeNumber Initializing number
     * @param errorMessage initializing message
     */
    ErrorCode(int codeNumber, String errorMessage) throws IllegalArgumentException{

        this.codeNumber = codeNumber;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns current errorcode number
     * @return int
     */
    public int getErrorCodeValue(){
        return this.codeNumber;
    }

    /**
     * returns current errorCode Message
     * @return String
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

    /**
     * Returns current errorcode given a corresponding value
     * @param errorCodeValue int identifying enum
     * @return ErrorCode
     * @throws IllegalArgumentException thrown if invalid number
     */
    public static ErrorCode getErrorCode(int errorCodeValue) throws IllegalArgumentException {
        if (errorCodeValue == 6){
            throw new IllegalArgumentException("RESERVED ERROR CODE NOT FOR USE");
        }

        for (ErrorCode e: ErrorCode.values()){
            if (e.getErrorCodeValue() == errorCodeValue){
                return e;
            }
        }

        throw new IllegalArgumentException("NO MATCHING ERROR CODE VALUE. VALUES BETWEEN 0-8");
    }
}
