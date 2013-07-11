package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConverterSelectionStrategy;
import rsb.filter.AbstractFilterObserver;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;
import spread.SpreadException;

/**
 * An {@link InPushConnector} for the spread daemon network.
 *
 * @author jwienke
 */
public class SpreadInPushConnector extends AbstractFilterObserver implements
        InPushConnector {

    private final static Logger LOG = Logger
            .getLogger(SpreadInPushConnector.class.getName());

    private EventHandler eventHandler;
    private ReceiverTask receiver;
    private final SpreadWrapper spread;

    private final ConverterSelectionStrategy<ByteBuffer> inStrategy;

    public SpreadInPushConnector(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> inStrategy) {
        this.spread = spread;
        this.inStrategy = inStrategy;
    }

    @Override
    public void activate() throws InitializeException {
        this.receiver = new ReceiverTask(this.spread, this.eventHandler,
                this.inStrategy);
        // activate spread connection
        if (!this.spread.isActive()) {
            this.spread.activate();
        }
        this.receiver.setPriority(Thread.NORM_PRIORITY + 2);
        this.receiver.setName("ReceiverTask [grp="
                + this.spread.getPrivateGroup() + "]");
        this.receiver.start();
    }

    @Override
    public void deactivate() throws RSBException {
        if (this.spread.isActive()) {
            LOG.fine("deactivating SpreadPort");
            this.spread.deactivate();
        }
        try {
            this.receiver.join();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notify(final ScopeFilter e, final FilterAction a) {
        LOG.fine("SpreadPort::notify(ScopeFilter e, FilterAction=" + a.name()
                + " called");
        switch (a) {
        case ADD:
            // TODO add reference handling from xcf4j
            this.joinSpreadGroup(e.getScope());
            break;
        case REMOVE:
            // TODO add reference handling from xcf4j
            this.leaveSpreadGroup(e.getScope());
            break;
        case UPDATE:
            LOG.info("Update of ScopeFilter requested on SpreadSport");
            break;
        default:
            break;
        }
    }

    private void joinSpreadGroup(final Scope scope) {
        if (this.spread.isActive()) {
            // join group
            try {
                this.spread.join(SpreadUtilities.spreadGroupName(scope));
            } catch (final SpreadException e) {
                throw new RuntimeException(
                        "Unable to join spread group for scope '" + scope
                                + "' with hash '"
                                + SpreadUtilities.spreadGroupName(scope) + "'.",
                        e);
            }
        } else {
            LOG.severe("Couldn't set up network filter, spread inactive.");
        }
    }

    private void leaveSpreadGroup(final Scope scope) {
        if (this.spread.isActive()) {
            this.spread.leave(SpreadUtilities.spreadGroupName(scope));
        } else {
            LOG.severe("Couldn't remove group filter, spread inactive.");
        }
    }

    @Override
    public void addHandler(final EventHandler handler) {
        // TODO make handler multiple handlers
        assert handler != null;
        this.eventHandler = handler;
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        if (handler != this.eventHandler) {
            return false;
        }
        // TODO remove it from the receive task!
        this.eventHandler = null;
        return true;
    }

    @Override
    public String getType() {
        return "SpreadPort";
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // TODO what can we do here
    }

    @Override
    public boolean isActive() {
        return this.spread.isActive();
    }

}
