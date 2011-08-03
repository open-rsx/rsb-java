package rsb.converter;

/**
 * Immutable object representing a converter signature. Used to index the
 * converter maps.
 * 
 * @author swrede
 *
 */
public class ConverterSignature {
	
	/** RSB WireSchema */
	private final String schema;
	/** Java Type */
	private final Class<?> datatype;

	public ConverterSignature(String schema, Class<?> datatype) {
		this.schema = schema;
		this.datatype = datatype;
	}	

	public String getSchema() {
		return schema;
	}

	public Class<?> getDatatype() {
		return datatype;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@SuppressWarnings({"PMD.AvoidFinalLocalVariable","PMD.DataflowAnomalyAnalysis"})
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings({"PMD.OnlyOneReturn","PMD.CyclomaticComplexity"})
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {			
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConverterSignature)) {
			return false;
		}
		ConverterSignature other = (ConverterSignature) obj;
		if (datatype == null) {
			if (other.datatype != null) {
				return false;
			}
		} else if (!datatype.equals(other.datatype)) {
			return false;
		}
		if (schema == null) {
			if (other.schema != null) {
				return false;
			}
		} else if (!schema.equals(other.schema)) {
			return false;
		}
		return true;
	}	
	
	
}