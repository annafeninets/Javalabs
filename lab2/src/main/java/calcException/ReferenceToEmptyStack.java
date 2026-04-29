package calcException;

public class ReferenceToEmptyStack extends StackException {
    public ReferenceToEmptyStack() {
        super();
    }

    public ReferenceToEmptyStack(String message) {
        super(message);
    }

    public ReferenceToEmptyStack(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Reference to empty stack";
    }
}
/**package calcException;

public class ReferenceToEmptyStack extends StackException{
    @Override
    public String getMessage(){
        return "Reference to empty stack <- " + super.getMessage();
    }
}*/