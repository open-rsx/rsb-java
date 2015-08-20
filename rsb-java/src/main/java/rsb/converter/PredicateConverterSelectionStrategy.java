/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A converter selection strategy that uses predicates to select converters.
 * Converters that are added first have a higher priority than converters added
 * later.
 *
 * @author jwienke
 * @param <WireType>
 *            the wire type of the converters maintained in this selection
 *            strategy.
 */
public class PredicateConverterSelectionStrategy<WireType> implements
        ConverterSelectionStrategy<WireType> {

    private final List<PredicateConverterPair> converters = Collections
            .synchronizedList(new ArrayList<PredicateConverterPair>());

    /**
     * Interface for predicates that can be used to decide which converter to
     * select.
     *
     * @author jwienke
     */
    public interface Predicate {

        /**
         * Checks whether the converter with this predicate can handle the
         * specified key.
         *
         * @param key
         *            the key to check against
         * @return <code>true</code> if the converter shall handle this key,
         *         else <code>false</code>
         */
        boolean handlesKey(final String key);

    }

    /**
     * Checks whether a key exactly matches a given template.
     *
     * @author jwienke
     */
    public static class ExactKeyPredicate implements Predicate {

        private final String desired;

        /**
         * Constructor.
         *
         * @param desired
         *            the desired key to search for
         */
        public ExactKeyPredicate(final String desired) {
            this.desired = desired;
        }

        @Override
        public boolean handlesKey(final String key) {
            return key.equals(this.desired);
        }

    }

    /**
     * A {@link Predicate} that checks keys against a given regular expression.
     *
     * @author jwienke
     */
    public static class RegExPredicate implements Predicate {

        private final Pattern pattern;

        /**
         * Constructor.
         *
         * @param pattern
         *            the regular expression to use
         */
        public RegExPredicate(final Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean handlesKey(final String key) {
            return this.pattern.matcher(key).matches();
        }

    }

    private final class PredicateConverterPair {

        private final Predicate predicate;
        private final Converter<WireType> converter;

        public PredicateConverterPair(final Predicate predicate,
                final Converter<WireType> converter) {
            this.predicate = predicate;
            this.converter = converter;
        }

        public Predicate getPredicate() {
            return this.predicate;
        }

        public Converter<WireType> getConverter() {
            return this.converter;
        }

    }

    @Override
    public Converter<WireType> getConverter(final String key) {
        synchronized (this.converters) {
            for (final PredicateConverterPair pair : this.converters) {
                if (pair.getPredicate().handlesKey(key)) {
                    return pair.getConverter();
                }
            }
        }
        throw new NoSuchConverterException("No converter for key '" + key
                + "' available.");
    }

    /**
     * Adds a new converter to the strategy for the given predicate.
     *
     * @param predicate
     *            the predicate deciding whether this converter will be chosen
     *            or not
     * @param converter
     *            the converter
     */
    public void addConverter(final Predicate predicate,
            final Converter<WireType> converter) {
        assert predicate != null;
        assert converter != null;
        this.converters.add(new PredicateConverterPair(predicate, converter));
    }

}
