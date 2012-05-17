package org.stanwood.podcaster.capture;

import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Used to test the iPlayer capture class
 */
@SuppressWarnings("nls")
public class TestIPlayerCapture {

	/**
	 * Used to check durations are handled correctly
	 */
	@Test
	public void testDuration() {
		Assert.assertEquals("100 milliseconds",IPlayerCapture.formatDuration(new Duration(100L)));
		Assert.assertEquals("1 second",IPlayerCapture.formatDuration(new Duration(1000L)));
		Assert.assertEquals("277 hours, 50 minutes, 34 seconds and 564 milliseconds",IPlayerCapture.formatDuration(new Duration(1000234564L)));

		Assert.assertEquals(1000234564L,new Duration(1000234564L).getMillis());
	}
}
