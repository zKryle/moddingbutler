package moddingbutler.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileUtil {
	private static final String OS = System.getProperty("os.name").toUpperCase();

	public static boolean pathExists(String path) {
		return Files.exists(Paths.get(path)) && path.length() > 0;
	}

	public static boolean isExtention(String path, String ext) {
		return path.toLowerCase().endsWith("." + ext);
	}

	public static void copyFile(String inPath, String outPath) throws IOException {
		Path sourceFile = Paths.get(inPath);
		Files.copy(sourceFile, Paths.get(outPath).resolve(sourceFile.getFileName()));
	}

	public static void makeDir(String path) {
		File theDir = new File(path);
		if (!theDir.exists())
			theDir.mkdirs();
	}

	@SuppressWarnings("rawtypes")
	public static void unzip(String zipFile, String extractFolder) throws ZipException, IOException {
		int BUFFER = 2048;
		ZipFile zip = new ZipFile(new File(zipFile));
		String newPath = extractFolder;

		new File(newPath).mkdir();
		Enumeration zipFileEntries = zip.entries();

		while (zipFileEntries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			File destFile = new File(newPath, entry.getName());

			destFile.getParentFile().mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				int currentByte;
				byte data[] = new byte[BUFFER];
				BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER);
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		}
	}

	public static Process runCommand(String command, String loc) throws IOException {
		return Runtime.getRuntime().exec(terminalPrefix() + command, null, new File(loc));
	}

	private static String terminalPrefix() {
		if (isWindows())
			return "cmd /c ";
		if (isMac())
			return "/bin/bash -c ";
		if (isUnix())
			return "bash -c ";
		return "";
	}

	public static boolean isWindows() {
		return OS.contains("WIN");
	}

	public static boolean isMac() {
		return OS.contains("MAC");
	}

	public static boolean isUnix() {
		return OS.contains("NIX") || OS.contains("NUX") || OS.contains("AIX");
	}
}
