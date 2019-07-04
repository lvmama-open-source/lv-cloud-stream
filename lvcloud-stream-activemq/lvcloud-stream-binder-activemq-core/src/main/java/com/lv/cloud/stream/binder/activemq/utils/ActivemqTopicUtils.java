package com.lv.cloud.stream.binder.activemq.utils;

import java.io.UnsupportedEncodingException;

public final class ActivemqTopicUtils {

	private ActivemqTopicUtils() {

	}

	/**
	 * Allowed chars are ASCII alphanumerics, ':','.', '_' and '-'.
	 */
	public static void validateTopicName(String topicName) {
		try {
			byte[] utf8 = topicName.getBytes("UTF-8");
			for (byte b : utf8) {
				if (!((b >= 'a') && (b <= 'z') || (b >= 'A') && (b <= 'Z') || (b >= '0') && (b <= '9') || (b == '.')
						|| (b == '-') || (b == '_') || (b == ':'))) {
					throw new IllegalArgumentException(
							"Topic name can only have ASCII alphanumerics, ':','.', '_' and '-', but was: '" + topicName
									+ "'");
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new AssertionError(e); // Can't happen
		}
	}
}
