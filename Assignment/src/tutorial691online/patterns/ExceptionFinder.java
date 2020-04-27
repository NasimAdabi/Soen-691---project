package tutorial691online.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691online.handlers.SampleHandler;
import tutorial691online.visitors.CatchClauseVisitor;
import tutorial691online.visitors.CommentVisitorTryAndCatch;
import tutorial691online.visitors.MethodInvocationVisitor;
import tutorial691online.visitors.MethodInvokeVisitor;
import tutorial691online.visitors.OverCatchVisitor;
import tutorial691online.visitors.ReturnStatementVisitor;
import tutorial691online.visitors.Throw1ClauseVisitor;
import tutorial691online.visitors.TryScopeVisitor;
import tutorial691online.visitors.TryVisitor;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public class ExceptionFinder {
	HashMap<MethodDeclaration, String> emptyCatches = new HashMap<>();
	HashMap<MethodDeclaration, String> dummyCatches = new HashMap<>();
	HashMap<MethodDeclaration, String> suspectMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> throwMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> catchMethods = new HashMap<>();
	HashMap<MethodDeclaration, String> kitchenSinkMethods = new HashMap<>();
	HashMap<TryStatement, String> tryBlocks = new HashMap<>();
	HashMap<MethodDeclaration, String> methodIvoke = new HashMap<>();
	HashMap<MethodDeclaration, String> tryScope = new HashMap<>();
	
	private int tryBlockCount = 0;
	private int tryBlockLOC = 0;
	private int tryBlockSLOC = 0;
	private int catchBlockCount = 0;
	private int catchBlockSLOC = 0;
	private int catchBlockLOC = 0;
	private ArrayList<String> tryBlockLOCStatements = new ArrayList<String>();
	private ArrayList<String> catchBlockLOCStatements = new ArrayList<String>();
	private int flowHandlingActionsCount = 0;
	private int incompleteDPCount = 0;
	private int catchReturnNullCount = 0;

	public void findExceptions(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
				// AST node
				CompilationUnit parsedCompilationUnit = parse(unit);

				//Pattern 1: log & throw
				// AND Exception Metrics: Flow Handling Actions
				CatchClauseVisitor catchVisitor = new CatchClauseVisitor();
				parsedCompilationUnit.accept(catchVisitor);
				// Give detail of detection
				getMethodsWithTargetCatchClauses(catchVisitor);
				flowHandlingActionsCount = catchVisitor.getActionStatements().size();
				//for (String actionStatement : exceptionVisitor.getActionStatements()) {
					//SampleHandler.printMessage("Actions Statement: " + actionStatement);
				//}
				catchBlockCount = catchVisitor.getCatchBlockCount();
				catchBlockLOC = catchVisitor.getTryBlockLOC();
				catchBlockLOCStatements = catchVisitor.getCatchBlockLOCStatements();
				// 'Catch & Return Null' Anti-pattern
				catchReturnNullCount = catchVisitor.catchReturnNullCount();
				
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
				tryBlockLOC = tryVisitor.getTryBlockLOC();
				tryBlockLOCStatements = tryVisitor.getTryBlockLOCStatements();
				
				//Exception Metrics: Try Size-SLOC & Catch Size-SLOC
				CommentVisitorTryAndCatch CommentVisitor = new CommentVisitorTryAndCatch(parsedCompilationUnit, unit.getSource().split("\n"));
				CommentVisitor.setTree(parsedCompilationUnit);
				parsedCompilationUnit.accept(CommentVisitor);
				
				for (Comment comment : (List<Comment>) parsedCompilationUnit.getCommentList()) {
					 comment.accept(CommentVisitor);
				 }
				tryBlockSLOC = CommentVisitor.getCommentInTryCount();
				//SampleHandler.printMessage("Satatementttttt:" + tryBlockLOCStatements);
				catchBlockSLOC = CommentVisitor.getCommentInCatchCount();
				//SampleHandler.printMessage("Satatementttttt:" + catchBlockLOCStatements);
				
				// For 'Incomplete Implementation' Anti-pattern
				incompleteDPCount = CommentVisitor.getToDoOrFixMeCommentsCount();
				
				printCharacteristicsMetrics(unit.getElementName());
		
				//Invoke method for each class
				SampleHandler.printMessage("-------- Invoke method in try clause for each class ------------");
				SampleHandler.printMessage("Class Name " + unit.getElementName());
				MethodInvokeVisitor numberOfMethodInvoked = new MethodInvokeVisitor();
				parsedCompilationUnit.accept(numberOfMethodInvoked);
				getMethodsWithTargetInvoke(numberOfMethodInvoked);
				SampleHandler.printMessage("Number of Invoke methods " + numberOfMethodInvoked.getNumberofMethodInvoke());
				
				//Try Scope for each class
				SampleHandler.printMessage("-------- Try Scope for each class ------------");
				SampleHandler.printMessage("Class Name " + unit.getElementName());
				TryScopeVisitor numberOfTryScope = new TryScopeVisitor();
				parsedCompilationUnit.accept(numberOfTryScope);
				getMethodsWithTargetTryScope(numberOfTryScope);
				//SampleHandler.printMessage("Number of Invoke methods " + numberOfTryScope.getNumberofMethodInvoke());
				
				
			}
		}
	}

	private void getMethodsWithTargetThrow1Clauses(Throw1ClauseVisitor throwUncheckedException) {
		// TODO Auto-generated method stub
		for (MethodInvocation methodInvocationStatement : Throw1ClauseVisitor.getmethodInvocationStatements()) {
			kitchenSinkMethods.put(findMethodForThrow1(methodInvocationStatement), "Throwing the Kitchen Sink");
		}
	}
	
	private void getMethodsWithTargetInvoke(MethodInvokeVisitor numberOfMethodInvoked) {
		// TODO Auto-generated method stub
		for (TryStatement methodInvocationStatement : MethodInvokeVisitor.getTryStatements()) {
			methodIvoke.put(findMethodForInvoke(methodInvocationStatement), "Method Invoke");
		}
	}
	
	private void getMethodsWithTargetTryScope(TryScopeVisitor numberOfTryScope) {
		// TODO Auto-generated method stub
		for (TryStatement methodInvocationStatement : TryScopeVisitor.getTryStatements()) {
			tryScope.put(findMethodForTryScope(methodInvocationStatement), "Try Scope");
		}
	}

	private void getMethodsWithTargetCatchClauses(CatchClauseVisitor catchClauseVisitor) {
		
		for(CatchClause emptyCatch: catchClauseVisitor.getEmptyCatches()) {
			emptyCatches.put(findMethodForCatch(emptyCatch), "EmptyCatch");
		}
		
		for(CatchClause dummyCatch: catchClauseVisitor.getDummyCatches()) {
			dummyCatches.put(findMethodForCatch(dummyCatch), "DummyCatch");
		}
		
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

	private MethodDeclaration findMethodForInvoke(TryStatement methodInvocationStatement) {
		return (MethodDeclaration) findParentMethodDeclaration(methodInvocationStatement);
	}
	private MethodDeclaration findMethodForTryScope(TryStatement trymethodInvoc) {
		return (MethodDeclaration) findParentMethodDeclaration(trymethodInvoc);
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
	
	public HashMap<MethodDeclaration, String> getemptyCatches() {
		return emptyCatches;
	}
	
	public void printExceptions() {

//		for (MethodDeclaration declaration : throwMethods.keySet()) {
//			String type = throwMethods.get(declaration);
//			SampleHandler.printMessage(
//					String.format("The following method suffers from the Throw & Log anti-pattern: %s", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
//		}
		
//		for (MethodDeclaration declaration : catchMethods.keySet()) {
//			String type = catchMethods.get(declaration);
//			SampleHandler.printMessage(
//					String.format("The following method suffers from the Over-Catch anti-pattern: %s", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
//		}

//		for (MethodDeclaration declaration : kitchenSinkMethods.keySet()) {
//			String type = kitchenSinkMethods.get(declaration);
//			SampleHandler.printMessage(String.format("The following method suffers from the %s anti-pattern", type));
//			if (declaration != null) {
//				SampleHandler.printMessage(declaration.toString());
//			}
//		}
		
		for (TryStatement tryBlock : tryBlocks.keySet()) {
			String type = tryBlocks.get(tryBlock);
			SampleHandler.printMessage(String.format("The following method is: ", type));
			if (tryBlock != null) {
				SampleHandler.printMessage(tryBlock.toString());
			}
		}
		
//		for(MethodDeclaration declaration : suspectMethods.keySet()) {
//			String type = suspectMethods.get(declaration);
//			SampleHandler.printMessage(String.format("The following method suffers from the %s pattern", type));
//			SampleHandler.printMessage(declaration.toString());
//		}
		
		SampleHandler.printMessage(String.format("Throw & Log anti-pattern Detected Count: %s", throwMethods.size()));
		SampleHandler.printMessage(String.format("Over-Catch anti-pattern Detected Count: %s", catchMethods.size()));
		SampleHandler.printMessage(
				String.format("Throwing the Kitchen Sink anti-pattern Detected Count: %s", kitchenSinkMethods.size()));
		SampleHandler.printMessage(String.format("Catch and Do Nothing(Empty Catch) anti-pattern Detected Count: %s", emptyCatches.size()));
		SampleHandler.printMessage(String.format("Dummy Handler anti-pattern Detected Count: %s", dummyCatches.size()));
		SampleHandler.printMessage(String.format("Method Invoke Detected Count: %s", methodIvoke.size()));
	}
	
	public void printCharacteristicsMetrics(String fileName){
		SampleHandler.printMessage("File name: " + fileName);
		SampleHandler.printMessage("Flow Handling Actions Count: " + flowHandlingActionsCount);
		SampleHandler.printMessage("Try Block Count:" + tryBlockCount);
		SampleHandler.printMessage("Try-LOC:" + tryBlockLOC);
		SampleHandler.printMessage("Try-SLOC:" + tryBlockSLOC);
		SampleHandler.printMessage("Catch Block Count:" + catchBlockCount);
		SampleHandler.printMessage("Catch-LOC:" + catchBlockLOC);
		SampleHandler.printMessage("Catch-SLOC:" + catchBlockSLOC);
		SampleHandler.printMessage("Incomplete Implementation anti-pattern Detected Count:" + incompleteDPCount);
		SampleHandler.printMessage("Catch & Return Null anti-pattern Detected Count:" + catchReturnNullCount);
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
