package org.stanwood.podcaster.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class FileHelper {

	public static String getName(File file) {
		String name = file.getName();
		return name.substring(0,name.indexOf('.'));
	}

	public static String getExtension(File file) {
		String name = file.getName();
		return name.substring(name.indexOf('.'));
	}

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

	public static DownloadedFile downloadToTempFile(URL url) throws IOException {
		File file = File.createTempFile("artwork", getFileExtension(url.getPath()));
		if (!file.delete()) {
			throw new IOException("Unable to delete temp file " + file.getAbsolutePath());
		}
		file.deleteOnExit();

		return downloadToFile(url,file);
	}

	private static String getFileExtension(String contentType) throws IOException {
		if (contentType.equals("image/png")) {
			return ".png";
		}
		else if (contentType.equals("image/jpeg")) {
			return ".jpg";
		}
		else if (contentType.equals("image/gif")) {
			return ".gif";
		}
		throw new IOException("Unknown content type '"+contentType+"'");
	}

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

    public static void copy(File s, File t) throws IOException {
        InputStream in = new FileInputStream(s);
        copy(in, t);
    }

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

}
