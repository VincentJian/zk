/* MessageLoader.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Feb 9, 2012 11:45:22 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.io.IOException;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.http.Wpds;
import org.zkoss.zk.ui.util.Clients;

/**
 * The interface for loading messages as String output.
 * @author simonpai
 * @since 5.0.11
 */
public interface MessageLoader {
	
	/**
	 * Load the message to output StringBuffer.
	 * <p>In the following two times, ZK system will scan the <code>MessageLoader</code> setting and 
	 * invoke this mehtod of corresponding underlying instance to load the system messages.
	 * <ol>
	 * <li>{@link Wpds#outLocaleJavaScript(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} (define in zul/lang/zk.wpd)</li>
	 * <li>{@link Clients#reloadMessages(java.util.Locale)}</li>
	 * </ol>
	 * For the firest time, execution does not have access to Desktop and Page instance. 
	 * Hence, programmer must notice that they cannot use many of Execution's method.
	 * </p>
	 * @param out StringBuffer to output
	 * @param exec current Execution
	 */
	public void load(StringBuffer out, Execution exec) throws IOException;
}
