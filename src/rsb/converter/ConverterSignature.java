package rsb.converter;


public class ConverterSignature {
	
	/** RSB WireSchema */
	private String schema;
	/** Java Type */
	private String datatype;

	public ConverterSignature(String schema, String datatype) {
		this.schema = schema;
		this.datatype = datatype;
	}	

	public String getSchema() {
		return schema;
	}

	public String getDatatype() {
		return datatype;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConverterSignature) {
			ConverterSignature other = (ConverterSignature) obj;
			if (other.getDatatype().contentEquals(this.getDatatype()) && other.getSchema().contentEquals(this.getSchema())) {
				return true;
			}
		}
		return false;
	}	
}