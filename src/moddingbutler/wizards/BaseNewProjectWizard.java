package moddingbutler.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import moddingbutler.Activator;

public abstract class BaseNewProjectWizard extends Wizard implements INewWizard {

	protected WizardNewProjectCreationPage mainPage;
	protected IWorkbench workbench;
	protected IStructuredSelection selection;
	protected IProject newProject;

	public BaseNewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public IWorkbench getWorkbench() {
		return workbench;
	}

	public IStructuredSelection getSelection() {
		return selection;
	}

	@Override
	public void addPages() {
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") {
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup((Composite) getControl(), getSelection(),
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
				Dialog.applyDialogFont(getControl());
			}
		};
		mainPage.setTitle(getProjectTitle());
		mainPage.setDescription(getProjectDescription());
		this.addPage(mainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		this.workbench = workbench;
		this.selection = currentSelection;

		initializeDefaultPageImageDescriptor();
		setNeedsProgressMonitor(true);
		setWindowTitle(getWindowTitle());
	}

	protected void initializeDefaultPageImageDescriptor() {
		setDefaultPageImageDescriptor(ImageDescriptor.createFromImage(getImage()));
	}

	protected Image getImage() {
		return Activator.getDefault().getImageRegistry().get(getProjectImage());
	}

	public abstract String getWindowTitle();

	public abstract String getProjectTitle();

	public abstract String getProjectDescription();

	public abstract String getProjectImage();

}
