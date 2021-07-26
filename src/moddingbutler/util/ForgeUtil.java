package moddingbutler.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class ForgeUtil {
	public static String getVersionFromMDK(String zipPath) throws Exception {
		// Get build.gradle string
		ZipFile zf = new ZipFile(zipPath);
		try {
			InputStream buildgradle = zf.getInputStream(zf.getEntry("build.gradle"));

			StringBuilder sb = new StringBuilder();
			for (int ch; (ch = buildgradle.read()) != -1;) {
				sb.append((char) ch);
			}
			zf.close();
			
			// Find forge version in build.gradle
			Matcher matcher = Pattern.compile("(?<=minecraft \'net.minecraftforge:forge:)1\\.[0-9\\.]*-[0-9\\.]*(?=\')")
					.matcher(sb.toString());
			if (matcher.find()) {
				return matcher.group(0);
			} else {
				throw new IOException("Could not find MC Version. Possibly invalid MDK.");
			}

		} catch (Exception e) {
			zf.close();
			throw new IOException("Invalid MDK given.");
		}

	}
}
