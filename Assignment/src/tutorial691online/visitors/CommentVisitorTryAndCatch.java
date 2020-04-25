package tutorial691online.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691online.handlers.SampleHandler;

public class CommentVisitorTryAndCatch extends ASTVisitor{
	private static final Logger logger = LogManager.getLogger(CommentVisitorForTry.class.getName());
	private CompilationUnit tree;
	List<String> lineComments = new ArrayList<String>();
	List<String> blockComments = new ArrayList<String>();
	List<String> javaDoc = new ArrayList<String>();
	List<String> tryBlock = new ArrayList<String>();
	private Map<Integer, Integer> tryScope = new HashMap<Integer, Integer>();
	int commentInTryCount = 0;
	int commentInCatchCount = 0;
	List<String> catchBlock = new ArrayList<String>();
	private Map<Integer, Integer> catchScope = new HashMap<Integer, Integer>();
	
	@Override
	public boolean visit(CatchClause node){
		int catchStartPosition = tree.getLineNumber(node.getBody().getStartPosition());
		int catchEndPosition = tree.getLineNumber(node.getBody().getStartPosition()+node.getBody().getLength());
		//SampleHandler.printMessage("Visiting 'catchStatement' start line " + catchStartPosition + " end line " + catchEndPosition);
		//SampleHandler.printMessage("bodyyyyy:"+node.getBody());
		catchScope.put(catchStartPosition, catchEndPosition);
		catchBlock.add(node.getBody().toString());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TryStatement node){
		int tryStartPosition = tree.getLineNumber(node.getBody().getStartPosition());
		int tryEndPosition = tree.getLineNumber(node.getBody().getStartPosition()+node.getBody().getLength());
		//SampleHandler.printMessage("Visiting 'TryStatement' start line " + tryStartPosition + " end line " + tryEndPosition);
		
		tryScope.put(tryStartPosition, tryEndPosition);
		tryBlock.add(node.getBody().toString());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(LineComment node) {
		int startPosition = tree.getLineNumber(node.getStartPosition());
		
		//SampleHandler.printMessage("Visiting 'LineComment' at line " + startPosition);
		
		lineComments.add(node.toString());
		isCommentInTry(startPosition, startPosition);
		isCommentInCatch(startPosition, startPosition);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(BlockComment node) {
		int startPosition = tree.getLineNumber(node.getStartPosition());
		int endPosition = tree.getLineNumber(node.getStartPosition()+node.getLength());
		
		//SampleHandler.printMessage("Visiting 'BlockComment' at line " + startPosition + " end line " + endPosition);
	
		blockComments.add(node.toString());
		isCommentInTry(startPosition, endPosition);
		isCommentInCatch(startPosition, endPosition);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Javadoc node) {
		SampleHandler.printMessage("Visiting JavaDoc at line " + tree.getLineNumber(node.getStartPosition()) +
									" end line " + tree.getLineNumber(node.getStartPosition()+node.getLength()));
	
		javaDoc.add(node.toString());
		return super.visit(node);
	}
	
	public void setTree(CompilationUnit cu) {
		tree = cu;
	}
	
	public int getCommentInTryCount() {
		return commentInTryCount;
	}
	
	public int getCommentInCatchCount() {
		return commentInCatchCount;
	}
	
	private boolean isCommentInTry(int startPosition, int endPosition) {
		boolean result = false;
		//SampleHandler.printMessage("Comment start " + startPosition + " comment end " + endPosition);
		
		for(Entry<Integer, Integer> item : tryScope.entrySet()) {
			//SampleHandler.printMessage("try start " + startPosition + " try end " + endPosition);
			
			if(//Start of Comment Block
				startPosition > item.getKey() && startPosition < item.getValue() &&
				//End of Comment Block
				endPosition >= item.getKey() && endPosition <= item.getValue()) {
				
				//if comment is just a line
				if(startPosition == endPosition) {
					commentInTryCount++;
				}else {
					commentInTryCount = commentInTryCount + (endPosition - startPosition + 1);
				}
				
				result = true;
			}
		}
		
		return result;
	}
	
	private boolean isCommentInCatch(int startPosition, int endPosition) {
		boolean result = false;
		//SampleHandler.printMessage("Comment start " + startPosition + " comment end " + endPosition);
		
		for(Entry<Integer, Integer> item : catchScope.entrySet()) {
			//SampleHandler.printMessage("try start " + startPosition + " try end " + endPosition);
			
			if(//Start of Comment Block
				startPosition > item.getKey() && startPosition < item.getValue() &&
				//End of Comment Block
				endPosition >= item.getKey() && endPosition <= item.getValue()) {
				
				//if comment is just a line
				if(startPosition == endPosition) {
					commentInCatchCount++;
				}else {
					commentInCatchCount = commentInCatchCount + (endPosition - startPosition + 1);
				}
				
				result = true;
			}
		}
		
		return result;
	}
	
}
