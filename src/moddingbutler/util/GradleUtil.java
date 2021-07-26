package moddingbutler.util;

import org.eclipse.buildship.core.internal.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

@SuppressWarnings("restriction")
public class GradleUtil {
	public static void addGradleNature(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getProject().getDescription();
		projectDescription.setNatureIds(MathUtil.append(projectDescription.getNatureIds(), GradleProjectNature.ID));
		project.setDescription(projectDescription, new NullProgressMonitor());
	}
}
