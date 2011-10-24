package org.stanwood.podcaster.util;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Used to test the class {@link FileHelper}
 */
public class TestFileHelper {

	/**
	 * Used to test that the name part of a file name can be parsed correctly
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testExtension() throws Exception {
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();

		Assert.assertEquals("Check extension", ".txt",FileHelper.getExtension(file));
	}

	/**
	 * Used to test that the extension part of a file name can be parsed correctly
	 * @throws Exception Thrown if their are any problems
	 */
	@Test
	public void testName() throws Exception {
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();

		Assert.assertTrue(FileHelper.getName(file).contains("test"));
		Assert.assertFalse(FileHelper.getName(file).endsWith("."));
	}
}
