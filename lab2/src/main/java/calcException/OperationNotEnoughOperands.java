package calcException;

import operation.Operation;

public class OperationNotEnoughOperands extends OperationException {
    public OperationNotEnoughOperands(Operation<?> operation) {
        super(operation);
    }

    public OperationNotEnoughOperands(Operation<?> operation, Exception e) {
        super(operation, e);
    }

    @Override
    public String getMessage() {
        return "Not enough operands for operation <- " + super.getMessage();
    }
}
/**package calcException;

import operation.Operation;

public class OperationNotEnoughOperands extends OperationException{
    public OperationNotEnoughOperands(Operation<?> operation) {
        super(operation, e);
    }
    @Override
    public String getMessage(){
        return "Not enough operands for operation <- "+ super.getMessage();
    }
}*/