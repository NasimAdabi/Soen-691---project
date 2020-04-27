package tutorial691.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;

import tutorial691.patterns.ExceptionFinder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DetectException extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		detectInProjects(projects);

		System.out.println("DONE DETECTING");

		return null;
	}

	private void detectInProjects(IProject[] projects) {
		for (IProject project : projects) {
			SampleHandler.printMessage("Project: " + project.getName());
			System.out.println("DETECTING IN: " + project.getName());
			ExceptionFinder exceptionFinder = new ExceptionFinder();

			try {
				exceptionFinder.findExceptions(project);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			SampleHandler.printMessage("__________________________________");

		}
	}
}
