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
package rsb.examples;

import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import rsb.AbstractDataHandler;
import rsb.Factory;
import rsb.Listener;
import rsb.config.ParticipantConfig;
import rsb.config.TransportConfig;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.ConverterRepository;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.ConverterSignature;
import rsb.converter.PredicateConverterSelectionStrategy;
import rsb.converter.PredicateConverterSelectionStrategy.ExactKeyPredicate;
import rsb.converter.PredicateConverterSelectionStrategy.RegExPredicate;
import rsb.converter.StringConverter;
import rsb.converter.UserData;
import rsb.converter.WireContents;

/**
 * An example that demonstrates how to configure custom converter selection
 * strategies. The general logic is along the lines of
 * {@link DataListenerExample}.
 *
 * @author jwienke
 */
public class CustomConverterSelection extends AbstractDataHandler<Object> {

    private static final Logger LOG = Logger
            .getLogger(CustomConverterSelection.class.getName());

    private static AtomicInteger counter = new AtomicInteger(0);
    private static Object condition = new Object();

    @Override
    public void handleEvent(final Object data) {
        counter.getAndIncrement();
        if (counter.get() % 100 == 0) {
            LOG.info("Event #" + counter.get() + " received with payload: "
                    + data);
        }
        if (counter.get() == 1000) {
            synchronized (condition) {
                condition.notifyAll();
            }
        }
    }

    /**
     * A completely custom converter that always adds a string prefix to
     * deserialized strings.
     *
     * @author jwienke
     */
    private static class PrefixStringConverter extends StringConverter {

        @Override
        public UserData<ByteBuffer> deserialize(final String wireSchema,
                final ByteBuffer bytes) throws ConversionException {
            final UserData<ByteBuffer> data =
                    super.deserialize(wireSchema, bytes);
            return new UserData<ByteBuffer>("MYPREFIX"
                    + (String) data.getData(), data.getTypeInfo());
        }

    }

    /**
     * A converter that doesn't convert anything and just returns
     * <code>null</code> all the time for deserialization.
     *
     * @author jwienke
     */
    private static class NoConverter implements Converter<ByteBuffer> {

        @Override
        public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
                final Object obj) throws ConversionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserData<ByteBuffer> deserialize(final String wireSchema,
                final ByteBuffer buffer) throws ConversionException {
            return new UserData<ByteBuffer>(null, Object.class);
        }

        @Override
        public ConverterSignature getSignature() {
            // return dummy values. This won't be used as we are using this
            // converter in a predicate based selection
            return new ConverterSignature("undefined", Object.class);
        }

    }

    private static class CustomConverterRepository implements
            ConverterRepository<ByteBuffer> {

        @Override
        public ConverterSelectionStrategy<ByteBuffer>
                getConvertersForSerialization() {
            // we do not need converters for serializing messages as we are only
            // receiving events in this example
            throw new UnsupportedOperationException();
        }

        @Override
        public ConverterSelectionStrategy<ByteBuffer>
                getConvertersForDeserialization() {

            // create a new instance of a selection strategy for our
            // deserialization case. In this case we take a strategy that uses
            // predicates to determine the converter to select.
            final PredicateConverterSelectionStrategy<ByteBuffer> strategy =
                    new PredicateConverterSelectionStrategy<ByteBuffer>();

            // add converters to this strategy
            final PrefixStringConverter stringConverter =
                    new PrefixStringConverter();
            strategy.addConverter(new ExactKeyPredicate(stringConverter
                    .getSignature().getSchema()), stringConverter);

            // add a wildcard converter for everything else with lower priority
            final NoConverter noConverter = new NoConverter();
            strategy.addConverter(new RegExPredicate(Pattern.compile(".*")),
                    noConverter);

            return strategy;

        }

        @Override
        public void addConverter(final Converter<ByteBuffer> converter) {
            // we won't use this method since we know all our converters in
            // advance
        }

    }

    public static void main(final String[] args) throws Throwable {

        final Factory factory = Factory.getInstance();

        // create a new participant config by using the default one as a
        // template
        final ParticipantConfig config =
                factory.getDefaultParticipantConfig().copy();
        for (final Entry<String, TransportConfig> entry : config
                .getTransports().entrySet()) {
            // for each transport, configure our own converter repository
            entry.getValue().setConverters(new CustomConverterRepository());
        }

        // for the new participant, use the created config
        final Listener listener =
                factory.createListener("/example/informer", config);

        // now continue as usual for receiving events
        listener.activate();

        try {
            listener.addHandler(new CustomConverterSelection(), true);
            while (!allEventsDelivered()) {
                synchronized (condition) {
                    condition.wait(1000);
                    LOG.fine("Wake-Up!!!");
                }
            }
        } finally {
            listener.deactivate();
        }

    }

    private synchronized static boolean allEventsDelivered() {
        return !(counter.get() != 1000);
    }

}
