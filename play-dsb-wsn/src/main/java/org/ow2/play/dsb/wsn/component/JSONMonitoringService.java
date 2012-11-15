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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;

import org.petalslink.dsb.jbi.se.wsn.api.MonitoringService;
import org.petalslink.dsb.jbi.se.wsn.api.Topic;
import org.petalslink.dsb.jbi.se.wsn.api.WSNException;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

/**
 * Push data to a JSON service
 * 
 * @author chamerling
 * 
 */
public class JSONMonitoringService implements MonitoringService {

	private static Logger logger = Logger.getLogger(JSONMonitoringService.class
			.getName());

	// where to push data
	private String endpoint;

	private AsyncHttpClient client;

	private Gson gson;

	public JSONMonitoringService(String endpoint) {
		this.endpoint = endpoint;
		this.gson = new Gson();
	}

	@Override
	public void newInNotifyError(final String uuid, Document payload,
			String to, Topic topic, long timestamp, Exception e)
			throws WSNException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Error while delivering notification to " + to);
		}

		MonitoringNotificationInfo info = new MonitoringNotificationInfo();
		info.uuid = uuid;
		info.error = e.getMessage();
		info.timestamp = timestamp;
		info.topic = topic;
		info.type = "newInNotifyError";
		post(info);
	}

	@Override
	public void newInNotifyInput(String uuid, Document payload, Topic topic,
			long timestamp) throws WSNException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("New newInNotifyInput on topic " + topic);
		}

		MonitoringNotificationInfo info = new MonitoringNotificationInfo();
		info.uuid = uuid;
		info.timestamp = timestamp;
		info.topic = topic;
		info.type = "newInNotifyInput";
		post(info);

	}

	@Override
	public void newInNotifyOutput(String uuid, Document payload, Topic topic,
			long timestamp) throws WSNException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("New newInNotifyOutput on topic " + topic);
		}

		MonitoringNotificationInfo info = new MonitoringNotificationInfo();
		info.uuid = uuid;
		info.timestamp = timestamp;
		info.topic = topic;
		info.type = "newInNotifyOutput";
		post(info);
	}

	@Override
	public void newOutNotify(String uuid, Document payload, String to,
			Topic topic, long timestamp) throws WSNException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("New newOutNotify on topic " + topic + " sending to "
					+ to);
		}

		MonitoringNotificationInfo info = new MonitoringNotificationInfo();
		info.uuid = uuid;
		info.timestamp = timestamp;
		info.topic = topic;
		info.type = "newOutNotify";
		info.to = to;
		post(info);
	}

	@Override
	public void newOutNotifyError(String uuid, Document paylaod, String to,
			Topic topic, long timestamp, Exception e) throws WSNException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("New newOutNotifyError on topic " + topic
					+ " while sending to " + to);
		}

		MonitoringNotificationInfo info = new MonitoringNotificationInfo();
		info.uuid = uuid;
		info.timestamp = timestamp;
		info.topic = topic;
		info.type = "newOutNotifyError";
		info.to = to;
		info.error = e.getMessage();
		post(info);
	}
	
	@Override
	public void newSubscribeRequest(String uuid, String subscriber, Topic topic) throws WSNException {
		MonitoringSubscriptionInfo info = new MonitoringSubscriptionInfo();
		info.uuid = uuid;
		info.subscriber = subscriber;
		info.topic = topic;
		info.type = "newSubscribeRequest";
		info.timestamp = System.currentTimeMillis();
		post(info);
	}

	@Override
	public void newSubscribeResponse(String uuid, String subscriptionID) throws WSNException {
		MonitoringSubscriptionInfo info = new MonitoringSubscriptionInfo();
		info.uuid = uuid;
		info.subscriptionID = subscriptionID;
		info.type = "newSubscribeResponse";
		info.timestamp = System.currentTimeMillis();
		post(info);
	}

	@Override
	public void newUnsubscribeRequest(String uuid, String subscriptionID) throws WSNException {
		MonitoringSubscriptionInfo info = new MonitoringSubscriptionInfo();
		info.uuid = uuid;
		info.subscriptionID = subscriptionID;
		info.type = "newUnsubscribeRequest";
		info.timestamp = System.currentTimeMillis();
		post(info);		
	}

	@Override
	public void newUnsubscribeResponse(String uuid) throws WSNException {
		MonitoringSubscriptionInfo info = new MonitoringSubscriptionInfo();
		info.uuid = uuid;
		info.type = "newUnsubscribeResponse";
		info.timestamp = System.currentTimeMillis();
		post(info);		
	}

	protected void post(final MonitoringNotificationInfo info) {
		try {
			getAsyncHttpClient().preparePost(endpoint)
					.addHeader("Content-Type", "application/json")
					.setBody(gson.toJson(info))
					.execute(new AsyncCompletionHandler<Response>() {

						@Override
						public Response onCompleted(Response response)
								throws Exception {
							logger.fine("Request is complete for UUID "
									+ info.uuid);
							return response;
						}

						@Override
						public void onThrowable(Throwable t) {
							logger.warning("HTTP failure for UUID '"
									+ info.uuid + "' : " + t.getMessage());
						}
					});
		} catch (Exception ex) {ex.printStackTrace();
			logger.warning("Error while sending monitoring information : "
					+ ex.getMessage());
		}
	}
	
	protected void post(final MonitoringSubscriptionInfo info) {
		try {
			getAsyncHttpClient().preparePost(endpoint)
					.addHeader("Content-Type", "application/json")
					.setBody(gson.toJson(info))
					.execute(new AsyncCompletionHandler<Response>() {

						@Override
						public Response onCompleted(Response response)
								throws Exception {
							logger.fine("Request is complete for UUID "
									+ info.uuid);
							return response;
						}

						@Override
						public void onThrowable(Throwable t) {
							logger.warning("HTTP failure for UUID '"
									+ info.uuid + "' : " + t.getMessage());
						}
					});
		} catch (Exception ex) {ex.printStackTrace();
			logger.warning("Error while sending monitoring information : "
					+ ex.getMessage());
		}
	}

	private synchronized AsyncHttpClient getAsyncHttpClient() {
		if (client == null) {
			client = new AsyncHttpClient(new AsyncHttpClientConfig.Builder()
					.setRequestTimeoutInMs(5000).build());
		}
		return client;
	}
}
