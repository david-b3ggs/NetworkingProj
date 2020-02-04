package tiktak.serialization;

public class ValidationException extends Exception {
    private String badToken;

    ValidationException(String message, Throwable cause, String badToken){
        super(message, cause);
        this.badToken = badToken;
    }

    public ValidationException(String message, String badToken){
        this(message, null, badToken);
    }

    public String getBadToken() {
        return badToken;
    }
}
