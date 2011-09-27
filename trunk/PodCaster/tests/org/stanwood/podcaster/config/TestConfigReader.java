package org.stanwood.podcaster.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.stanwood.podcaster.logging.LogSetupHelper;
import org.stanwood.podcaster.util.FileHelper;

public class TestConfigReader {

	/**
	 * Used to test that the configuration directory is read correctly
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testGlobalSettings() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("  <global>"+FileHelper.LS);
		testConfig.append("    <configDirectory>/blah/blah1</configDirectory>"+FileHelper.LS);
		testConfig.append("    <mplayerPath>/blah/bin/mplayer</mplayerPath>"+FileHelper.LS);		
		testConfig.append("    <ffmpegPath>/blah/bin/ffmpeg</ffmpegPath>"+FileHelper.LS);
		testConfig.append("    <iplayerdlPath>/blah/bin/iplayer-dl</iplayerdlPath>"+FileHelper.LS);
		testConfig.append("  </global>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Assert.assertEquals("/blah/blah1",configReader.getConfigDir().getAbsolutePath());
		Assert.assertEquals("/blah/bin/mplayer",configReader.getMPlayerPath());
		Assert.assertEquals("/blah/bin/ffmpeg",configReader.getFFMpegPath());
		Assert.assertEquals("/blah/bin/iplayer-dl",configReader.getIPlayerDLPath());
	}
	
	@Test
	public void testGlobalSettingsDifferentOrder() throws Exception {
		LogSetupHelper.initLogingInternalConfigFile("info.log4j.properties");

		StringBuilder testConfig = new StringBuilder();
		testConfig.append("<podcaster>"+FileHelper.LS);
		testConfig.append("  <global>"+FileHelper.LS);
		testConfig.append("    <mplayerPath>/blah/bin/mplayer</mplayerPath>"+FileHelper.LS);
		testConfig.append("    <configDirectory>/blah/blah1</configDirectory>"+FileHelper.LS);		
		testConfig.append("    <iplayerdlPath>/blah/bin/iplayer-dl</iplayerdlPath>"+FileHelper.LS);
		testConfig.append("    <ffmpegPath>/blah/bin/ffmpeg</ffmpegPath>"+FileHelper.LS);		
		testConfig.append("  </global>"+FileHelper.LS);
		testConfig.append("</podcaster>"+FileHelper.LS);

		ConfigReader configReader = createConfigReader(testConfig);
		Assert.assertEquals("/blah/blah1",configReader.getConfigDir().getAbsolutePath());
		Assert.assertEquals("/blah/bin/mplayer",configReader.getMPlayerPath());
		Assert.assertEquals("/blah/bin/ffmpeg",configReader.getFFMpegPath());
		Assert.assertEquals("/blah/bin/iplayer-dl",configReader.getIPlayerDLPath());
	}
	
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
		Assert.assertEquals("iplayer-dl",configReader.getIPlayerDLPath());
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
