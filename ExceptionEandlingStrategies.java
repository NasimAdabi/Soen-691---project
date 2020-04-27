package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ThrowStatement;

import java.awt.List;

import java.util.Iterator;

import org.eclipse.jdt.core.IPackageFragment;

import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;

import org.eclipse.jdt.core.dom.Type;

import tutorial691.handlers.SampleHandler;

public class ExceptionEandlingStrategies extends ASTVisitor {

	HashSet<TryStatement> tryStatement = new HashSet<>();
	HashSet<CatchClause> catchStatement = new HashSet<>();

	
	ArrayList<String> catchStatement2 = new ArrayList<>();
	

	///////////////////////////////////////////////////

	ArrayList<ThrowStatement> throwStatement = new ArrayList<>();

	@Override
	public boolean visit(ThrowStatement node) {
		node.accept(new ASTVisitor() {
			@Override
			public boolean visit(ClassInstanceCreation exceptionNode) {
				
				if (exceptionNode.getType().resolveBinding().getQualifiedName().equals("java.lang.Exception")) {
					throwStatement.add(node);
					
				
			}return super.visit(exceptionNode);}
		});

	return super.visit(node);
	}		
	
	public ArrayList<ThrowStatement> getThrowStatement() {
		return throwStatement;
	}
	
	
	

	
	/////////////////////////////////////////////////////
	@Override
	public boolean visit(TryStatement node) {
		
		ArrayList<CatchClause> localExceptions = new ArrayList<>();
		
	//////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////	

		for (Object catchClause : node.catchClauses()) {
			((ASTNode) catchClause).accept(new ASTVisitor() {
				@Override
				public boolean visit(CatchClause exceptionNode) {

//					
					
					localExceptions.add(exceptionNode);

					return super.visit(exceptionNode);
				}
			});

		}

		int count = 0;
		for (int i = 0; i < localExceptions.size(); i++) {

			for (int j = 0; j < throwStatement.size(); j++) {
				
//				if (i == j) {
//					continue;
//				}
				
				try {
					String exceptionName1 = throwStatement.get(j).getException().getType().resolveBinding().getQualifiedName().toString();
					String exceptionName2 = localExceptions.get(i).getException().getType().resolveBinding().getQualifiedName().toString();
					Class exceptionClass1 = Class.forName(exceptionName1); // try
					Class exceptionClass2 = Class.forName(exceptionName2);  // catch


					boolean overCatchFound = exceptionClass1.isAssignableFrom(exceptionClass2);
					if (overCatchFound) {
						System.out.println("Subsumption Strategy Anti-pattern : " + exceptionClass1 + " isAssignableFrom " + exceptionClass2);
						catchStatement2.add(exceptionClass1 + " -> " + exceptionClass2);
						count++;
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block

				}
				catch (NullPointerException e) {
					// TODO Auto-generated catch block

				}



			}


		}
		

		return super.visit(node);
	}

	public ArrayList<String> getcatchStatement() {
		return catchStatement2;
	}
	
	
	
}
