/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb;

/**
 * Specification of desired quality of service settings for sending and
 * receiving events. Specification given here are required "at least". This
 * means concrete port instances can implement "better" QoS specs without any
 * notification to the clients. Better is decided by the integer value of the
 * specification enums. Higher values mean better services.
 * 
 * @author jwienke
 */
public class QualityOfServiceSpec {

	/**
	 * Possible ordering requirements for events.
	 * 
	 * @author jwienke
	 */
	public enum Ordering {
		/**
		 * Events are received in no specific order.
		 */
		UNORDERED,
		/**
		 * Events are received in the same order as sent by a sender. This is
		 * only valid for the connection of one sender to one receiver. No
		 * assumptions are placed on other receivers or events of other senders.
		 */
		ORDERED;
	}

	/**
	 * Possible requirements on the reliability of events.
	 * 
	 * @author jwienke
	 */
	public enum Reliability {
		/**
		 * Events may be dropped.
		 */
		UNRELIABLE,
		/**
		 * Events will always be received, otherwise an error is generated.
		 */
		RELIABLE;
	}

	private Ordering ordering;
	private Reliability reliability;

	/**
	 * Default specs with {@link Reliability#RELIABLE} and
	 * {@link Ordering#UNORDERED}.
	 */
	public QualityOfServiceSpec() {
		ordering = Ordering.UNORDERED;
		reliability = Reliability.RELIABLE;
	}

	/**
	 * Constructor with specified settings.
	 * 
	 * @param ordering
	 *            desired ordering
	 * @param reliability
	 *            desired reliability
	 */
	public QualityOfServiceSpec(Ordering ordering, Reliability reliability) {
		this.ordering = ordering;
		this.reliability = reliability;
	}

	/**
	 * Returns the desired ordering setting.
	 * 
	 * @return requested ordering setting
	 */
	public Ordering getOrdering() {
		return ordering;
	}

	/**
	 * Returns the desired reliability setting.
	 * 
	 * @return requested reliability setting
	 */
	public Reliability getReliability() {
		return reliability;
	}

	@Override
	public int hashCode() {
		return 7 * reliability.hashCode() + 13 * ordering.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof QualityOfServiceSpec)) {
			return false;
		}
		QualityOfServiceSpec other = (QualityOfServiceSpec) obj;
		return reliability.equals(other.reliability)
				&& ordering.equals(other.ordering);
	}

	@Override
	public String toString() {
		return "QualityOfServiceSpec[ordering = " +ordering+ ", reliability = "+reliability+"]";
	}

}
