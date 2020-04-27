package tutorial691online.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import tutorial691online.handlers.SampleHandler;

public class MethodInvocationVisitor extends ASTVisitor{
	
	private static String[] LogMethods = {"log", "info", "warn", "error", "trace", "debug", "fatal"}; // "log statement"
    private static String[] PrintMethods = {"println", "print"}; // "print statement"
	private static String[] DefaultMethods = {"printStackTrace"}; // display statement
	private static String[] ThrowMethods = {"throw"}; // "throw statement"
	
	private int logPrintDefaultStatements = 0;
	private int thrownStatements = 0;
	private int numberofCheckedException =0;
	private int flowHandlingAction = 0;
	private int numberofMethodInvoke =0;
	private String statementAccordingToVisitorType;
	private String exceptionName;
	private ITypeBinding exceptionType;
	private String exceptionTypeName;
	private MethodInvocation currentNode;
	private MethodInvocation invokedMethodNode;
	private ArrayList<String> flowHandlingActionStatements = new ArrayList<String>();
	
	public MethodInvocationVisitor(String statement) {
		this.statementAccordingToVisitorType = statement;
	}
	public MethodInvocationVisitor() {
	}
	@Override
	public boolean visit(MethodInvocation node) {
		this.currentNode = node;
		
		//Log statement inside catch
		if(this.statementAccordingToVisitorType == "LogCatchSwitch"){ 
			
			String nodeName = node.getName().toString();
			//SampleHandler.printMessage("Invoked nodeName::::::" + nodeName);
			if (IsLoggingStatement(nodeName) || IsDefaultStatement(nodeName) || IsPrintStatement(nodeName)) {
				logPrintDefaultStatements += 1;
			}
			
			if(IsLoggingStatement(nodeName) && IsThrownStatement(nodeName)) {
				thrownStatements += 1;
			}

			//Flow Handling Actions: Method call/Log inside Catch
			if(IsLoggingStatement(nodeName) || isMethodCall(node)) {
				flowHandlingAction ++;
			}
		}
		if (this.statementAccordingToVisitorType == "TryBlock") {

			this.invokedMethodNode = node;
			IMethodBinding methodNode = node.resolveMethodBinding();
			// SampleHandler.printMessage("Invoked Method::::::" + node.getName());
			if (methodNode != null) {
				ITypeBinding[] exceptionBinding = methodNode.getExceptionTypes();
				for (ITypeBinding exception : exceptionBinding) {
					String exceptionName = exception.getQualifiedName();
					// Integer type = findKind(exception , node);
					this.exceptionName = exceptionName;
					// SampleHandler.printMessage("Throws exception::::::" + exceptionName);

					ITypeBinding exceptionType = exception.getTypeDeclaration();
					this.exceptionTypeName = exceptionType.getQualifiedName();
					this.exceptionType = exceptionType;
					// SampleHandler.printMessage("Invoked Typeeeeeeeee::::::" +
					// this.exceptionType.toString());
				}
			}

		}
		if(this.statementAccordingToVisitorType == "MethodInvoke") {
			this.invokedMethodNode = node;
			IMethodBinding methodNode = node.resolveMethodBinding();
			//SampleHandler.printMessage("Invoked Method::::::" + node.getName());
			numberofMethodInvoke++;
		}
		if(this.statementAccordingToVisitorType == "TryScope") {
			SampleHandler.printMessage("Invoked Method::::::" + node);
			
		}
		if(this.statementAccordingToVisitorType == "throwBlock") {
			IMethodBinding methodNode = node.resolveMethodBinding();
			if(methodNode != null) {
				ITypeBinding[] exceptionBinding = methodNode.getExceptionTypes();
				//SampleHandler.printMessage("Invoked Method Name::::::" + methodNode.getName());	
				//SampleHandler.printMessage("Number of exception::::::" + methodNode.getExceptionTypes().length);
				for(ITypeBinding exception : exceptionBinding) {
					//SampleHandler.printMessage("exception::::::" + exception.getName());
					numberofCheckedException++;
				}
			}
		}	

		return super.visit(node);
	}

	public boolean isMethodCall(MethodInvocation node) {
		IMethodBinding methodNode = currentNode.resolveMethodBinding();
		
		if(methodNode != null) {
			IMethodBinding binding = methodNode.getMethodDeclaration();
			
			Expression e = node.getExpression(); 
			if(e instanceof Name) {
				Name name = (Name) e;
				String type = name.resolveBinding() + "";
				//SampleHandler.printMessage("Object Name:" + n.resolveBinding().getName() + ", Method name:" + node.getName().getFullyQualifiedName());
				//SampleHandler.printMessage("type:" + name.resolveBinding());
				if(!type.contains("java.util.logging.Logger")) {
					flowHandlingActionStatements.add(binding.getName() + ", Action:'Method Call'");
				}
			}else {
				flowHandlingActionStatements.add(binding.getName() + ", Action:'Method Call'");
			}

			//if(binding != null) {
				//SampleHandler.printMessage("Type: "+ type.toString()+ ",method: " + methodName + ",calls:" + type.getFullyQualifiedName());	
				return true;
			//}
		}
		
		return false;
	}
	
	public MethodInvocation getInvokedMethod() {
		return invokedMethodNode;
	}
	
	public String getExceptionName() {
		return exceptionName;
	}

	public ITypeBinding getExceptionType() {
		return exceptionType;
	}
	
	public String getExceptionTypeName() {
		return exceptionTypeName;
	}
	
	public static String findParentType (ASTNode node){
		  
		  int parentNodeType = node.getParent().getNodeType();
		  
		  if(parentNodeType == ASTNode.TYPE_DECLARATION)
		  {
			  TypeDeclaration type = (TypeDeclaration) node.getParent();
			  if(type.resolveBinding() != null)
				  return type.resolveBinding().getQualifiedName();
			  else		  
				  return type.getName().getFullyQualifiedName();
		  }
		  
		  return findParentType(node.getParent());
	  }

	
	
    // To check whether an invocation is a Throw statement
    private static boolean IsThrownStatement(String statement)
    {
        if (statement == null) return false;
        for (String logmethod : ThrowMethods)
        {
            if (statement.indexOf(logmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
    
    // To check whether an invocation is a logging statement
    private static boolean IsLoggingStatement(String statement)
    {
        if (statement == null) return false;
        for (String logmethod : LogMethods)
        {
            if (statement.indexOf(logmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
    
    /// To check whether an invocation is a default statement
	private static boolean IsDefaultStatement(String statement)
	{
        if (statement == null) return false;
        for (String defaultmethod : DefaultMethods)
        {
            if (statement.indexOf(defaultmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }
	
    /// To check whether an invocation is a print statement
	private static boolean IsPrintStatement(String statement)
	{
        if (statement == null) return false;
        for (String defaultmethod : PrintMethods)
        {
            if (statement.indexOf(defaultmethod) > -1)
            {
                return true;
            }
        }
        return false;
    }

	public ArrayList<String> getFlowHandlingActions() {
		return flowHandlingActionStatements;
	}
	
	public int getLogPrintDefaultStatements() {
		return logPrintDefaultStatements;
	}
	
	public int getThrownStatements() {
		return thrownStatements;
	}
	
	public int getNumberofCheckedException() {
		return numberofCheckedException;
	}
	public int getNumberofMethodInvoke() {
		return numberofMethodInvoke;
	}
}
