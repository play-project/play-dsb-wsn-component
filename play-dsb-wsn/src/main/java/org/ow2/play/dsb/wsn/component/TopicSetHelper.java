/**
 *
 * Copyright (c) 2012, PetalsLink
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package org.ow2.play.dsb.wsn.component;

import java.util.List;

import org.ow2.play.governance.api.bean.Topic;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class TopicSetHelper {

	private TopicSetHelper() {
	}

	/**
	 * Get a WSN DOM document from a list of topics
	 * 
	 * @param topics
	 * @return
	 */
	public static final Document getWSNDocument(List<Topic> topics) {
		if (topics == null) {
			return null;
		}

		// because using the WSN libs is awful, let's do it from string...

		StringBuffer sb = new StringBuffer();
		sb.append("<wstop:TopicSet xmlns:wstop=\"http://docs.oasis-open.org/wsn/t-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml#\">\n");

		for (Topic topic : topics) {
			if (topic != null) {
				sb.append("<");
				if (topic.getPrefix() != null) {
					sb.append(topic.getPrefix());
					sb.append(":");
				}
				sb.append(topic.getName());
				sb.append(" wstop:topic=\"true\"");

				if (topic.getNs() != null && topic.getPrefix() != null) {
					sb.append(" xmlns:");
					sb.append(topic.getPrefix());
					sb.append("=\"");
					sb.append(topic.getNs());
					sb.append("\" />");
				}
			}
		}

		sb.append("\n</wstop:TopicSet>");

		try {
			return com.ebmwebsourcing.easycommons.xml.XMLHelper
					.createDocumentFromString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
