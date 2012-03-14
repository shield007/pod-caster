package org.stanwood.podcaster.audio;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message bundle class
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.stanwood.podcaster.audio.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Used to get the message
	 * @param key The key
	 * @return The message
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
