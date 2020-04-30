package tutorial691online.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class ExceptionHandlingStrategyVisitor extends ASTVisitor {
	
	private static String[] ThrowMethods = {"throw"};
	private int specificStrategyCount = 0;
	private ITypeBinding exceptionInTry;
	private ITypeBinding catchArguments;
	public ITypeBinding[] methodExceptionBindings;
	
	@Override
	public boolean visit(TryStatement node) {
		
		//Throw in Try
		for(Object statement : node.getBody().statements()) {
			if(IsThrownStatement(statement.toString())) {
				if(statement instanceof ThrowStatement) {
					ITypeBinding throwInTry = ((ThrowStatement)statement).getExpression().resolveTypeBinding();
					exceptionInTry = throwInTry;
				}
			}
		}
		
		CatchClauseVisitor catchVisitor = new CatchClauseVisitor();
		node.accept(catchVisitor);
		catchArguments = catchVisitor.getCatchArguments();
		
		if(isStrategy(catchArguments, exceptionInTry)) {
			if(isStrategy(catchArguments, exceptionInTry)) {
				specificStrategyCount++;
			}
		}
////////////////////////////
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("throwBlock");
		node.accept(methodInvocationVisitor);
		methodExceptionBindings = methodInvocationVisitor.getMethodExceptionBindings();
		
		if(methodExceptionBindings != null) {
			//Throw jelo method
			for(ITypeBinding methodException: methodExceptionBindings) {
				if(catchArguments != null && methodException != null) {
					if(isStrategy(catchArguments, methodException)) {
						specificStrategyCount++;
					}
				}
			}
		}
		
		
		return super.visit(node);
	}
	
	public int ExceptionHandlingStrategyCount() {
		return specificStrategyCount;
	}
	
	private boolean isStrategy(ITypeBinding catchArguments, ITypeBinding exceptionInTry) {
		
		if(exceptionInTry != null && catchArguments != null) {
			// Specific Strategy
			if(exceptionInTry.equals(catchArguments)) {
//				SampleHandler.printMessage("exception1:"+exceptionInTry);
//				SampleHandler.printMessage("handler1:"+catchArguments);
				return true;
			} //Subsumption Strategy
			else if(IsSuperType(catchArguments, exceptionInTry)) {
//				SampleHandler.printMessage("exceptionnnnnnnnnnnnn:"+exceptionInTry);
//				SampleHandler.printMessage("handlerrrrrrrrrrrrrrr:"+catchArguments);
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Recursively find if the given subtype is a supertype of the reference type.
	 * 
	 * @param subtype       type to evaluate
	 * @param referenceType initial tracing reference to detect the super type
	 */
	public static Boolean IsSuperType(ITypeBinding subType, ITypeBinding referenceType) {

		if (subType == null || referenceType == null || referenceType.getQualifiedName().equals("java.lang.Object"))
			return false;

		if (subType.isEqualTo(referenceType.getSuperclass()))
			return true;
		
		
		return IsSuperType(subType, referenceType.getSuperclass());
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
