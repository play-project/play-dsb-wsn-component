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

import org.petalslink.dsb.jbi.se.wsn.api.Topic;

/**
 * @author chamerling
 * 
 */
public class MonitoringInfo {

	/**
	 * Correlation UUID
	 */
	public String uuid;

	/**
	 * The message type
	 */
	public String type;

	/**
	 * 
	 */
	public Topic topic;

	/**
	 * server timestamp
	 */
	public long timestamp;

	/**
	 * not null if this is an error
	 */
	public String error;

	/**
	 * Target endpoint
	 */
	public String to;

	/**
	 * Source endpoint
	 */
	public String from;

}
