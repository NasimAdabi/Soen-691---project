package tutorial691online.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class TryVisitor extends ASTVisitor {
	private static HashSet<TryStatement> tryStatements = new HashSet<>();
	private int tryBlockCount = 0;
	private int tryBlockLOC = 0;
	private ArrayList<String> tryBlockLOCStatements = new ArrayList<String>();
	
//	@Override
//	public boolean visit(ContinueStatement node){
//		SampleHandler.printMessage("BLOCKKKK:" + node);
//		
//		return super.visit(node);
//	}
//	
	@Override
	public boolean visit(TryStatement node){
		tryStatements.add(node);
		tryBlockCount++;
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("TryBlock");
		node.accept(methodInvocationVisitor);
		
		List<Statement> bodyStatements = node.getBody().statements();
		//SampleHandler.printMessage("nodeeeee:" + node);
		for (Statement st : bodyStatements) {
			tryBlockLOCStatements.add(st.toString());
			tryBlockLOC++;
		}

		//SampleHandler.printMessage("Commentttttt:" + getCommentLineCount(bodyStatements));
		
		return super.visit(node);
	}

	public static HashSet<TryStatement> getTryBlocks(){
		return tryStatements;
	}
	
	public ArrayList<String> getTryBlockLOCStatements(){
		return tryBlockLOCStatements;
	}
	
	public int getTryBlockCount() {
		return tryBlockCount;
	}
	
	public int getTryBlockLOC() {
		return tryBlockLOC;
	}
	
}
