package org.stanwood.podcaster.util;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.stanwood.podcaster.util.FileHelper;

public class TestFileHelper {

	@Test
	public void testExtension() throws Exception {
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();

		Assert.assertEquals("Check extension", ".txt",FileHelper.getExtension(file));
	}

	@Test
	public void testName() throws Exception {
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();

		Assert.assertTrue(FileHelper.getName(file).contains("test"));
		Assert.assertFalse(FileHelper.getName(file).endsWith("."));
	}
}
