package rsb.introspection;

import java.util.logging.Logger;

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;

/**
 * Observer class to log participant creation and destruction.
 *
 * @author swrede
 */
public class LoggingObserver implements ParticipantObserver {

    private static final Logger LOG = Logger.getLogger(LoggingObserver.class
            .getName());

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {
        LOG.info("New participant created: " + participant.getId() + " at "
                + participant.getScope() + " with parent: " + args.getParent());
    }

    @Override
    public void destroyed(final Participant participant) {
        LOG.info("Participant removed: " + participant.getId() + " at "
                + participant.getScope());
    }

}
