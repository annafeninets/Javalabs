package calcException;

public class StackException extends Exception {
    public StackException() {
        super();
    }

    public StackException(String message) {
        super(message);
    }

    public StackException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Stack exception";
    }
}
/**package calcException;

public class StackException extends Throwable{
    @Override
    public String getMessage(){
        return "Stack exception";
    }
}*/