package org.stanwood.bbcpodcaster.rss;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.stanwood.podcaster.rss.RSSFeed;
import org.stanwood.podcaster.util.FileHelper;

import com.sun.syndication.io.FeedException;

public class TestRSSFeed {

	@Test
	public void testFeedCreation() throws Exception {
		File rssFile = File.createTempFile("temp", ".rss");
		rssFile.deleteOnExit();

		RSSFeed rss = new RSSFeed(rssFile);
		rss.createNewFeed();
		rss.setTitle("Test");
		rss.setDescription("This is a test description");
		rss.setLink(new URL("http://blah/blah"));
		rss.write();

		String actual = FileHelper.readFileContents(rssFile);

		StringBuilder expected = new StringBuilder();
		expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+FileHelper.LS);
		expected.append("<rss version=\"2.0\">"+FileHelper.LS);
		expected.append("  <channel>"+FileHelper.LS);
		expected.append("    <title>Test</title>"+FileHelper.LS);
		expected.append("    <link>http://blah/blah</link>"+FileHelper.LS);
		expected.append("    <description>This is a test description</description>"+FileHelper.LS);
		expected.append("  </channel>"+FileHelper.LS);
		expected.append("</rss>"+FileHelper.LS);
		expected.append(""+FileHelper.LS);

		Assert.assertEquals("Check that created feed", expected.toString(),actual);
	}

	@Test
	public void testWithMissingFields() throws Exception {
		File rssFile = File.createTempFile("temp", ".rss");
		rssFile.deleteOnExit();

		RSSFeed rss = new RSSFeed(rssFile);
		try {
			rss.write();
			Assert.fail("Did not detect missing fields");
		}
		catch (FeedException e) {
			Assert.assertEquals("parse() or createNewFeed() must be called before writing the feed", e.getMessage());
		}

		rss.createNewFeed();
		try {
			rss.write();
			Assert.fail("Did not detect missing fields");
		}
		catch (FeedException e) {
			Assert.assertEquals("Unable to write feed, feed has no title", e.getMessage());
		}

		rss.setTitle("Test");
		try {
			rss.write();
			Assert.fail("Did not detect missing fields");
		}
		catch (FeedException e) {
			Assert.assertEquals("Unable to write feed, feed has no description", e.getMessage());
		}

		rss.setDescription("This is a test description");
		try {
			rss.write();
			Assert.fail("Did not detect missing fields");
		}
		catch (FeedException e) {
			Assert.assertEquals("Unable to write feed, feed has no link", e.getMessage());
		}

		rss.setLink(new URL("http://blah/blah"));
		rss.write();
	}
}
