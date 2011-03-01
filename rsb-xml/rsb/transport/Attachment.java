/**
 * 
 */
package rsb.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author swrede
 *
 */
public class Attachment {
	/**
	 * Wraps binary data which can then be attached to {@link XOPData}.
	 * @see XOPData.addAttachment(String, Attachment)
	 * @see XOPData.getAttachment(String)
	 * @see XOPData
	 */
	// TODO check whether we need an URI/UUID here?
	// TODO add missing conversion routines
	
	private byte[] value;
	@SuppressWarnings("unchecked")
	private Class c;
	
	public void setValue(byte[] b) {
		this.value = b;
		this.c = byte[].class;
	}
	/**
	 * Returns serialized object data of an Attachment
	 * @return serialized object of an Attachment
	 */
	public byte[] getValue() {
		return value;
	}
	
	/**
	 * Returns the type of the serialized object 
	 * @return serialized object type
	 */
	@SuppressWarnings("unchecked")
	public Class getContentType() {
		return c;
	}
	/**
	 * Serializes a float array into the contents of an Attachment
	 * @param fa
	 * @return Attachment object containing the float array
	 * @throws SerializeException
	 */
	public static Attachment fromFloatArray(float[] fa) throws SerializeException {
		// TODO implement meaningful conversion
		Attachment a = new Attachment();
		a.c = float[].class;
		try {
			a.value = serialize(fa);
		} catch (IOException e) {
			// TODO wrap exception message in meaningful log statement 
			e.printStackTrace();
			throw new SerializeException("Caught IOException serializing float array to Attachment");
		}
		return a;
	}
	/**
	 * Serializes a proxy string into the contents of an Attachment
	 * @param proxy
	 * @return Attachment containing a binary representation of the given proxy string
	 * @throws SerializeException
	 */
	public static Attachment fromString(String proxy) throws SerializeException {
		Attachment a = new Attachment();
		a.c = String.class;
		try {
			a.value = serialize(proxy);
		} catch (IOException e) {
			// TODO wrap exception message in meaningful log statement 
			e.printStackTrace();
			throw new SerializeException("Caught IOException serializing String to Attachment");
		}
		return a;
	}
	/**
	 * Transforms a serializable object into its binary representation
	 * @param s object implementing the serializable interface
	 * @return serialized object
	 * @throws IOException
	 */
	public static byte[] serialize(Serializable s) throws IOException {
		byte[] buffer;		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(s);
		buffer = baos.toByteArray();
		return buffer;		
	}	

	/**
	 * Deserializes the contents of this attachment into a float array.
	 * Will only work if this Attachment actually contains a serialized float
	 * array. If this is not the case, a SerializeException will be thrown.
	 * @return this attachment deserialized into a float array
	 * @throws SerializeException if the contained data is not a serialized
	 * float array
	 */
	public float[] getAsFloatArray() throws SerializeException {
		ByteArrayInputStream bais = new ByteArrayInputStream(getValue());
		try {
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (float[]) oos.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializeException("Caught IOException deserializing Attachement to float array");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializeException("Caught ClassNotFoundException deserializing Attachement to float array");
		}
	}
	/**
	 * Deserializes the contents of this attachment into a string array.
	 * Will only work if this Attachment actually contains a serialized string
	 * array. If this is not the case, a SerializeException will be thrown.
	 * @return this attachment deserialized into a string array
	 * @throws SerializeException if the contained data is not a serialized
	 * string array
	 */
	public String[] getAsStringArray() throws SerializeException {
		ByteArrayInputStream bais = new ByteArrayInputStream(getValue());
		try {
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (String[]) oos.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializeException("Caught IOException deserializing Attachement to string array");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializeException("Caught ClassNotFoundException deserializing Attachement to string array");
		}
	}
	/**
	 * Deserializes the contents of this attachment into a integer array.
	 * Will only work if this Attachment actually contains a serialized integer
	 * array. If this is not the case, a SerializeException will be thrown.
	 * @return this attachment deserialized into a integer array
	 * @throws SerializeException if the contained data is not a serialized
	 * integer array
	 */
	public int[] getAsIntArray() throws SerializeException {
		ByteArrayInputStream bais = new ByteArrayInputStream(getValue());
		try {
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (int[]) oos.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializeException("Caught IOException deserializing Attachement to int array");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializeException("Caught ClassNotFoundException deserializing Attachement to int array");
		}
	}
	/**
	 * Deserializes the contents of this attachment into a double array.
	 * Will only work if this Attachment actually contains a serialized double
	 * array. If this is not the case, a SerializeException will be thrown.
	 * @return this attachment deserialized into a double array
	 * @throws SerializeException if the contained data is not a serialized
	 * double array
	 */
	public double[] getAsDoubleArray() throws SerializeException {
		ByteArrayInputStream bais = new ByteArrayInputStream(getValue());
		try {
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (double[]) oos.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializeException("Caught IOException deserializing Attachement to double array");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializeException("Caught ClassNotFoundException deserializing Attachement to double array");
		}
	}
	/**
	 * Deserializes the contents of this attachment into a string.
	 * Will only work if this Attachment actually contains a serialized string.
	 * If this is not the case, a SerializeException will be thrown.
	 * @return this attachment deserialized into a string
	 * @throws SerializeException if the contained data is not a serialized
	 * string
	 */
	public String getAsString() throws SerializeException{
		ByteArrayInputStream bais = new ByteArrayInputStream(getValue());
		try {
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (String) oos.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializeException("Caught IOException deserializing Attachement to String");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializeException("Caught ClassNotFoundException deserializing Attachement to String");
		}

	}
}
