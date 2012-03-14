package org.stanwood.podcaster;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.stanwood.podcaster.util.WebFile;

/**
 * Used to download audio streams from URL's
 */
public class URLFetcher {

	private boolean playlist;
	private String mediaURL = null;

	/**
	 * Used to create url fetcher object that will fetch the media url using
	 * the given radio id. For example
	 * <pre>http://www.bbc.co.uk/radio1/realaudio/media/r1live.ram</pre>
	 * or <pre>http://www.bbc.co.uk/radio/listen/live/r1.asx</pre>
	 *
	 * @param url The URL of the webpage containing the Media URL
	 * @throws IOException
	 */
	public URLFetcher(URL url) throws IOException {
		WebFile page = new WebFile(url);
		String MIME = page.getMIMEType();
		if (page.getResponseCode()==404) {
			throw new IOException(MessageFormat.format(Messages.getString("URLFetcher.UNABLE_FIND_RESOURCE"),url.toExternalForm())); //$NON-NLS-1$
		}
		byte[] content = (byte[]) page.getContent();
		String strContent = new String(content);
		if (MIME.equals("audio/x-pn-realaudio")) { //$NON-NLS-1$
			mediaURL = strContent;
			playlist = false;
		} else if (MIME.equals("video/x-ms-asf")) { //$NON-NLS-1$
			mediaURL = url.toExternalForm();
			playlist = true;
		} else {
			throw new IOException(MessageFormat.format(Messages.getString("URLFetcher.UNSUPPORTED_MIME_TYPE"), url.toExternalForm(),MIME)); //$NON-NLS-1$
		}
	}

	/**
	 * Get media URL details
	 * @return The URL details
	 */
	public StreamReference getMediaUrl() {
		return new StreamReference(mediaURL, playlist);
	}
}
