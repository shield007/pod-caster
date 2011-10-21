package org.stanwood.podcaster.config;

import java.io.File;
import java.net.URL;

import org.stanwood.podcaster.audio.Format;

/**
 * This is a base class for podcast's that holds generic podcast information
 */
public abstract class AbstractPodcast {

	private String id;
	private String feedTitle;
	private String feedCopyright;
	private String feedArtist;
	private String feedDescription;
	private String entryDescription;
	private File rssFile;
	private URL rssUrl;
	private Format format;
	private URL feedImageURL;
	private int maxEntries;

	/**
	 * The constructor
	 * @param id The id of the podcast entry in the configurtion
	 */
	public AbstractPodcast(String id) {
		this.id = id;
	}

	/**
	 * Used to get the podcast id
	 * @return the podcast id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Used to get the podcast title
	 * @return the podcast title
	 */
	public String getFeedTitle() {
		return feedTitle;
	}

	/**
	 * Used to set the podcast title
	 * @param feedTitle the podcast title
	 */
	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	/**
	 * Used to get the podcast copyright description
	 * @return the podcast copyright description
	 */
	public String getFeedCopyright() {
		return feedCopyright;
	}

	/**
	 * Used to set the podcast copyright description
	 * @param feedCopyright the podcast copyright description
	 */
	public void setFeedCopyright(String feedCopyright) {
		this.feedCopyright = feedCopyright;
	}

	/**
	 * Used to get the podcast artist
	 * @return the podcast artist
	 */
	public String getFeedArtist() {
		return feedArtist;
	}

	/**
	 * Used to set the podcast artist
	 * @param feedArtist the podcast artist
	 */
	public void setFeedArtist(String feedArtist) {
		this.feedArtist = feedArtist;
	}

	/**
	 * Used to get the description of the podcast
	 * @return the description of the podcast
	 */
	public String getFeedDescription() {
		return feedDescription;
	}

	/**
	 * Used to set the description of the podcast
	 * @param feedDescription the description of the podcast
	 */
	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}

	/**
	 * Used to get the description of the a podcast entry
	 * @return the description of the a podcast entry
	 */
	public String getEntryDescription() {
		return entryDescription;
	}

	/**
	 * Used to set the description of the a podcast entry
	 * @param entryDescription the description of the a podcast entry
	 */
	public void setEntryDescription(String entryDescription) {
		this.entryDescription = entryDescription;
	}

	/**
	 * Used to set the podcast rss file
	 * @param file the podcast rss file
	 */
	public void setRSSFile(File file) {
		this.rssFile = file;

	}

	/**
	 * Used to set the podcast URL
	 * @param url the podcast URL
	 */
	public void setRSSURL(URL url) {
		this.rssUrl = url;
	}

	/**
	 * Used to get the podcast rss file
	 * @return the podcast rss file
	 */
	public File getRSSFile() {
		return this.rssFile;
	}

	/**
	 * Used to get the podcast URL
	 * @return the podcast URL
	 */
	public URL getRSSURL() {
		return this.rssUrl;
	}

	/**
	 * Used to get the format of all the entries in the podcast
	 * @return the format of all the entries in the podcast
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Used to set the format of all the entries in the podcast
	 * @param format the format of all the entries in the podcast
	 */
	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * Used to set the image for the podcast
	 * @param url the image for the podcast
	 */
	public void setFeedImageURL(URL url) {
		this.feedImageURL = url;
	}

	/**
	 * Used to get the image for the podcast
	 * @return the image for the podcast
	 */
	public URL getFeedImageURL() {
		return feedImageURL;
	}

	/**
	 * Used to get the maximum number of entries in the podcast feed
	 * @return the maximum number of entries in the podcast feed
	 */
	public int getMaxEntries() {
		return maxEntries;
	}

	/**
	 * Used to set the maximum number of entries in the podcast feed
	 * @param maxEntries the maximum number of entries in the podcast feed
	 */
	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}


}
