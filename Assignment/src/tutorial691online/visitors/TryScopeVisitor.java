package tutorial691online.visitors;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Statement;

import tutorial691online.handlers.SampleHandler;

import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryScopeVisitor extends ASTVisitor  {
	private static HashSet<TryStatement> tryStatements = new HashSet<>();
	private int numberOfTryScope=0;
	@Override
	public boolean visit(TryStatement node) {
		tryStatements.add(node);
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryScope");
		node.accept(methodInvocationVisitor);
		
		//List<Statement> bodyStatements = node.getBody().statements();
		//SampleHandler.printMessage("nodeeeee:" + node.getBody().statements());
		//SampleHandler.printMessage("parent :" + node.getBody().getParent().getParent().getParent().toString());
		
		int number = node.getBody().getParent().getParent().getParent().toString().indexOf('{');
		String str = node.getBody().getParent().getParent().getParent().toString().substring(0, number);
		//SampleHandler.printMessage("parent:" + str);
		if(str.contains("for") || str.contains("switch")|| str.contains("if") || str.contains("while") || str.contains("else")){
			numberOfTryScope++;
		}
			tryStatements.add(node);
		
		return super.visit(node);
	}
	public static HashSet<TryStatement> getTryStatements() {
		return tryStatements;
	}
	public int getNumberOfTryScope() {
		return numberOfTryScope;
	}
}
