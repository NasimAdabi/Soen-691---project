package tutorial691.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ThrowStatement;


public class ThrowVisitor extends ASTVisitor{

	HashSet<ThrowStatement> throwStatement = new HashSet<>();

	@Override
	public boolean visit(ThrowStatement node) {
		node.accept(new ASTVisitor() {
			@Override
			public boolean visit(ClassInstanceCreation exceptionNode) {

				try {
					if (exceptionNode.getType().resolveBinding().getQualifiedName().equals("java.lang.Exception")) {
						throwStatement.add(node);
						System.out.println("Generic Throw Anti-pattern : " +  node.toString());

					}

				} catch (Exception ex) {
				}
				return super.visit(exceptionNode);
			}


		});

		return super.visit(node);
	}

	public HashSet<ThrowStatement> getThrowStatement() {
		return throwStatement;
	}

}
