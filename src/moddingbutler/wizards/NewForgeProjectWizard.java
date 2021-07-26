package moddingbutler.wizards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import moddingbutler.ImageResource;
import moddingbutler.util.FileUtil;
import moddingbutler.util.ForgeUtil;
import moddingbutler.util.GradleUtil;
import moddingbutler.wizards.page.NewForgeProjectOptionsPage;

public class NewForgeProjectWizard extends BaseNewProjectWizard {

	protected NewForgeProjectOptionsPage optionsPage;

	@Override
	public void addPages() {
		super.addPages();

		optionsPage = new NewForgeProjectOptionsPage(selection);
		this.addPage(optionsPage);
	}

	@Override
	public String getWindowTitle() {
		return "Create a new Forge project";
	}

	@Override
	public String getProjectTitle() {
		return "Forge Project";
	}

	@Override
	public String getProjectDescription() {
		return "Create a Forge mod with the use of Modding Butler.";
	}

	@Override
	public String getProjectImage() {
		return ImageResource.ICON_PROJECT_FORGE;
	}

	@Override
	public boolean performFinish() {
		try {
			String modid = optionsPage.getModId();
			String displayName = optionsPage.getDisplayName();
			String mdkPath = optionsPage.getMdkPath();
			String mappingsChannel = optionsPage.getMappingsChannel();
			String forgeVersion = ForgeUtil.getVersionFromMDK(mdkPath);

			StringBuilder sb = new StringBuilder();
			sb.append("Mod ID: \t\t" + modid + "\n");
			sb.append("Display Name: \t" + displayName + "\n");
			sb.append("MDK Path: \t" + mdkPath + "\n");
			sb.append("Mappings Channel: \t" + mappingsChannel + "\n");
			sb.append("Forge Version: \t" + forgeVersion + "\n");

			MessageDialog requestRestartDialog = new MessageDialog(this.getShell(), "Confirm Options", getImage(),
					sb.toString(), MessageDialog.CONFIRM, new String[] { "Confirm", "Cancel" }, 0);
			int index = requestRestartDialog.open();

			if (index == 0) {
				createProject();

				if (newProject == null)
					return false;

				IWorkingSet[] workingSets = mainPage.getSelectedWorkingSets();
				getWorkbench().getWorkingSetManager().addToWorkingSets(newProject, workingSets);
				BasicNewResourceWizard.selectAndReveal(newProject, getWorkbench().getActiveWorkbenchWindow());
			}

		} catch (Exception e) {
			MessageDialog errorDialog = new MessageDialog(this.getShell(), "Error", getImage(), e.getMessage(),
					MessageDialog.OK, new String[] { "Close" }, 0);
			errorDialog.open();
		}

		return true;
	}

	public IProject createProject() {
		final IProject newProject = mainPage.getProjectHandle();

		IPath defaultPath = Platform.getLocation();
		IPath newPath = mainPage.getLocationPath();
		if (defaultPath.equals(newPath)) {
			newPath = null;
		} else {
			IPath withName = defaultPath.append(newProject.getName());
			if (newPath.toFile().equals(withName.toFile())) {
				newPath = null;
			}
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProject.getName());
		description.setLocation(newPath);

		final String mdkPath = optionsPage.getMdkPath();
		final String projectLocation = mainPage.getLocationPath().toOSString() + "\\"
				+ (mainPage.useDefaults() ? mainPage.getProjectName() + "\\" : "");
		System.out.println(projectLocation);

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Setup forge mod.", 220);

				// Copy MDK
				monitor.setTaskName("Copying MDK to destination");
				try {
					FileUtil.makeDir(projectLocation);
					FileUtil.unzip(mdkPath, projectLocation);
				} catch (Exception e) {
					throw new RuntimeException("Error copying MDK to destination.");
				}
				monitor.worked(20);

				// Run commands
				monitor.setTaskName("Generating Eclipse Runs: Starting Daemon");
				try {
					Process process = FileUtil.runCommand("gradlew genEclipseRuns --refresh-dependencies",
							projectLocation);
					InputStream stdout = process.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
					while (process.isAlive()) {
						String line = reader.readLine();
						if (line == null)
							continue;
						if (line.contains("> Configure")) {
							monitor.setTaskName("Generating Eclipse Runs: Configuring Project");
						} else if (line.contains("> Task :")) {
							monitor.setTaskName("Generating Eclipse Runs: " + line.replace("> Task :", ""));
						}
					}

					int result = process.exitValue();

					if (result != 0) {
						throw new RuntimeException("Error running gradlew commands.");
					}
					
					monitor.worked(100);

					monitor.setTaskName("Applying Eclispe Plugin: Starting Daemon");
					process = FileUtil.runCommand("gradlew eclipse", projectLocation);
					stdout = process.getInputStream();
					reader = new BufferedReader(new InputStreamReader(stdout));
					while (process.isAlive()) {
						String line = reader.readLine();
						if (line == null)
							continue;
						if (line.contains("> Configure")) {
							monitor.setTaskName("Applying Eclispe Plugin: Configuring Project");
						} else if (line.contains("> Task :")) {
							monitor.setTaskName("Applying Eclispe Plugin: " + line.replace("> Task :", ""));
						}
					}

					result = process.exitValue();

					if (result != 0) {
						throw new RuntimeException("Error running gradlew commands.");
					}
					
					monitor.worked(100);

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error running gradlew commands.");
				}

			}
		};

		IRunnableWithProgress op2 = monitor -> {
			CreateProjectOperation op1 = new CreateProjectOperation(description, "New Forge Project");
			try {
				op1.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
			} catch (ExecutionException e) {
				throw new InvocationTargetException(e);
			}
		};

		try {
			getContainer().run(true, true, op);
			getContainer().run(true, true, op2);
			GradleUtil.addGradleNature(newProject);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				if (((CoreException) t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					MessageDialog.openError(getShell(), "Unable to create project",
							"Another project with the same name (and different case) already exists.");
				} else {
					ErrorDialog.openError(getShell(), "Unable to create project", null,
							((CoreException) t).getStatus());
				}
			} else {
				MessageDialog.openError(getShell(), "Unable to create project", t.getMessage());
			}
			e.printStackTrace();
			return null;
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Unable to add gradle nature.", e.getMessage());
		}

		return newProject;
	}
}