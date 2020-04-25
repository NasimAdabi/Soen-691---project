	package tutorial691online.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691online.handlers.SampleHandler;
import tutorial691online.visitors.CatchClauseVisitor;
import tutorial691online.visitors.CommentVisitor;
import tutorial691online.visitors.CommentVisitorForTry;
import tutorial691online.visitors.OverCatchVisitor;
import tutorial691online.visitors.Throw1ClauseVisitor;
import tutorial691online.visitors.ThrowsClauseVisitor;
import tutorial691online.visitors.TryVisitor;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class ExceptionFinder {
	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> throwMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> catchMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> kitchenSinkMethods = new HashMap<>();
	HashMap<TryStatement, String> tryBlocks = new HashMap<>();
	
	private int tryBlockCount = 0;
	private int tryBlockLOC = 0;
	private int tryBlockSLOC = 0;
	private ArrayList<String> tryBlockLOCStatements = new ArrayList<String>();
	private int flowHandlingActionsCount = 0;

	public void findExceptions(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
				// AST node
				CompilationUnit parsedCompilationUnit = parse(unit);

				//Pattern 1: log & throw
				// AND Exception Metrics: Flow Handling Actions
				CatchClauseVisitor exceptionVisitor = new CatchClauseVisitor();
				parsedCompilationUnit.accept(exceptionVisitor);
				// Give detail of detection
                getMethodsWithTargetCatchClauses(exceptionVisitor);
				flowHandlingActionsCount = exceptionVisitor.getActionStatements().size();
				//for (String actionStatement : exceptionVisitor.getActionStatements()) {
					//SampleHandler.printMessage("Actions Statement: " + actionStatement);
				//}
				
				// Pattern 3: overcatch
				OverCatchVisitor overCatchVisitor = new OverCatchVisitor();
				parsedCompilationUnit.accept(overCatchVisitor);
				getMethodsWithTargetTryClauses(overCatchVisitor);

				//Pattern 2 : Kitchen Sink
				Throw1ClauseVisitor throwUncheckedException1 = new Throw1ClauseVisitor();
				parsedCompilationUnit.accept(throwUncheckedException1);
				getMethodsWithTargetThrow1Clauses(throwUncheckedException1);
				
				//Exception Metrics: Try Quantity & Try Size-LOC
				TryVisitor tryVisitor = new TryVisitor();
				parsedCompilationUnit.accept(tryVisitor);
				//getMethodsWithTryBlock(tryVisitor);
				tryBlockCount = tryVisitor.getTryBlockCount();
				tryBlockLOC = tryVisitor.getTryBlockSLOC();
				tryBlockLOCStatements = tryVisitor.getTryBlockLOCStatements();
				
				//Exception Metrics: Try Size-SLOC
				CommentVisitorForTry CommentVisitorForTry = new CommentVisitorForTry();
				CommentVisitorForTry.setTree(parsedCompilationUnit);
				parsedCompilationUnit.accept(CommentVisitorForTry);
				
				for (Comment comment : (List<Comment>) parsedCompilationUnit.getCommentList()) {
					 comment.accept(CommentVisitorForTry);
				 }
				tryBlockSLOC = CommentVisitorForTry.getCommentInTryCount();
				//SampleHandler.printMessage("Satatementttttt:" + tryBlockLOCStatements);
				
				
				printCharacteristicsMetrics(unit.getElementName());
			}
		}
	}

	private void getMethodsWithTargetThrow1Clauses(Throw1ClauseVisitor throwUncheckedException) {
		// TODO Auto-generated method stub
		for (MethodInvocation methodInvocationStatement : Throw1ClauseVisitor.getmethodInvocationStatements()) {
			kitchenSinkMethods.put(findMethodForThrow1(methodInvocationStatement), "Throwing the Kitchen Sink");
		}
	}

	private void getMethodsWithTargetCatchClauses(CatchClauseVisitor catchClauseVisitor) {
		for (CatchClause throwStatement : catchClauseVisitor.getThrowStatements()) {
			// suspectMethods.put(findMethodForThrow(throwStatement), "throwStatement");
			throwMethods.put(findMethodForThrow(throwStatement), "LogThrow");
		}
	}

	private void getMethodsWithTargetTryClauses(OverCatchVisitor overCatchVisitor) {
		for (CatchClause catchblock : overCatchVisitor.getCatchBlocks()) {
			// suspectMethods.put(findMethodForCatch(catchblock), "Over-Catch");
			catchMethods.put(findMethodForCatch(catchblock), "Over-Catch");
		}
	}
	
	private ASTNode findParentMethodDeclaration(ASTNode node) {
		if (node != null && node.getParent() != null) {
			if (node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
				return node.getParent();
			} else {
				return findParentMethodDeclaration(node.getParent());
			}
		}
		return null;
	}

	private ASTNode findParentTryBlock(ASTNode node) {
		if (node != null && node.getParent() != null) {
			if (node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
				return node.getParent();
			} else {
				return findParentMethodDeclaration(node.getParent());
			}
		}
		return null;
	}
	
	private MethodDeclaration findMethodForThrow(CatchClause throwStatement) {
		return (MethodDeclaration) findParentMethodDeclaration(throwStatement);
	}

	private MethodDeclaration findMethodForCatch(CatchClause catchStatement) {
		return (MethodDeclaration) findParentMethodDeclaration(catchStatement);
	}

	private MethodDeclaration findMethodForThrow1(MethodInvocation methodInvoc) {
		return (MethodDeclaration) findParentMethodThrow1Declaration(methodInvoc);
	}

	private ASTNode findParentMethodThrow1Declaration(ASTNode node) {
		// TODO Auto-generated method stub
		if (node != null && node.getParent() != null) {
			if (node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
				return node.getParent();
			} else {
				return findParentMethodThrow1Declaration(node.getParent());
			}
		} else
			return null;
	}

	public HashMap<MethodDeclaration, String> getSuspectMethods() {
		return suspectMethods;
	}

	public void printExceptions() {

		for (MethodDeclaration declaration : throwMethods.keySet()) {
			String type = throwMethods.get(declaration);
//			SampleHandler.printMessage(
//					String.format("The following method suffers from the Throw & Log anti-pattern: %s", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
		}
		for (MethodDeclaration declaration : catchMethods.keySet()) {
			String type = catchMethods.get(declaration);
//			SampleHandler.printMessage(
//					String.format("The following method suffers from the Over-Catch anti-pattern: %s", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
		}

		for (MethodDeclaration declaration : kitchenSinkMethods.keySet()) {
//			String type = kitchenSinkMethods.get(declaration);
//			SampleHandler.printMessage(String.format("The following method suffers from the %s anti-pattern", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
		}
		
		for (TryStatement tryBlock : tryBlocks.keySet()) {
			String type = tryBlocks.get(tryBlock);
			SampleHandler.printMessage(String.format("The following method is: ", type));
			if (tryBlock != null) {
				SampleHandler.printMessage(tryBlock.toString());
			}
		}
		
		SampleHandler.printMessage(String.format("Throw & Log anti-pattern Detected Count: %s", throwMethods.size()));
		SampleHandler.printMessage(String.format("Over-Catch anti-pattern Detected Count: %s", catchMethods.size()));
		SampleHandler.printMessage(
				String.format("Throwing the Kitchen Sink anti-pattern Detected Count: %s", kitchenSinkMethods.size()));
	
	}
	
	public void printCharacteristicsMetrics(String fileName){
		SampleHandler.printMessage("File name: " + fileName);
		SampleHandler.printMessage("Flow Handling Actions Count: " + flowHandlingActionsCount);
		SampleHandler.printMessage("Try Block Count:" + tryBlockCount);
		SampleHandler.printMessage("Try-LOC:" + tryBlockLOC);
		SampleHandler.printMessage("Try-SLOC:" + tryBlockSLOC);
		
	}

	public static CompilationUnit parse(ICompilationUnit unit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
