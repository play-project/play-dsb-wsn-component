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
import java.util.logging.Logger;

import org.ow2.play.governance.api.TopicAware;
import org.ow2.play.governance.api.bean.Topic;
import org.petalslink.dsb.jbi.se.wsn.api.ManagementService;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author chamerling
 * 
 */
public class TopicAwareService implements TopicAware {

	private ManagementService management;
	private Logger logger;

	public TopicAwareService(ManagementService management, Logger logger) {
		this.management = management;
		this.logger = logger;
	}

	@Override
	public boolean add(Topic topic) {
		logger.info("Adding a topic : " + topic);
		if (topic == null) {
			return false;
		}

		management.add(to(topic));
		return true;
	}

	@Override
	public boolean delete(Topic topic) {
		logger.info("Deleting topic : " + topic);
		if (topic == null) {
			return false;
		}

		this.management.delete(to(topic));
		return true;
	}

	@Override
	public List<Topic> get() {
		logger.info("Get topics");
		return Lists.transform(management.getTopics(),
				new Function<org.petalslink.dsb.jbi.se.wsn.api.Topic, Topic>() {

					public Topic apply(org.petalslink.dsb.jbi.se.wsn.api.Topic t) {
						Topic result = new Topic();
						result.setName(t.name);
						result.setNs(t.ns);
						result.setPrefix(t.prefix);
						return result;
					}
				});
	}

	protected org.petalslink.dsb.jbi.se.wsn.api.Topic to(Topic topic) {
		org.petalslink.dsb.jbi.se.wsn.api.Topic result = new org.petalslink.dsb.jbi.se.wsn.api.Topic();
		result.name = topic.getName();
		result.ns = topic.getNs();
		result.prefix = topic.getPrefix();
		return result;
	}

}
