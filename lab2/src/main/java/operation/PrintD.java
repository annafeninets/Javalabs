package operation;

import calcException.StackException;
import runtimeContext.RuntimeContext;
import stack.MyStack;

public class PrintD extends StackOperation<Double> {
    @Override
    protected void apply(MyStack<Double> stack, RuntimeContext<Double> context) throws StackException {
        stack.print();
    }
}
/**package operation;

import calcException.ReferenceToEmptyStack;
import runtimeContext.RuntimeContext;
import stack.MyStack;

public class PrintD implements StackOperation<Double>{
    @Override
    public void apply(MyStack<Double> stack, RuntimeContext<Double> context) throws ReferenceToEmptyStack {
        stack.print();
    }

}*/