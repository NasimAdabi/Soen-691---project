package tutorial691.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CatchClause;
//import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.SimpleName;

public class ExceptionVisitor extends ASTVisitor{
	HashSet<CatchClause> catchClauses = new HashSet<>();
	
	@Override
	public boolean visit(CatchClause node) {
		Object caughtException = node.getException().resolveBinding();
		
		node.accept(new ASTVisitor() {
			@Override
			public boolean visit(ThrowStatement throwNode) {
				

				if (throwNode.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION) {
					ClassInstanceCreation exceptionNode = (ClassInstanceCreation) throwNode.getExpression();
					
					if(exceptionNode.arguments().size() == 0) {
						catchClauses.add(node);
						System.out.println("Dectructive Wrapping Anti-pattern : " +  node.toString());

					}
					
					if(exceptionNode.arguments().size() == 1) {
						if (exceptionNode.arguments().get(0).getClass().getTypeName().equals("org.eclipse.jdt.core.dom.StringLiteral")) {
							catchClauses.add(node);
							System.out.println("Dectructive Wrapping Anti-pattern : " +  node.toString());
						}
						else {
							try {
								
								Object passedException = ((SimpleName) exceptionNode.arguments().get(0)).resolveBinding();
								if(caughtException != passedException) {
									catchClauses.add(node);
									System.out.println("Dectructive Wrapping Anti-pattern : " +  node.toString());
								}									}
							catch(Exception ex) {}
						
						}
					}
					
					if(exceptionNode.arguments().size() > 1) {
						try {
							Object passedException = ((SimpleName) exceptionNode.arguments().get(1)).resolveBinding();
							if(caughtException != passedException) {
								catchClauses.add(node);
							}
						} catch (Exception e) {}
						
					}
					
				}
				
				if (throwNode.getExpression().getNodeType() == ASTNode.SIMPLE_NAME) {
					Object passedException = ((SimpleName) throwNode.getExpression()).resolveBinding();
					if(caughtException != passedException) {
						catchClauses.add(node);
					}	
				}
								
				return super.visit(node);
			}
		});
		
//		node.accept(new ASTVisitor() {
//			@Override
//			public boolean visit(MethodInvocation node) {
//				System.out.println(node);
//				return super.visit(node);
//			}
//		});
		
		return super.visit(node);
	}
	
	public HashSet<CatchClause> getCatchClauses() {
		return catchClauses;
	}
}





/*
Destructive wrapping:
It is defined on checked exceptions which are checked before compilation. 

When there is a catch and throw again, the original exception has to be passed to throw statement.
There are two ways to write a throw statement.
1- throw e
2- throw new Exception("Something went wrong ... ", e)

We care about the second way.
So, first we need to find catch clauses. then we have to find throw inside that catch clause and finally check
whether the original exception has been passed to new exception or not.

This is OK:
try { ... }
catch (Exception e) { throw e }

This is also OK:
try { ... }
catch (Exception e) { throw new Exception("Something went wrong ... ", e) }

This is wrong:
try { ... }
catch (Exception e) { throw new Exception("Something went wrong ... ") }

This is also wrong:
try { ... }
catch (Exception e) { throw new Exception("Something went wrong ... ", f) } // which is f is not equal to e


These are just the basic examples. 
What if we have method inside catch clause and throw an exception inside that method.
How about having a new try-catch statement inside catch clause.

There are two ways to throw an exception:

1- Pass an existing exception instance to throw
	In this case we have to check this instance is the same as caught exception
	
2- Initialize an exception in throw statement
	In this case there will be several constructor for initializing exception:
		First group (Always anti-pattern):
			1- 	Exception() 
			2-  Exception(String message)
			
		Second group (Could be anti-pattern if cause is not caught exception):
			3-  Exception(String message, Throwable cause)  
			4-  Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) 
			5-  Exception(Throwable cause)
		


 */
