package tutorial691online.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;

public class ReturnStatementVisitor extends ASTVisitor{
	List<String> returnStatements = new ArrayList<String>();
	
	@Override
	public boolean visit(ReturnStatement node) {
		returnStatements.add(node.toString());

		return super.visit(node);
	}
	
	public List<String> getReturnStatements() {
		return returnStatements;
	}
}
