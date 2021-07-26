package moddingbutler.wizards.page;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;

public abstract class BaseWizardPage extends WizardPage {
	protected ISelection selection;

	public BaseWizardPage(ISelection selection) {
		super("newWizardPage");
		setTitle(getPageTitle());
		setDescription(getPageDescription());
		this.selection = selection;
	}

	protected void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public ISelection getSelection() {
		return selection;
	}

	public abstract String getPageTitle();

	public abstract String getPageDescription();
}
