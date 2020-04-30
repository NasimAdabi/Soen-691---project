package tutorial691online.visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class ExceptionHandlingStrategyVisitor extends ASTVisitor {
	
	private static String[] ThrowMethods = {"throw"};
	private int specificStrategyCount = 0;
	private String exceptionListInTry;
	private String catchArguments;
	
	@Override
	public boolean visit(TryStatement node) {
		
		for(Object statement : node.getBody().statements()) {
			if(IsThrownStatement(statement.toString())) {
				if(statement instanceof ThrowStatement) {
					String throwInTry = ((ThrowStatement)statement).getExpression().resolveTypeBinding().getName();
					exceptionListInTry = throwInTry;
				}
			}
		}
		
		CatchClauseVisitor catchVisitor = new CatchClauseVisitor();
		node.accept(catchVisitor);
		catchArguments = catchVisitor.getCatchArguments();
		
		if(isSpecificStrategy(exceptionListInTry, catchArguments)) {
			specificStrategyCount++;
		}
		
		return super.visit(node);
	}
	
	public int ExceptionHandlingStrategyCount() {
		return specificStrategyCount;
	}
	
	private boolean isSpecificStrategy(String exceptionListInTry, String catchArguments) {
		if(exceptionListInTry != null && catchArguments != null) {
			if(exceptionListInTry.equals(catchArguments)) {
				//SampleHandler.printMessage("exception:"+exceptionListInTry);
				//SampleHandler.printMessage("handler:"+catchArguments);
				return true;
			}
		}

		return false;
	}
	
    private static boolean IsThrownStatement(String statement)
    {
        if (statement == null) return false;
        for (String logmethod : ThrowMethods)
        {
            if (statement.indexOf(logmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
}
