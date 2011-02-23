/**
 * 
 */
package rsb.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Document;
import nu.xom.Serializer;

/**
 * @author rgaertne
 *
 */
public class XcfSerializer extends Serializer {

	private String xmldecl = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
	
	/**
	 * @param arg0
	 */
	public XcfSerializer(OutputStream arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws UnsupportedEncodingException
	 */
	public XcfSerializer(OutputStream arg0, String arg1)
			throws UnsupportedEncodingException {
		super(arg0, arg1);
		xmldecl = "<?xml version=\"1.0\" encoding=\""+getEncoding()+"\" standalone=\"no\"?>";
	}


	// TODO is there need for this methods?
	/*public String getXmldecl() {
		return xmldecl;
	}

	public void setXmldecl(String xmldecl) {
		this.xmldecl = xmldecl;
	}
*/    /**
     * Writes XML declaration followed by a line break.
     */
	public void writeXMLDeclaration() {
		try {
			this.writeRaw(xmldecl);
			this.breakLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String createXML(Document doc) {
		String result = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XcfSerializer ser = new XcfSerializer(baos, "UTF-8");
			ser.setIndent(1);
			ser.write(doc);
			result = baos.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
