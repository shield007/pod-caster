package org.stanwood.bbcpodcaster.logging;

import java.net.URL;

import org.junit.Test;
import org.stanwood.podcaster.URLFetcher;

public class TestURLFetcher {

//	@Test
//	public void testRealAudio() throws Exception {
//		URLFetcher urlFetcher = new URLFetcher(new URL("http://www.bbc.co.uk/radio1/realaudio/media/r1live.ram"));
//
//
//		System.out.println("["+urlFetcher.getMediaUrl()+"]");
//	}

	@Test
	public void testWindowsMedia() throws Exception {
		URLFetcher urlFetcher = new URLFetcher(new URL("http://www.bbc.co.uk/radio/listen/live/r1.asx"));
		System.out.println("["+urlFetcher.getMediaUrl()+"]");
	}
}
