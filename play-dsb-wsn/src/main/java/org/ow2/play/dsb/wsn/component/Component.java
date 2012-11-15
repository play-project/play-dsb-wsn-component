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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.jbi.JBIException;
import javax.xml.namespace.QName;

import org.ow2.play.governance.api.EventGovernance;
import org.ow2.play.governance.api.TopicAware;
import org.ow2.play.governance.api.bean.Topic;
import org.ow2.play.service.registry.api.Constants;
import org.ow2.play.service.registry.api.Registry;
import org.ow2.play.service.registry.api.RegistryException;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.jbi.se.wsn.NotificationEngine;
import org.petalslink.dsb.jbi.se.wsn.TopicSetHelper;
import org.petalslink.dsb.jbi.se.wsn.api.MonitoringService;
import org.petalslink.dsb.notification.commons.SOAUtil;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * This component gets topics from the governance service and launch all the
 * required services
 * 
 * @author chamerling
 * 
 */
public class Component extends org.petalslink.dsb.jbi.se.wsn.Component {

	@Override
	protected void initializeNotificationEngine() throws JBIException {

		// get the configuration files...
		Properties props = new Properties();
		try {
			props.load(Component.class.getClassLoader().getResourceAsStream(
					"notification.cfg"));
		} catch (IOException e) {
		}

		// get the initial toic set from the governance component
		Document topicsDOM = getTopicSet();
		if (topicsDOM == null) {
			getLogger().warning(
					"Can not get a list of topics to use in the component!");

			// create an empty topicset
			topicsDOM = TopicSetHelper
					.getWSNDocument(new ArrayList<org.petalslink.dsb.jbi.se.wsn.api.Topic>());
			// TODO : Expose the component stuff for later initialization!
			// TODO : Send an alert to someone...
			// throw new
			// JBIException("Can not get the topics from the governance");
		}

		Document tnsDOM = getTNS();

		String endpointName = props.getProperty(ENDPOINT_NAME, "Endpoint");
		QName interfaceName = QName
				.valueOf(props
						.getProperty(INTERFACE_NAME,
								"{http://dsb.petalslink.com/notification}NotificationInterface"));
		QName serviceName = QName.valueOf(props.getProperty(SERVICE_NAME,
				"{http://dsb.petalslink.com/notification}NotificationService"));

		if (engine == null) {
			engine = new NotificationEngine(getLogger(), serviceName,
					interfaceName, endpointName, getClient(),
					getMonitoringService());
		}
		this.engine.init(topicsDOM, tnsDOM);
	}

	/**
	 * 
	 * @return
	 * @throws JBIException
	 */
	protected Document getTNS() throws JBIException {
		URL tns = Component.class.getClassLoader().getResource(TOPICS_NS_FILE);
		if (tns == null) {
			throw new JBIException(
					"Can not find the notification topicnamespace configuration file");
		}

		try {
			return SOAUtil.getInstance().getDocumentBuilderFactory()
					.newDocumentBuilder().parse(tns.openStream());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JBIException(e);
		}
	}

	/**
	 * Get the topic set from the governance engine
	 * 
	 * @return
	 * @throws JBIException
	 */
	public Document getTopicSet() {
		Document result = null;

		String reg = getRegistryEndpoint();
		getLogger().info(
				"Initializing topics from the governance engine running at "
						+ reg);

		try {

			List<Topic> topics = getEventGovernance(reg).getTopics();

			if (getLogger().isLoggable(Level.INFO)) {
				getLogger().info("Available topics : ");
				for (Topic topic : topics) {
					getLogger().info(topic.toString());
				}
			}

			result = TopicSetHelper
					.getWSNDocument(Lists
							.transform(
									topics,
									new Function<Topic, org.petalslink.dsb.jbi.se.wsn.api.Topic>() {
										@Override
										public org.petalslink.dsb.jbi.se.wsn.api.Topic apply(
												Topic t) {
											getLogger().fine(
													"Transforming topic " + t);
											org.petalslink.dsb.jbi.se.wsn.api.Topic result = new org.petalslink.dsb.jbi.se.wsn.api.Topic();
											result.name = t.getName();
											result.ns = t.getNs();
											result.prefix = t.getPrefix();
											return result;
										}
									}));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void doAddServices() {
		getLogger().info("Adding PLAY Web services");
		TopicAware service = new TopicAwareService(this.managementService,
				getLogger());
		this.ws.add(getService(TopicAware.class, service, "PlayTopic"));
	}

	protected EventGovernance getEventGovernance(String reg)
			throws RegistryException {
		Registry registry = CXFHelper
				.getClientFromFinalURL(reg, Registry.class);
		String govEndpoint = registry.get(Constants.GOVERNANCE);
		return CXFHelper.getClientFromFinalURL(govEndpoint,
				EventGovernance.class);
	}

	public synchronized MonitoringService getMonitoringService() {
		if (monitoringService == null) {
			try {
				monitoringService = new JSONMonitoringService(
						getMonitoringEndpoint());
			} catch (RegistryException e) {
				e.printStackTrace();
			}
		}
		return monitoringService;
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws RegistryException
	 */
	private String getMonitoringEndpoint() throws RegistryException {
		String reg = getRegistryEndpoint();
		String result = null;
		Registry registry = CXFHelper
				.getClientFromFinalURL(reg, Registry.class);
		String monitoring = registry.get(Constants.MONITORING_DSB_WSN);
		if (monitoring == null) {
			result = "http://localhost:3000/api/v1/monitoring/wsn/";
		} else {
			result = monitoring;
		}
		return result;
	}

	protected String getRegistryEndpoint() {
		Properties play = new Properties();
		try {
			play.load(Component.class.getClassLoader().getResourceAsStream(
					"play.cfg"));
		} catch (Exception e) {
			getLogger()
					.warning(
							"Can not find the PLAY configuration file in the DSB, please add play.cfg in the container. Running in dowgraded mode...");
		}
		return play.getProperty("play.registry",
				"http://localhost:8080/registry/RegistryService");

	}
}
