package calcException;

import operation.Operation;

public class ContextException extends OperationException {
    public ContextException(Operation<?> operation) {
        super(operation);
    }

    public ContextException(Operation<?> operation, Exception e) {
        super(operation, e);
    }

    @Override
    public String getMessage() {
        return "Context operation exception <- " + super.getMessage();
    }
}
/**package calcException;

import operation.Operation;

public class ContextException extends OperationException{
    public ContextException(Operation<?> operation){
        super(operation, e);
    }

    @Override
    public String getMessage(){
        return "Context operation exception <- "+super.getMessage();
    }
}*/