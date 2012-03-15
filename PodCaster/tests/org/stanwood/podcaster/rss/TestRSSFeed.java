package org.stanwood.podcaster.rss;


import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.stanwood.podcaster.audio.MP3File;
import org.stanwood.podcaster.audio.TestAudioConversion;
import org.stanwood.podcaster.util.FileHelper;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.DateParser;

/**
 * Used to test the class {@link RSSFeed}
 */
@SuppressWarnings("nls")
public class TestRSSFeed {

	/**
	 * Used to test the creation of a new rss feed
	 * @throws Exception Thrown if their are any problems
	 */
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

	/**
	 * Used to test a RSS feed can be read and parsed
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testReadExsitingFeed() throws Exception {
		File rssFile = File.createTempFile("temp", ".xml");
		rssFile.deleteOnExit();
		FileHelper.copy(TestRSSFeed.class.getResourceAsStream("testFeed.xml"), rssFile);

		RSSFeed rss = new RSSFeed(rssFile);
		rss.parse();
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

	/**
	 * Used to test the adding of a entry to the rss feed
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testAddEntry() throws Exception {
//		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
		DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		File rssFile = File.createTempFile("temp", ".xml");
		rssFile.deleteOnExit();
		FileHelper.copy(TestRSSFeed.class.getResourceAsStream("testFeed.xml"), rssFile);

		File mp3File = File.createTempFile("test", ".mp3");
		mp3File.deleteOnExit();
		FileHelper.copy(TestAudioConversion.class.getResourceAsStream("test.mp3"),mp3File);

		MP3File mp3 = new MP3File(mp3File);

		RSSFeed rss = new RSSFeed(rssFile);
		rss.parse();

		DateTime date = new DateTime("2009-04-04T21:00:00+00:00");

		rss.addEntry("Funky", new URL("http://www.funky.com/test.mp3"), date, "Funky aduio for all your funky needs", "DJ Funk", mp3);

		rss.write();

		String actual = FileHelper.readFileContents(rssFile);

		StringBuilder expected = new StringBuilder();
		expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+FileHelper.LS);
		expected.append("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">"+FileHelper.LS);
		expected.append("  <channel>"+FileHelper.LS);
		expected.append("    <title>Test</title>"+FileHelper.LS);
		expected.append("    <link>http://blah/blah</link>"+FileHelper.LS);
		expected.append("    <description>This is a test description</description>"+FileHelper.LS);
		expected.append("    <item>"+FileHelper.LS);
		expected.append("      <title>Funky</title>"+FileHelper.LS);
		expected.append("      <link>http://www.funky.com/test.mp3</link>"+FileHelper.LS);
		expected.append("      <description>Funky aduio for all your funky needs</description>"+FileHelper.LS);
		expected.append("      <enclosure url=\"http://www.funky.com/test.mp3\" length=\"94020\" type=\"audio/mpeg\" />"+FileHelper.LS);
		expected.append("      <pubDate>"+DateParser.formatRFC822(date.toDate())+"</pubDate>"+FileHelper.LS);
		expected.append("      <guid>http://www.funky.com/test.mp3</guid>"+FileHelper.LS);
		expected.append("      <dc:creator>DJ Funk</dc:creator>"+FileHelper.LS);
		expected.append("      <dc:date>"+date.toMutableDateTime(DateTimeZone.UTC).toString(fmt2)+"</dc:date>"+FileHelper.LS);
		expected.append("    </item>"+FileHelper.LS);
		expected.append("  </channel>"+FileHelper.LS);
		expected.append("</rss>"+FileHelper.LS);
		expected.append(""+FileHelper.LS);

		Assert.assertEquals("Check that created feed", expected.toString(),actual);
	}

	/**
	 * Used to test the writing of a RSS feed with missing fields
	 * @throws Exception Thrown if their are any problems
	 */
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
