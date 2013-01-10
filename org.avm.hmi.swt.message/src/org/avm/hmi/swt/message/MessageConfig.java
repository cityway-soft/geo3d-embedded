package org.avm.hmi.swt.message;

import java.util.Properties;


public interface MessageConfig {

	public static final String MESSAGES = "messages";

	Properties getProperty(String key);
}
