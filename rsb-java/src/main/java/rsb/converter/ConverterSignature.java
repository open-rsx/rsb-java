/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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

    /** Contains the wire schema of the converter. */
    private final String schema;
    /** Contains the data type that this converter can use. */
    private final Class<?> dataType;

    /**
     * Instantiates a new signature object.
     *
     * @param schema
     *            the wire schema produced by a converter
     * @param dataType
     *            the datatype handled by a converter.
     */
    public ConverterSignature(final String schema, final Class<?> dataType) {
        assert dataType != null;
        assert schema != null;
        this.schema = schema;
        this.dataType = dataType;
    }

    /**
     * Returns the wire schema the described converter produces and reads.
     *
     * @return wire schema string
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * Returns the data type of the described converter.
     *
     * @return data type class
     */
    public Class<?> getDataType() {
        return this.dataType;
    }

    @SuppressWarnings({ "PMD.AvoidFinalLocalVariable",
            "PMD.DataflowAnomalyAnalysis" })
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.dataType.hashCode();
        result = prime * result + this.schema.hashCode();
        return result;
    }

    @SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity" })
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConverterSignature)) {
            return false;
        }
        final ConverterSignature other = (ConverterSignature) obj;
        if (!this.dataType.equals(other.dataType)) {
            return false;
        }
        if (!this.schema.equals(other.schema)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConverterSignature [schema=" + this.schema + ", datatype="
                + this.dataType + "]";
    }

}
