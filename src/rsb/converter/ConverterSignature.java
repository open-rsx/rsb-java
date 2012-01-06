/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.converter;

/**
 * Immutable object representing a converter signature. Used to index the
 * converter maps.
 * 
 * @author swrede
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

	@Override
	public String toString() {
		return "ConverterSignature [schema=" + schema + ", datatype="
				+ datatype + "]";
	}	
	
}