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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ow2.play.governance.api.bean.Topic;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * @author chamerling
 * 
 */
public class TopicSetHelperTest extends TestCase {

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

	public void testGetTopicsetNull() throws Exception {
		assertNull(TopicSetHelper.getWSNDocument(null));
	}
	
	public void testGetTopicsetNotNull() throws Exception {
		List<Topic> topics = new ArrayList<Topic>();
		Topic t = new Topic();
		t.setName("Foo");
		t.setNs("http://bar");
		t.setPrefix("b");
		topics.add(t);
		assertNotNull(TopicSetHelper.getWSNDocument(topics));

		String xml = XMLHelper.createStringFromDOMDocument(TopicSetHelper.getWSNDocument(topics));
		System.out.println(xml);
		
		// ... this is a so powerful test...
		assertTrue(xml.contains("<b:Foo "));
		assertTrue(xml.contains("xmlns:b=\"http://bar\""));
		assertTrue(xml.contains("wstop:topic=\"true\""));
	}

}
