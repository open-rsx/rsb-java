package rsb.transport;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBObject;
import rsb.filter.FilterObserver;

/**
 * @author swrede
 */
public interface Port extends RSBObject, FilterObserver {

	public void push(Event e);

	public String getType();

	/**
	 * Sets the quality of service requirements on sending and receiving event
	 * notifications.
	 * 
	 * @param spec
	 *            new spec to apply
	 */
	public void setQualityOfServiceSpec(QualityOfServiceSpec spec);

}
