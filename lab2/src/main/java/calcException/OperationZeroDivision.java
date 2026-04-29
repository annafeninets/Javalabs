package calcException;

import operation.Operation;

public class OperationZeroDivision extends OperationException {
    public OperationZeroDivision(Operation<?> op) {
        super(op);
    }

    public OperationZeroDivision(Operation<?> op, Exception e) {
        super(op, e);
    }

    @Override
    public String getMessage() {
        return "Zero division <- " + super.getMessage();
    }
}
/**package calcException;

import operation.Operation;

public class OperationZeroDivision extends ArithmeticException{
    public OperationZeroDivision(Operation<?> op){
        super(op);
    }

    @Override
    public String getMessage(){
        return "Zero division <-" + super.getMessage();
    }

}*/