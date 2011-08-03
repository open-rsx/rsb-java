package rsb.converter;

/**
*
* Maintains a collection of converters for a specific wire format. Each
* converter has a wire type describing the actual message that is written on
* the wire and a data type that indicates which data it can serialize on the
* wire.
* 
* @author swrede
*
*/
public interface ConverterRepository<WireType> {

	/**
	 * This method queries the converter map for seralizable data types
	 * and returns an UnambiguousConverterMap for the chosen <WireType> 
	 * to the caller.
	 * 
	 * @return ConverterSelectionStrategy object for serialization
	 */
	public abstract ConverterSelectionStrategy<WireType> getConvertersForSerialization();

	public abstract ConverterSelectionStrategy<WireType> getConvertersForDeserialization();

	public abstract void addConverter(Converter<WireType> converter);

}