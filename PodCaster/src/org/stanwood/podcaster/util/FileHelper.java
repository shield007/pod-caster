package org.stanwood.podcaster.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class FileHelper {

	public final static String LS = System.getProperty("line.separator");

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
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
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
		File file = File.createTempFile("artwork", getExtension(url.getPath()));
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
            File file = File.createTempFile(filename.replaceAll("/|\\\\","-"), ".o"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
}