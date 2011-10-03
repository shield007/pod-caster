package org.stanwood.podcaster.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHelper {

	private final static Log log = LogFactory.getLog(FileHelper.class);
	
	/** A Line separator property value */
	public final static String LS = System.getProperty("line.separator");
	
	/** Stores the current users home directory */
	public final static File HOME_DIR = new File(System.getProperty("user.home")); //$NON-NLS-1$

	/**
	 * This will create a temporary directory using the given name.
	 *
	 * @param name The name of the directory to create
	 * @return A file object pointing to the directory that was created
	 * @throws IOException Thrown if their is a problme creating the directory
	 */
	public static File createTmpDir(String name) throws IOException {
		File dir = createTempFile(name, ""); //$NON-NLS-1$
		if (!dir.delete()) {
			throw new IOException(MessageFormat.format("Unable to delete file ''{0}''",dir.getAbsolutePath())); //$NON-NLS-1$
		}
		if (!dir.mkdir()) {
			throw new IOException(MessageFormat.format("Unable to create directory ''{0}''",dir.getAbsolutePath())); //$NON-NLS-1$
		}

		return dir;
	}
	
	public static String getName(File file) {
		String name = file.getName();
		return name.substring(0,name.indexOf('.'));
	}

	public static String getExtension(File file) {
		String name = file.getName();
		return name.substring(name.lastIndexOf('.'));
	}

	/**
	 * Used to download a file from a URL. The URL can point to a web
	 * resource.
	 * @param url The URL of the file to downloaded
	 * @param target The target file to store it in
	 * @return The downloaded file details
	 * @throws IOException Thrown if their is a problem downloading the file
	 */
	public static DownloadedFile downloadToFile(URL url,File target) throws IOException {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			conn = url.openConnection();
			String contentType = conn.getContentType();
			out = new BufferedOutputStream(new FileOutputStream(target));

			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
			return new DownloadedFile(target,contentType);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

	/**
	 * Used to download a file from a URL to a file
	 * @param url The url of the file to download
	 * @return The downloaded file
	 * @throws IOException Thrown if their is a problem downloading it
	 */
	public static DownloadedFile downloadToTempFile(URL url) throws IOException {
		File file = createTempFile("artwork", getExtension(url.getPath()));
		if (!file.delete()) {
			throw new IOException("Unable to delete temp file " + file.getAbsolutePath());
		}
		file.deleteOnExit();

		return downloadToFile(url,file);
	}

	/**
	 * Used to get the extension from the path. includeing the '.'
	 * @param path The path
	 * @return The extension
	 */
	public static String getExtension(String path) {
		return path.substring(path.lastIndexOf('.'));
	}

	/**
	 * Used to get a file in the next to a class
	 * @param filename The filename to get
	 * @param currentClass The class it is next to
	 * @return The file
	 * @throws IOException Thrown if their is a problem finding it
	 */
    public static File getFile(String filename, Class<?> currentClass) throws IOException {
        URL url = currentClass.getResource(filename);
        if (url == null) {
            throw new IOException("Unable to find file " + filename);
        }
        return getFile(filename,currentClass, url);
    }

    private static File getFile(String filename, Class<?> currentClass, URL url)
            throws  IOException, UnsupportedEncodingException {
        if (url.getProtocol().equals("jar")) { //$NON-NLS-1$
            File file = createTempFile(filename.replaceAll("/|\\\\","-"), ".o"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            file.deleteOnExit();
            InputStream stream = currentClass.getResourceAsStream(filename);
            if (stream==null) {
            	throw new IOException("Unable to find file " + filename);
            }
            copy(stream, file);
            return file;
        }

        String fullPath = URLDecoder.decode(url.getFile(), "UTF8"); //$NON-NLS-1$
        File file = new File(fullPath);
        return file;
    }

    /**
     * Used to copy a file
     * @param s The source file to copy
     * @param t The target file to copy
     * @throws IOException Thrown if their is a problem copying the file
     */
    public static void copy(File s, File t) throws IOException {
        InputStream in = new FileInputStream(s);
        copy(in, t);
    }

    /**
     * Used to copy a file from a input stream to a target file
     * @param in The input stream pointing to the source file
     * @param t The target file to copy
     * @throws IOException Thrown if their is a problem copying the file
     */
    public static void copy(InputStream in, File t) throws IOException {
    	byte[] buffer = new byte[200];
        FileOutputStream out = new FileOutputStream(t);

        while(true) {
            int count = in.read(buffer);
            if(count == -1) break;
            out.write(buffer,0,count);
        }

        out.close();
        in.close();
    }

    /**
	 * Used to display the contents of a file
	 *
	 * @param file The file to display
	 * @param os The output stream to display it to
	 * @throws IOException Thrown if their is a problem reading or displaying the file
	 */
	public static void displayFile(File file, PrintStream os) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;
		while ((str = in.readLine()) != null) {
			os.println(str);
		}
		in.close();
	}

	/**
	 * Used to read the contents of a file into a string
	 *
	 * @param file The file to read
	 * @return The contents of the file
	 * @throws IOException Thrown if their is a problem reading the file
	 */
	public static String readFileContents(File file) throws IOException {
		StringBuilder results = new StringBuilder();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;
		while ((str = in.readLine()) != null) {
			results.append(str + LS);
		}
		in.close();
		return results.toString();
	}
	
	/**
	 * Used to read the contents of a stream into a string
	 *
	 * @param inputStream The input stream
	 * @return The contents of the file
	 * @throws IOException Thrown if their is a problem reading the file
	 */
	public static String readFileContents(InputStream inputStream) throws IOException {
		if (inputStream==null) {
			throw new IOException("Input stream is null");
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder results = new StringBuilder();
			String str;
			while ((str = in.readLine()) != null) {
				results.append(str + LS);
			}
			return results.toString();
		}
		finally {
			if (in!=null) {
				in.close();
			}
		}
	}

	/**
	 * Used to display the contents of a file
	 * @param file The file to display
	 * @param startLine The line to start displaying from
	 * @param endLine The line to finish displaying from
	 * @param os The output stream used to print the file to
	 * @throws IOException Thrown if their is a problem reading the file
	 */
	public static void displayFile(File file,int startLine, int endLine, OutputStream os)throws IOException {
		PrintStream ps = new PrintStream(os);
		if (startLine<0) {
			startLine = 0;
		}
		int line = 1;
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str;
		while ((str = in.readLine()) != null) {
			if (line>=startLine && line <=endLine) {
				ps.println(line + ": " + str); //$NON-NLS-1$
			}
			line++;
		}
		in.close();
	}
	
	/**
	 * Used to get the current working directory
	 * @return the current working directory
	 */
	public static File getWorkingDirectory() {
		return new File( System.getProperty("user.dir")); //$NON-NLS-1$
	}

	/**
	 * Used to convert relative paths to absolute paths. This will remove .. from the path
	 * @param path The relative path
	 * @return The absolute path
	 */
	public static File resolveRelativePaths(File path) {
		String segments[] = path.getAbsolutePath().split(Pattern.quote(File.separator));
		List<String>newSegments = new ArrayList<String>();
		for (String seg : segments) {
			if (seg.equals("..")) { //$NON-NLS-1$
				newSegments.remove(newSegments.size()-1);
			}
			else {
				newSegments.add(seg);
			}
		}
		File result = null;
		for (String seg : newSegments) {
			if (result == null) {
				result = new File(seg);
			}
			else {
				result = new File(result,seg);
			}
		}
		return result;
	}
	
	/**
	 * Used a temporary file that will be deleted when the JVM exits
	 * @param name name of file
	 * @param ext extension of the file
	 * @return The file
	 * @throws IOException Thrown if their is a problem creating the file
	 */
	public static File createTempFile(String name,String ext) throws IOException {
		final File file = File.createTempFile(name, ext);
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				if (file.exists()) {
					try {
						FileHelper.delete(file);
					} catch (IOException e) {
						log.error(MessageFormat.format("Unable to delete temp file ''{0}''",file),e); //$NON-NLS-1$
					}
				}
			}
		});
		return file;
	}


	/**
	 * Used to create a temporary file with the give contents
	 * @param testConfig The contents to put in the file
	 * @return A reference to the file
	 * @throws IOException Thrown if their are any problems
	 */
	public static File createTmpFileWithContents(StringBuilder testConfig)  throws IOException {
		File configFile = createTempFile("config", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
		configFile.deleteOnExit();
		FileHelper.appendContentsToFile(configFile, testConfig);
		return configFile;
	}
	
	/**
	 * Used to add contents to a file
	 * @param file The file to add contetns to
	 * @param contents The contents
	 * @throws IOException Thrown if their is a IO problem
	 */
	public static void appendContentsToFile(File file,StringBuilder contents) throws IOException {
		PrintStream ps = null;
		try {
			FileOutputStream os = new FileOutputStream(file);
			ps = new PrintStream(os);
			ps.print(contents.toString());
		}
		finally {
			ps.close();
		}
	}

	/**
	 * Used to delete a file or a directory tree. If a directory is given, then all it's contents are also deleted recusrsivly.
	 * @param file The file or directory to delete
	 * @throws IOException Thrown if their are any problems
	 */
	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			FileHelper.deleteDir(file);
		}
		else {
			if (!file.delete() && file.exists()) {
				throw new IOException(MessageFormat.format("Unable to delete file ''{0}''",file)); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Used to delete a directory and all it's children
	 *
	 * @param dir The directory to delete
	 * @return True if successful, otherwise false
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

}
