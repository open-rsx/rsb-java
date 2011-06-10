/**
 * 
 */
package rsb.xml;

import java.io.IOException;

import java.lang.ref.WeakReference;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * @author rgaertne
 * 
 */
public class DocumentBuilder {

	private static ThreadLocal<WeakReference<Builder>> builderRef = new ThreadLocal<WeakReference<Builder>>();

	/**
	 * Creates a nu.xom.Document from given XML string.
	 * 
	 * @param xml
	 * @return XOM Document instance
	 */
	public static Document createDocument(String xml) throws ValidityException,
			ParsingException {
		WeakReference<Builder> wref = builderRef.get();
		Builder parser;
		if (wref == null || (parser = wref.get()) == null) {
			parser = new Builder();
			builderRef.set(new WeakReference<Builder>(parser));
		}

		Document doc = null;
		try {
			doc = parser.build(xml, "");
		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
		}
		return doc;
	}

}
