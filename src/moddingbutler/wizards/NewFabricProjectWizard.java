package moddingbutler.wizards;

import moddingbutler.ImageResource;

public class NewFabricProjectWizard extends BaseNewProjectWizard {

	@Override
	public String getWindowTitle() {
		return "Create a new Fabric project";
	}

	@Override
	public String getProjectTitle() {
		return "Fabric Project";
	}

	@Override
	public String getProjectDescription() {
		return "Create a Fabric mod with the use of Modding Butler.";
	}

	@Override
	public String getProjectImage() {
		return ImageResource.ICON_PROJECT_FABRIC;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}