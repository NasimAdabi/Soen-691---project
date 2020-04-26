package tutorial691.patterns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691.handlers.SampleHandler;
import tutorial691.visitors.ExceptionVisitor;
import tutorial691.visitors.OverCatchVisitor;
import tutorial691.visitors.ThrowVisitor;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public class ExceptionFinder {
	
	private int count_ptrn1 = 0;
	private int count_ptrn2 = 0;
	private int count_ptrn3 = 0;
	public void findExceptions(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();

		for(IPackageFragment mypackage : packages){
			checkExceptions(mypackage);
		}
		SampleHandler.printMessage("Destructive Wrapping Anti-pattern Count: " + String.valueOf(count_ptrn1));
		SampleHandler.printMessage("Generic Throw Anti-pattern Count: " + String.valueOf(count_ptrn2));
		SampleHandler.printMessage("Over-Catch Anti-pattern Count: " + String.valueOf(count_ptrn3));
		
}
	
	private void checkExceptions(IPackageFragment packageFragment) throws JavaModelException {
		boolean shouldPrint = false;
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);
			
			//do method visit here and check stuff
			ExceptionVisitor exceptionVisitor = new ExceptionVisitor();
			ThrowVisitor throwVisitor = new ThrowVisitor();
			OverCatchVisitor catVisitor = new OverCatchVisitor();
			parsedCompilationUnit.accept(exceptionVisitor);
			parsedCompilationUnit.accept(throwVisitor);
			parsedCompilationUnit.accept(catVisitor);
			
			count_ptrn1 += exceptionVisitor.getCatchClauses().size();
			count_ptrn2 += throwVisitor.getThrowStatement().size();
			count_ptrn3 += catVisitor.getcatchStatement().size();
			
			if(shouldPrint) {
				printExceptions(exceptionVisitor);
				printThrow(throwVisitor);
				printCat(catVisitor);
				
			}
		}
		
		
	}
	
	private void printExceptions(ExceptionVisitor visitor) {
		for(CatchClause statement: visitor.getCatchClauses()) {
			SampleHandler.printMessage("Destructive Wrapping Anti-pattern: " + statement.toString());
		}
	}
	
	private void printThrow(ThrowVisitor visitor) {
		
		for(ThrowStatement statement: visitor.getThrowStatement()) {
			SampleHandler.printMessage("Generic Throw Anti-pattern: " + statement.toString());
			
		}
	}
	
	private void printCat(OverCatchVisitor visitor) {
		
		
		for(int i =0 ; i < visitor.getcatchStatement().size(); i++ ) {
			System.out.println(visitor.getcatchStatement().get(i).toString());
		}
	}
	
	
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
