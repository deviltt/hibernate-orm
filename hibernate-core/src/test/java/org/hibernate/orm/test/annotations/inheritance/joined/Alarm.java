/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

//$Id$
package org.hibernate.orm.test.annotations.inheritance.joined;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("AlarmT")
public class Alarm extends EventInformation {

	protected EventInformation eventInfo;

	@OneToOne
	@JoinColumn(name = "EVENTINFO_NOTIFICATIONID",
			referencedColumnName = "NOTIFICATIONID")
	public EventInformation getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(EventInformation value) {
		this.eventInfo = value;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String eventId = ( getEventInfo() != null ?
				getEventInfo().getNotificationId() : null );
		sb.append(
				"AlarmT: id = " + getNotificationId() + "\t" +
						"has event id = " + eventId
		);
		return sb.toString();
	}

}
