package calcException;

import operation.Operation;

public class OperationException extends Exception {
    private final String operationName;
    private Exception connectedException;

    public OperationException(Operation<?> operation) {
        this.operationName = operation.toString();
        this.connectedException = null;
    }

    public OperationException(Operation<?> operation, Exception connectedException) {
        this.operationName = operation.toString();
        this.connectedException = connectedException;
    }

    @Override
    public String getMessage() {
        String baseMessage = "Operation <" + operationName + "> error.";
        if (connectedException != null) {
            return baseMessage + " Caused by: " + connectedException.getMessage();
        }
        return baseMessage;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        if (connectedException != null) {
            return connectedException.getStackTrace();
        }
        return super.getStackTrace();
    }

    public final String getOperationName() {
        return operationName;
    }
}
/**package calcException;

import operation.Operation;

public class OperationException extends Throwable{
    OperationException(Operation<?> operation){
        operationName = operation.toString();
    }
    @Override
    public String getMessage(){
        return "Operation <"+operationName+"> error. ";
    }

    final String operationName;
}*/