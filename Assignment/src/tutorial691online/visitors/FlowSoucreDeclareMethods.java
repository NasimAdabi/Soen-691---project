package tutorial691online.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class FlowSoucreDeclareMethods extends ASTVisitor {
	private static HashSet<TryStatement> tryStatements = new HashSet<>();
	private int numberOfFlowSouceDeclared=0;
	@Override
	public boolean visit(TryStatement node) {
		tryStatements.add(node);
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryScope");
		node.accept(methodInvocationVisitor);
		//List<Statement> bodyStatements = node.getBody().statements();
		SampleHandler.printMessage("nodeeeee:" + node.getBody().getParent().toString());
		String str = node.getBody().getParent().toString();
		str = str.replaceAll("\\s+","");
		if(str.contains("){")&&(str.contains("public") || str.contains("protected") || str.contains("private") )) {
			numberOfFlowSouceDeclared ++;
		}
		SampleHandler.printMessage("number of contains:" + numberOfFlowSouceDeclared);
			tryStatements.add(node);
		
		return super.visit(node);
	}
	public static HashSet<TryStatement> getTryStatements() {
		return tryStatements;	
	}
	public int getNumberOfFlowSouceDeclared() {
		return numberOfFlowSouceDeclared;
	}




}
