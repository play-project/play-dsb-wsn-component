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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.petalslink.dsb.jbi.se.wsn.api.Topic;
import org.petalslink.dsb.jbi.se.wsn.api.WSNException;

/**
 * @author chamerling
 * 
 */
public class JSONMonitoringServiceTest extends TestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testJSON() throws WSNException {

		final AtomicLong counter = new AtomicLong(0);
		final CountDownLatch latch = new CountDownLatch(100);

		JSONMonitoringService service = new JSONMonitoringService(
				"http://localhost:3000/api/v1/monitoring/wsn/");
		Topic t = new Topic();
		t.name = "TestTopic";
		t.ns = "http://play.ow2.org";
		t.prefix = "play";
		long start = System.currentTimeMillis();

		service.newInNotifyInput(UUID.randomUUID().toString(), null, t,
				System.currentTimeMillis());

		Random r = new Random();
		int j = 0;

		for (int i = 0; i < 10000; i++) {
			if (j++ == 10) {
				j = 0;
			}
			try {

				long a = System.currentTimeMillis();
				service.newInNotifyError(UUID.randomUUID().toString(), null,
						"toin", t, System.currentTimeMillis(), new Exception(
								"OHOOH"));

				try {
					Thread.sleep(r.nextInt(500));
				} catch (InterruptedException e) {
				}

				t.name = "TestTopic" + j;
				service.newOutNotifyError(UUID.randomUUID().toString(), null,
						"http://localhost:" + j, t, System.currentTimeMillis(),
						new Exception("AHAHA"));
				try {
					Thread.sleep(r.nextInt(500));
				} catch (InterruptedException e) {
				}
				service.newInNotifyInput(UUID.randomUUID().toString(), null, t,
						System.currentTimeMillis());
				service.newInNotifyOutput(UUID.randomUUID().toString(), null,
						t, System.currentTimeMillis());

				try {
					Thread.sleep(r.nextInt(500));
				} catch (InterruptedException e) {
				}
				System.out.println(i + " : Sent in "
						+ (System.currentTimeMillis() - a));

			} catch (WSNException e) {
			}
		}
		System.out.println("SENT in " + (System.currentTimeMillis() - start));

		try {
			latch.await(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}

		System.out.println("Done in " + (System.currentTimeMillis() - start));
	}

}
