package tutorial691online.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class MethodInvokeVisitor extends ASTVisitor {
private static HashSet<TryStatement> tryStatements = new HashSet<>();
	private int numberofMethodInvoke1;
	@Override
	public boolean visit(TryStatement node) {
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryBlock");
		node.accept(methodInvocationVisitor);		
		
		numberofMethodInvoke1 = numberofMethodInvoke1 + methodInvocationVisitor.getNumberofMethodInvoke();
		return super.visit(node);
	}
	public static HashSet<TryStatement> getTryStatements() {
		return tryStatements;
	}
	public int getNumberofMethodInvoke() {
		return numberofMethodInvoke1;
	}
	
}
