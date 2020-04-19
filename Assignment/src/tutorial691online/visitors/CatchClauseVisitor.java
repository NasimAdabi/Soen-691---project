package tutorial691online.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import tutorial691online.handlers.SampleHandler;

public class CatchClauseVisitor extends ASTVisitor{

	private HashSet<CatchClause> throwStatements = new HashSet<>();
	private int logActionCount = 0;
	private int methodCallActionCount = 0;
	private String exceptionName;
	private ITypeBinding exceptionType;
	private String exceptionTypeName;
	private Map<String, String> flowHandlingStatements = new HashMap<String, String>();
	
	@Override
	public boolean visit(CatchClause node) {
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("LogCatchSwitch");
		node.accept(methodInvocationVisitor);
		flowHandlingStatements.putAll(methodInvocationVisitor.getFlowHandlingActions());
		
		
		if(isfirstPatternException(node)) {
			throwStatements.add(node);
		}
		
		return super.visit(node);
	}

//	public void getLogActionCount(CatchClause node) {
//		
//		List<Statement> blockStatements = node.getBody().statements();
//		
//		for(Statement statement : blockStatements) {
//			if(statement.toString().contains("log")) {
//				//SampleHandler.printMessage("log.error:"+statement);
//				logActionCount++;
//			}
//			else {
//				IMethodBinding methodNode = node.resolveMethodBinding();
//				if(methodNode != null) {
//					ITypeBinding[] exceptionBinding = methodNode.getExceptionTypes();
//					for(ITypeBinding exception : exceptionBinding) {
//						String exceptionName = exception.getQualifiedName();
//						this.exceptionName = exceptionName;
//						//SampleHandler.printMessage("Throws exception::::::" + exceptionName);
//						
//						ITypeBinding exceptionType = exception.getTypeDeclaration();
//						this.exceptionTypeName = exceptionType.getQualifiedName();
//						this.exceptionType = exceptionType;
//						//SampleHandler.printMessage("Invoked Typeeeeeeeee::::::" + this.exceptionType.toString());
//						//SampleHandler.printMessage("testtttttttttttttttttttttttttttttt" + methodNode);
//					}
//				}
//				
//				methodCallActionCount++;
//			}
//		}
//		
//	}
	
	private boolean isfirstPatternException(CatchClause node) {
		int throwCounter = 0;
		int logCounter = 0;
		
		List<Statement> blockStatements = node.getBody().statements();
		
		for(Statement statement : blockStatements) {
			if(statement.toString().contains("throw")) {
				throwCounter++;
			}
			if(statement.toString().contains("log")) {
				flowHandlingStatements.put(statement.toString(), "Log");
				logCounter++;
			}
			if(throwCounter>0 && logCounter>0) {
				return true;
			}
		}
		
		
		return false;
	}

	public HashSet<CatchClause> getThrowStatements() {
		return throwStatements;
	}
	
	public Map<String, String> getActionStatements() {
		return flowHandlingStatements;
	}
}
