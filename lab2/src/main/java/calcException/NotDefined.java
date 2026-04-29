package calcException;

import operation.Operation;

public class NotDefined extends OperationException {
    private final String notDefinedString;

    public NotDefined(Operation<?> operation, String notDefinedString) {
        super(operation);
        this.notDefinedString = notDefinedString;
    }

    public NotDefined(Operation<?> operation, String notDefinedString, Exception e) {
        super(operation, e);
        this.notDefinedString = notDefinedString;
    }

    @Override
    public String getMessage() {
        return "Value \"" + notDefinedString + "\" is not defined <- " + super.getMessage();
    }
}
/**package calcException;

import operation.Operation;

public class NotDefined extends ContextException{
    public NotDefined(Operation<?> operation, String _notDefinedString){
        super(operation);
        notDefinedString = _notDefinedString;
    }

    @Override
    public String getMessage(){
        return "Value \""+notDefinedString+"\" is not defined <- "+super.getMessage();
    }

    final String notDefinedString;
}*/