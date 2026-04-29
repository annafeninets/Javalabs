package calcException;


import operation.CustomizableOperation;

public class OperationConfigurationError extends Throwable {
    OperationConfigurationError(CustomizableOperation _operation){
        opName = _operation.toString();
    }

    @Override
    public String getMessage(){
        return "<"+opName+"> configuration error.";
    }

    final String opName;
}