package rsb;

import java.util.UUID;

/**
 * 
 */

/**
 * @author swrede
 *
 */
public class RSBEvent {
	
	UUID uuid;
	String type;
	String uri;
	Object data;
	
	public boolean hasID() {
		return (uuid!=null) ? true : false;
	}
	
	public UUID generateID() {
		uuid = UUID.randomUUID();
		return uuid;
	}
	
	public UUID ensureID() {
		if (!hasID()) {
			generateID();
		}
		return uuid;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}


}
