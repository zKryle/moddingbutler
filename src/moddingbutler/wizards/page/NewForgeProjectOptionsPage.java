package moddingbutler.wizards.page;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import moddingbutler.util.FileUtil;

public class NewForgeProjectOptionsPage extends BaseWizardPage {

	public NewForgeProjectOptionsPage(ISelection selection) {
		super(selection);
	}

	@Override
	public String getPageTitle() {
		return "Forge Mod Options";
	}

	@Override
	public String getPageDescription() {
		return "Customise the creation of your forge mod.";
	}

	private Text modId;
	private Text displayName;
	private Text mdkPath;
	private Button radioOfficial;
	private Button radioSnapshot;
	private Button autoBuildGradle;
	private Button autoModsToml;
	private Button autoMain;

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setFont(parent.getFont());

		createInfoGroup(container);
		createMdkGroup(container);
		createMappingsGroup(container);
		createExtraOptions(container);

		// Finish
		initialize();
		dialogChanged();
		setControl(container);
	}

	private final void createInfoGroup(Composite parent) {
		// Layout
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Row 1
		Label label = new Label(container, SWT.NULL);
		label.setText("&Mod ID:");

		modId = new Text(container, SWT.BORDER | SWT.SINGLE);
		modId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		modId.addModifyListener(e -> dialogChanged());

		// Row 2
		label = new Label(container, SWT.PUSH);
		label.setText("&Display Name:");

		displayName = new Text(container, SWT.BORDER | SWT.SINGLE);
		displayName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		displayName.addModifyListener(e -> dialogChanged());
	}

	private final void createMdkGroup(Composite parent) {
		// Layout
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Row 3
		Label label = new Label(container, SWT.NULL);
		label.setText("&Forge MDK:");

		mdkPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		mdkPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mdkPath.addModifyListener(e -> dialogChanged());

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
	}

	private final void createMappingsGroup(Composite parent) {
		// Layout
		Composite topComp = new Composite(parent, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 1;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		topComp.setLayout(innerLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		topComp.setLayoutData(gd);

		// Row 4
		Group group = new Group(topComp, SWT.NONE);
		group.setText("Choose the mappings channel:");
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 8;
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}
		};

		radioOfficial = new Button(group, SWT.RADIO | SWT.LEFT);
		radioOfficial.setText("Official (Mojmaps)");
		radioOfficial.addSelectionListener(listener);

		radioSnapshot = new Button(group, SWT.RADIO | SWT.LEFT);
		radioSnapshot.setText("Snapshot (MCP)");
		radioSnapshot.addSelectionListener(listener);
	}

	private final void createExtraOptions(Composite parent) {
		// Layout
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Row 5
		autoBuildGradle = new Button(container, SWT.CHECK | SWT.RIGHT);
		autoBuildGradle.setText("Auto-generate build.gradle");
		autoBuildGradle.setSelection(true);
		
		// Row 6
		autoModsToml = new Button(container, SWT.CHECK | SWT.RIGHT);
		autoModsToml.setText("Auto-generate mods.toml");
		autoModsToml.setSelection(true);
		
		// Row 7
		autoMain = new Button(container, SWT.CHECK | SWT.RIGHT);
		autoMain.setText("Auto-generate main class");
		autoMain.setSelection(true);
	}

	private void initialize() {
	}

	private void dialogChanged() {
		String modId = getModId();
		String displayName = getDisplayName();
		String mdk = getMdkPath();
		String mappings = getMappingsChannel();

		if (modId.length() == 0) {
			updateStatus("Mod ID must be specified.");
			return;
		}
		if (!Pattern.matches("[a-z0-9_]*", modId)) {
			updateStatus("Mod ID must only contain lowercase, numbers and _.");
			return;
		}
		if (displayName.length() == 0) {
			updateStatus("Display Name must be specified.");
			return;
		}
		if (!FileUtil.pathExists(mdk)) {
			updateStatus("Please select an MDK.");
			return;
		}
		if (!FileUtil.isExtention(mdk, "zip")) {
			updateStatus("MDK must be a zip file.");
			return;
		}
		if (mappings == null) {
			updateStatus("Mappings Channel must be specified.");
			return;
		}

		updateStatus(null);
	}

	private void handleBrowse() {
		FileDialog dialog = new FileDialog(mdkPath.getShell());
		dialog.setText("Select the Forge MDK.");
		dialog.setFilterExtensions(new String[] { "*.zip" });

		String mdkName = getMdkPath().trim();
		if (!mdkName.equals("")) {
			File path = new File(mdkName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(mdkName).toOSString());
			}
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			mdkPath.setText(selectedDirectory);
		}
	}

	public String getModId() {
		return modId.getText().trim();
	}

	public String getDisplayName() {
		return displayName.getText().trim();
	}

	public String getMdkPath() {
		return mdkPath.getText().trim();
	}

	public String getMappingsChannel() {
		if (radioOfficial.getSelection())
			return "official";
		if (radioSnapshot.getSelection())
			return "snapshot";
		return null;
	}
	
	public boolean isGenerateBuildGradle() {
		return autoBuildGradle.getSelection();
	}
	
	public boolean isGenerateModsToml() {
		return autoModsToml.getSelection();
	}
	
	public boolean isGenerateMainClass() {
		return autoMain.getSelection();
	}
}