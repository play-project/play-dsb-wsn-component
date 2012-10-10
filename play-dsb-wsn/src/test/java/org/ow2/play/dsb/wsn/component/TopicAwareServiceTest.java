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
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.ow2.play.governance.api.TopicAware;
import org.ow2.play.governance.api.bean.Topic;
import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.jbi.se.wsn.api.NotificationService;
import org.petalslink.dsb.jbi.se.wsn.api.SubscriptionService;
import org.petalslink.dsb.notification.service.NotificationConsumerService;
import org.petalslink.dsb.soap.CXFExposer;
import org.petalslink.dsb.soap.api.Exposer;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 *
 */
public class TopicAwareServiceTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAddTopic() throws Exception {
		
		String address = "http://localhost:6573/petals/dsb/wsn/Listener";
		final AtomicLong counter = new AtomicLong(0);
				
		TopicAware client = CXFHelper.getClientFromFinalURL("http://localhost:8079/play-dsb-wsn-package/PlayTopic", TopicAware.class);
		Topic topic = new Topic();
		topic.setName("TestTopic");
		topic.setNs("http://petals.ow2.org/dsb/wsn");
		topic.setPrefix("pet");
		
		List<Topic> list = client.get();
		System.out.println("Topics : " + list);
		client.add(topic);

		List<Topic> after = client.get();
				
		System.out.println("Topics : ");
		System.out.println(after);
		
		assertTrue(after.contains(topic));
		
		startServer(address, counter);
		
		// subscribe to new topic
		SubscriptionService subsClient = CXFHelper.getClientFromFinalURL("http://localhost:8079/play-dsb-wsn-package/SubscriptionService", SubscriptionService.class);
		org.petalslink.dsb.jbi.se.wsn.api.Topic t = new org.petalslink.dsb.jbi.se.wsn.api.Topic();
		t.name = topic.getName();
		t.ns = topic.getNs();
		t.prefix = topic.getPrefix();
		String id = subsClient.subscribe(t, address);
		
		System.out.println("Subscription ID : " + id);
		
		// notify, we should receive...
		NotificationService notifclient = CXFHelper.getClientFromFinalURL("http://localhost:8079/play-dsb-wsn-package/NotificationService", NotificationService.class);
		notifclient.notify(t, "<hello>hi</hello>");
		
		Thread.sleep(5000L);
		
		// assert that we received some messages
		assertEquals(1, counter);
		
		// remove the topic
		
		// check that we do not receive anymore
		
		
	}
	
	   private static Service startServer(final String address, final AtomicLong counter) {
	        System.out.println("****** CREATING LOCAL SERVER ******");

	        // local address which will receive notifications
	        System.out
	                .println("Creating service which will receive notification messages from the DSB...");

	        Service server = null;
	        QName interfaceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
	                "NotificationConsumer");
	        QName serviceName = new QName("http://docs.oasis-open.org/wsn/bw-2",
	                "NotificationConsumerService");
	        QName endpointName = new QName("http://docs.oasis-open.org/wsn/bw-2",
	                "NotificationConsumerPort");
	        // expose the service
	        INotificationConsumer consumer = new INotificationConsumer() {
	            public void notify(Notify notify) throws WsnbException {
	                System.out
	                        .println(String
	                                .format("Got a notify on HTTP service #%s, this notification comes from the DSB itself...",
	                                        counter.incrementAndGet()));

	                Document dom = Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
	                System.out.println("==============================");
	                try {
	                    XMLHelper.writeDocument(dom, System.out);
	                } catch (TransformerException e) {
	                }
	                System.out.println("==============================");
	            }
	        };
	        NotificationConsumerService service = new NotificationConsumerService(interfaceName,
	                serviceName, endpointName, "NotificationConsumerService.wsdl", address, consumer);

	        Exposer exposer = new CXFExposer();
	        try {
	            server = exposer.expose(service);
	            server.start();
	            System.out.println("Local server is started and is ready to receive notifications");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return server;

	    }

}
