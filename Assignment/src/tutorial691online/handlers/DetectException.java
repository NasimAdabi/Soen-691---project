package tutorial691online.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691online.patterns.ExceptionFinder;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DetectException extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		Map<String, Integer> metricTrySizeLOC = new HashMap<String, Integer>();
		Map<String, Integer> metricTryBlockCount = new HashMap<String, Integer>();
		Map<String, Integer> metricTryBlockSLOC = new HashMap<String, Integer>();
		Map<String, Integer> metricCatchBlockSLOC = new HashMap<String, Integer>();
		
		Map<String, Integer> metricFlowHandlingActionsCount = new HashMap<String, Integer>();
		Map<String, Integer> metricCatchBlockCount = new HashMap<String, Integer>();
		Map<String, Integer> metricCatchBlockLOC = new HashMap<String, Integer>();
		Map<String, Integer> metricCatchReturnNullCount = new HashMap<String, Integer>();
		
		Map<String, Integer> metricIncompleteImplementationCount = new HashMap<String, Integer>();
		Map<String, Integer> metricInvokedMethodsCount = new HashMap<String, Integer>();
		Map<String, Integer> metricCatchAndDoNothing = new HashMap<String, Integer>();
		Map<String, Integer> metricDummyCatch = new HashMap<String, Integer>();
		Map<String, Integer> metricLogAndThrow = new HashMap<String, Integer>();
		Map<String, Integer> metricOverCatch = new HashMap<String, Integer>();
		Map<String, Integer> metricThrowKitchenSink = new HashMap<String, Integer>();
		Map<String, Integer> metricTryScope = new HashMap<String, Integer>();
		Map<String, Integer> metricFlowTypePrevalance = new HashMap<String, Integer>();
		Map<String, Integer> metricFlowQuantity = new HashMap<String, Integer>();
		for(IProject project : projects) {
			SampleHandler.printMessage("DETECTING IN: " + project.getName());
			ExceptionFinder exceptionFinder = new ExceptionFinder();
			
			try {
				// find the exceptions and print the methods that contain the exceptions
				exceptionFinder.findExceptions(project);
//				metricTrySizeLOC = exceptionFinder.getProject_Metric_TrySizeLOC();
//				metricTryBlockCount = exceptionFinder.getProject_Metric_TryBlockCount();
//				metricTryBlockSLOC = exceptionFinder.getProject_Metric_TryBlockSLOC();
				
//				metricCatchBlockSLOC = exceptionFinder.getProject_Metric_CatchBlockSLOC();
//				metricFlowHandlingActionsCount = exceptionFinder.getProject_Metric_FlowHandlingActionsCount();
//				metricCatchBlockCount = exceptionFinder.getProject_Metric_CatchBlockCount();
//				metricCatchBlockLOC = exceptionFinder.getProject_Metric_CatchBlockLOC();
//				metricCatchReturnNullCount = exceptionFinder.getProject_Metric_CatchReturnNullCount();
				
//				metricIncompleteImplementationCount = exceptionFinder.getProject_Metric_IncompleteImplementationCount();
//				metricInvokedMethodsCount = exceptionFinder.getProject_Metric_InvokedMethodsCount();
//				metricCatchAndDoNothing = exceptionFinder.getProject_Metric_CatchAndDoNothing();
//				metricDummyCatch = exceptionFinder.getProject_Metric_DummyCatch();
//				metricLogAndThrow = exceptionFinder.getProject_Metric_LogAndThrow();
//				metricOverCatch = exceptionFinder.getProject_Metric_OverCatch();
//				metricThrowKitchenSink = exceptionFinder.getProject_Metric_ThrowKitchenSink();
//				metricTryScope = exceptionFinder.getProject_Metric_TryScope();
//				metricFlowTypePrevalance = exceptionFinder.getProject_Metric_FlowTypePrevalance();
				metricFlowQuantity = exceptionFinder.getProject_Metric_FlowQuantity();
				
				exceptionFinder.printExceptions();			
				
			} catch (JavaModelException | URISyntaxException e) {
				e.printStackTrace();
			}	
			
		}
		//SampleHandler.printMessage("metricTrySizeLOC sizeeeee:"+metricTrySizeLOC.size());
		//SampleHandler.printMessage("metricTryBlockCount sizeeeee:"+metricTryBlockCount.size());
		//SampleHandler.printMessage("Metric TrySLOC sizeeeee:"+metricTryBlockSLOC.size());
		//SampleHandler.printMessage("Metric CatchSLOC sizeeeee:"+metricCatchBlockSLOC.size());
		
//		createCSVMetrics("TrySizeLOC", metricTrySizeLOC);
//		createCSVMetrics("TryQuantity", metricTryBlockCount);
//		createCSVMetrics("TrySLOC", metricTryBlockSLOC);
//		createCSVMetrics("CatchSizeSLOC", metricCatchBlockSLOC);
//		createCSVMetrics("FlowHandlingActions", metricFlowHandlingActionsCount);
//		createCSVMetrics("CatchQuantity", metricCatchBlockCount);
//		createCSVMetrics("CatchSizeLOC", metricCatchBlockLOC);
//		createCSVMetrics("CatchAndReturnNull_AntiPattern", metricCatchReturnNullCount);
//		createCSVMetrics("CatchAndDoNothing_AntiPattern", metricCatchAndDoNothing);
//		createCSVMetrics("DummyCatch_AntiPattern", metricDummyCatch);
//		createCSVMetrics("LogAndThrow_AntiPattern", metricLogAndThrow);
//		createCSVMetrics("OverCatch_AntiPattern", metricOverCatch);
//		createCSVMetrics("ThrowKitchenSink_AntiPattern", metricThrowKitchenSink);
//		createCSVMetrics("TryScope", metricTryScope);
//		createCSVMetrics("FlowTypePrevalance", metricFlowTypePrevalance);
		createCSVMetrics("FlowQuantity", metricFlowQuantity);
		
		SampleHandler.printMessage("DONE DETECTING");
		return null;
	}
	
	public static void createCSVMetrics(String fileName, Map<String, Integer> value) {
		
		CSVCreator csvCreator = new CSVCreator();
		
		try {
			csvCreator.createCSV(fileName, value);
			
		} catch (URISyntaxException e) {
			
			SampleHandler.printMessage("I have a problem in 'createCSVMetrics' method, help me !");
			e.printStackTrace();
		}
		
	}
}