package org.stanwood.bbcpodcaster.audio;

import org.junit.Test;

public class TestAudioConversion {
	
	@Test
	public void testWav2Mp3() throws Exception{
//		LogSetupHelper.initLogingInternalConfigFile("debug.log4j.properties");
//		
//		File mp3File = File.createTempFile("converted", ".mp3");
//		if (!mp3File.delete() && mp3File.exists()) {
//			throw new IOException("Unable to delete file: " + mp3File);
//		}
//		mp3File.deleteOnExit();
//		
//		File wavFile = File.createTempFile("source", ".wav");
//		if (!wavFile.delete() && wavFile.exists()) {
//			throw new IOException("Unable to delete file: " + mp3File);
//		}		
//		FileHelper.copy(TestAudioConversion.class.getResourceAsStream("test.wav"),wavFile);
//		wavFile.deleteOnExit();
//		
//		AudioFileConverter.wav2Format(new WavFile(wavFile),Format.MP3, mp3File);
//		
//		Assert.assertTrue("Check file can be found",mp3File.exists());
//		Assert.assertFalse("Check file can be found",wavFile.exists());
//		Assert.assertTrue("Check file contains data",mp3File.length()>0);
	}
}
