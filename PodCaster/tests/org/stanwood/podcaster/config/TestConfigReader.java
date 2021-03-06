package org.stanwood.podcaster.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.stanwood.podcaster.audio.Format;
import org.stanwood.podcaster.logging.LogSetupHelper;
import org.stanwood.podcaster.util.FileHelper;

/**
 * Used to test the reading of the application configuration
 */
@SuppressWarnings("nls")
public class TestConfigReader {

	/**
	 * Used to test that the configuration directory and other globals is read correctly
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testGlobalSettings() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("  <global>"+FileHelper.LS);
		testConfig.append("    <configDirectory>/blah/blah1</configDirectory>"+FileHelper.LS);
		testConfig.append("    <mplayer-path>/blah/bin/mplayer</mplayer-path>"+FileHelper.LS);
		testConfig.append("    <ffmpeg-path>/blah/bin/ffmpeg</ffmpeg-path>"+FileHelper.LS);
		testConfig.append("    <get-iplayer-path>/blah/bin/get-iplayer</get-iplayer-path>"+FileHelper.LS);
		testConfig.append("  </global>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Assert.assertEquals(new File(File.separator+"blah"+File.separator+"blah1").getAbsolutePath(),configReader.getConfigDir().getAbsolutePath());
		Assert.assertEquals("/blah/bin/mplayer",configReader.getMPlayerPath());
		Assert.assertEquals("/blah/bin/ffmpeg",configReader.getFFMpegPath());
		Assert.assertEquals("/blah/bin/get-iplayer",configReader.getGetIPlayerPath());
	}

	/**
	 * Used to test that the globals can be in a different order
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testGlobalSettingsDifferentOrder() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("  <global>"+FileHelper.LS);
		testConfig.append("    <mplayer-path>/blah/bin/mplayer</mplayer-path>"+FileHelper.LS);
		testConfig.append("    <configDirectory>/blah/blah1</configDirectory>"+FileHelper.LS);
		testConfig.append("    <get-iplayer-path>/blah/bin/get-iplayer</get-iplayer-path>"+FileHelper.LS);
		testConfig.append("    <ffmpeg-path>/blah/bin/ffmpeg</ffmpeg-path>"+FileHelper.LS);
		testConfig.append("  </global>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Assert.assertEquals(new File(File.separator+"blah"+File.separator+"blah1").getAbsolutePath(),configReader.getConfigDir().getAbsolutePath());
		Assert.assertEquals("/blah/bin/mplayer",configReader.getMPlayerPath());
		Assert.assertEquals("/blah/bin/ffmpeg",configReader.getFFMpegPath());
		Assert.assertEquals("/blah/bin/get-iplayer",configReader.getGetIPlayerPath());
	}

	/**
	 * Used to test podcast entries
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testPodCasts() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("  <podcast id=\"radio\" rssFile=\"/srv/www/htdocs/podcasts/radio.rss\" rssUrl=\"http://blah.com/podcasts/radio.rss\" maxEntries=\"10\">"+FileHelper.LS);
		testConfig.append("    <metadata>"+FileHelper.LS);
		testConfig.append("      <feed>"+FileHelper.LS);
		testConfig.append("        <title>A test feed</title>"+FileHelper.LS);
		testConfig.append("        <image url=\"http://www.bbc.co.uk/iplayer/images/brand/b006wq52_512_288.jpg\"/>"+FileHelper.LS);
		testConfig.append("        <copyright>Copyright blah</copyright>"+FileHelper.LS);
		testConfig.append("        <artist>A Author</artist>"+FileHelper.LS);
		testConfig.append("        <description>This is a test radio steam feed</description>"+FileHelper.LS);
		testConfig.append("      </feed>"+FileHelper.LS);
		testConfig.append("      <entry>"+FileHelper.LS);
		testConfig.append("        <description>This is a test radio stream entry</description>"+FileHelper.LS);
		testConfig.append("      </entry>"+FileHelper.LS);
		testConfig.append("    </metadata>"+FileHelper.LS);
		testConfig.append("    <radioStream url=\"http://www.bbc.co.uk/radio1/wm_asx/aod/radio1_hi.asx\" captureTime=\"10000\"/>"+FileHelper.LS);
		testConfig.append("  </podcast>"+FileHelper.LS);
		testConfig.append("  <podcast id=\"iplayer\" format=\"flac\" rssFile=\"/srv/www/htdocs/podcasts/iplayer.rss\" rssUrl=\"http://blah.com/podcasts/iplayer.rss\">"+FileHelper.LS);
		testConfig.append("    <metadata>"+FileHelper.LS);
		testConfig.append("      <feed>"+FileHelper.LS);
		testConfig.append("      </feed>"+FileHelper.LS);
		testConfig.append("      <entry>"+FileHelper.LS);
		testConfig.append("      </entry>"+FileHelper.LS);
		testConfig.append("    </metadata>"+FileHelper.LS);
		testConfig.append("    <iplayer episode=\"Radio 1\" captureTime=\"10000\"/>"+FileHelper.LS);
		testConfig.append("  </podcast>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Collection<AbstractPodcast> podcasts = configReader.getPodcasts();
		Assert.assertEquals(2,podcasts.size());

		StreamPodcast podcast2 = (StreamPodcast) configReader.getPodcast("radio");
		Assert.assertNotNull(podcast2);
		Assert.assertEquals("radio",podcast2.getId());
		Assert.assertEquals("This is a test radio stream entry",podcast2.getEntryDescription());
		Assert.assertEquals("A Author",podcast2.getFeedArtist());
		Assert.assertEquals("Copyright blah",podcast2.getFeedCopyright());
		Assert.assertEquals("This is a test radio steam feed",podcast2.getFeedDescription());
		Assert.assertEquals("A test feed",podcast2.getFeedTitle());
		Assert.assertEquals("http://www.bbc.co.uk/iplayer/images/brand/b006wq52_512_288.jpg",podcast2.getFeedImageURL().toExternalForm());
		Assert.assertEquals(new File(File.separator+"srv"+File.separator+"www"+File.separator+"htdocs"+File.separator+"podcasts"+File.separator+"radio.rss").getAbsolutePath(),podcast2.getRSSFile().getAbsolutePath());
		Assert.assertEquals("http://blah.com/podcasts/radio.rss",podcast2.getRSSURL().toExternalForm());
		Assert.assertEquals(Format.MP3,podcast2.getFormat());
		Assert.assertEquals("http://www.bbc.co.uk/radio1/wm_asx/aod/radio1_hi.asx",podcast2.getStreamURL().toExternalForm());
		Assert.assertEquals(10000,podcast2.getCaptureTime().getMillis());

		IPlayerPodcast podcast1 = (IPlayerPodcast) configReader.getPodcast("iplayer");
		Assert.assertNotNull(podcast1);
		Assert.assertEquals("iplayer",podcast1.getId());
		Assert.assertNull(podcast1.getEntryDescription());
		Assert.assertNull(podcast1.getFeedArtist());
		Assert.assertNull(podcast1.getFeedCopyright());
		Assert.assertNull(podcast1.getFeedDescription());
		Assert.assertNull(podcast1.getFeedTitle());
		Assert.assertNull(podcast1.getFeedImageURL());
		Assert.assertEquals(new File(File.separator+"srv"+File.separator+"www"+File.separator+"htdocs"+File.separator+"podcasts"+File.separator+"iplayer.rss").getAbsolutePath(),podcast1.getRSSFile().getAbsolutePath());
		Assert.assertEquals("http://blah.com/podcasts/iplayer.rss",podcast1.getRSSURL().toExternalForm());
		Assert.assertEquals(Format.FLAC,podcast1.getFormat());
		Assert.assertEquals("Radio 1",podcast1.getEpisodeId());
		Assert.assertEquals(10000,podcast1.getCaptureTime().getMillis());
	}

	/**
	 * Used to test the default value of globals
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testDefaultGlobals() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Assert.assertEquals(new File(FileHelper.HOME_DIR,".podcaster").getAbsolutePath(),configReader.getConfigDir().getAbsolutePath());
		Assert.assertEquals("mplayer",configReader.getMPlayerPath());
		Assert.assertEquals("ffmpeg",configReader.getFFMpegPath());
		Assert.assertEquals("get-iplayer",configReader.getGetIPlayerPath());
	}

	private ConfigReader createConfigReader(StringBuilder testConfig)
			throws IOException, FileNotFoundException, ConfigException {
				File configFile = FileHelper.createTmpFileWithContents(testConfig);
				ConfigReader configReader = null;
				InputStream is = null;
				try {
					is = new FileInputStream(configFile);
					configReader = new ConfigReader(is);
					configReader.parse();
				}
				finally {
					if (is!=null) {
						is.close();
					}
				}
				return configReader;
			}
}
