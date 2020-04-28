package tutorial691online.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class FlowTypePrevelanceVisitor extends ASTVisitor {
	private static HashSet<TryStatement> tryStatements = new HashSet<>();
	private int numberOfFlowTypePrevalance=0;
	private int noOfTryBlocks =0;
	@Override
	public boolean visit(TryStatement node) {
		tryStatements.add(node);
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryScope");
		node.accept(methodInvocationVisitor);
		noOfTryBlocks++;
		//List<Statement> bodyStatements = node.getBody().statements();
		SampleHandler.printMessage("nodeeeee:" + node.getBody().getParent().toString());
		String str = node.getBody().getParent().toString();
		String findStr = "catch";
		int lastIndex = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		    	numberOfFlowTypePrevalance ++;
		        lastIndex += findStr.length();
		    }
		}
		SampleHandler.printMessage("number of contains:" + numberOfFlowTypePrevalance);
			tryStatements.add(node);
		
		return super.visit(node);
	}
	public static HashSet<TryStatement> getTryStatements() {
		return tryStatements;	
	}
	public int getNumberOfFlowTypePrevalance() {
		return numberOfFlowTypePrevalance;
	}
	public int getNumberOfTryBlocks() {
		return noOfTryBlocks;
	}


}
