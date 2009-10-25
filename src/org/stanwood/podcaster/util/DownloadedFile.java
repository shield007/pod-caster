package org.stanwood.podcaster.util;

import java.io.File;

/**
 * Used to store details on files downloaded from the web
 */
public class DownloadedFile {

	private File file;
	private String contentType;	

	/**
	 * Used to create a instance of the class
	 * @param file The file downloaded from the web
	 * @param contentType The content type of the file
	 */
	public DownloadedFile(File file, String contentType) {
		super();
		this.file = file;
		this.contentType = contentType;
	}
	
	/**
	 * Used to get the file that was downloaded
	 * @return The file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Used to set the file that was downloaded
	 * @param file The file
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * Used to get the content type of the downloaded file
	 * @return The content type
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Used to set the content type of the downloaded file
	 * @param contentType The content type of the downloaded file
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
}
