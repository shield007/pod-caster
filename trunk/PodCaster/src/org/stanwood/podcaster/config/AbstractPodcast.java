package org.stanwood.podcaster.config;

import java.io.File;
import java.net.URL;

import org.stanwood.podcaster.audio.Format;

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
	
	public AbstractPodcast(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public String getFeedTitle() {
		return feedTitle;
	}

	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	public String getFeedCopyright() {
		return feedCopyright;
	}

	public void setFeedCopyright(String feedCopyright) {
		this.feedCopyright = feedCopyright;
	}

	public String getFeedArtist() {
		return feedArtist;
	}

	public void setFeedArtist(String feedArtist) {
		this.feedArtist = feedArtist;
	}

	public String getFeedDescription() {
		return feedDescription;
	}

	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}

	public String getEntryDescription() {
		return entryDescription;
	}

	public void setEntryDescription(String entryDescription) {
		this.entryDescription = entryDescription;
	}

	public void setRSSFile(File file) {
		this.rssFile = file;
		
	}

	public void setRSSURL(URL url) {
		this.rssUrl = url;
	}	
	
	public File getRSSFile() {
		return this.rssFile;
	}

	public URL getRSSURL() {
		return this.rssUrl;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public void setFeedImageURL(URL url) {
		this.feedImageURL = url;
	}
	
	public URL getFeedImageURL() {
		return feedImageURL;
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}
	
	
}
