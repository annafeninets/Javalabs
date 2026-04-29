package calcException;

import operation.Operation;

public class ArithmeticException extends OperationException {
    public ArithmeticException(Operation<?> op) {
        super(op);
    }

    public ArithmeticException(Operation<?> op, Exception e) {
        super(op, e);
    }

    @Override
    public String getMessage() {
        return "Arithmetic Exception <- " + super.getMessage();
    }
}
/**package calcException;

import operation.Operation;

public class ArithmeticException extends OperationException{
    ArithmeticException(Operation<?> op){
        super(op, e);
    }
    @Override
    public String getMessage(){
        return " Arithmetic Exception <-" + super.getMessage();
    }
}*/