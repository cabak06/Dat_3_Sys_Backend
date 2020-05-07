package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class InvalidInputException extends Exception{

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException() {
        super("Wrong input. Please try again!");
    }  
}