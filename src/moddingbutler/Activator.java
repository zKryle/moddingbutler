package moddingbutler;

import java.util.Arrays;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "ModdingButler";

	private static Activator plugin;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		initImages();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	protected void initImages() throws Exception {
		Arrays.asList(ImageResource.class.getFields()).stream().filter(f -> f.getType() == String.class).forEach(f -> {
			try {
				putImage((String) f.get(null));
			} catch (Exception e) {
				return;
			}
		});
	}

	private void putImage(String name) {
		getImageRegistry().put(name, new Image(Display.getCurrent(),
				Thread.currentThread().getContextClassLoader().getResourceAsStream("icons/" + name + ".png")));
	}

}
