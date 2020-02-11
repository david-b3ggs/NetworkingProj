/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: ValidationException.java
 * Description: Custom Exception to be thrown when message formatting is incorrect for tiktak protocol
 * Last Modified: 2/10/2020
 */

package tiktak.serialization;

/**
 * Custom Exception to be thrown when message formatting is incorrect for tiktak protocol
 * @version 1.0
 * @author David Beggs
 */
public class ValidationException extends Exception {
    private String badToken;

    /**
     * Constructor
     * @param message String given as explanation to user
     * @param cause Throwable cause of exception
     * @param badToken String what caused exception
     */
    ValidationException(String message, Throwable cause, String badToken){
        super(message, cause);
        this.badToken = badToken;
    }

    /**
     * Constructor
     * @param message String given as explanation to user
     * @param badToken String what caused the exception
     */
    public ValidationException(String message, String badToken){
        this(message, null, badToken);
    }

    /**
     * Return token member variable
     * @return String
     */
    public String getBadToken() {
        return badToken;
    }
}
