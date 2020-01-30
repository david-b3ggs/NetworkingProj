package tiktak.serialization;

public class ValidationException extends Exception {

    ValidationException(){
        super();
    }

    public ValidationException(String message){
        super(message);
    }
}
