package operation;

import calcException.OperationNotEnoughOperands;
import calcException.OperationZeroDivision;
import calcException.ReferenceToEmptyStack;
import main.Main;
import stack.MyStack;

import java.util.logging.Level;

public class DivisionD extends ArithmeticOperation<Double> {
    @Override
    protected void apply(MyStack<Double> stack) throws OperationNotEnoughOperands, OperationZeroDivision {
        try {
            double secondOp = stack.pop();
            double firstOp = stack.pop();
            if (secondOp == 0) {
                throw new OperationZeroDivision(this);
            }
            double result = firstOp / secondOp;
            Main.logger.log(Level.INFO, "Result: " + result);
            stack.push(result);
        } catch (ReferenceToEmptyStack e) {
            throw new OperationNotEnoughOperands(this);
        }
    }
}
/**package operation;

import calcException.OperationNotEnoughOperands;
import calcException.OperationZeroDivision;
import calcException.ReferenceToEmptyStack;
import main.Main;
import stack.MyStack;

import java.util.logging.Level;

public class DivisionD implements ArithmeticOperation<Double> {
    @Override
    public void apply(MyStack<Double> stack) throws OperationNotEnoughOperands, OperationZeroDivision {
        try {
            double secondOp = stack.pop();
            double firstOp = stack.pop();
            if(secondOp == 0){
                throw new OperationZeroDivision(this);
            }
            double result = firstOp / secondOp;
            Main.logger.log(Level.INFO, "Result: "+result);
            stack.push(result);
        } catch (ReferenceToEmptyStack e){
            throw new OperationNotEnoughOperands(this);
        }
    }
}*/