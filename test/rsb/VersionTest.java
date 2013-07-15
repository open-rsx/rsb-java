package rsb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test for {@link Version}.
 *
 * @author jwienke
 */
public class VersionTest {

    @Test
    public void versionString() {
        assertNotNull(Version.getInstance().getVersionString());
        assertFalse(Version.getInstance().getVersionString().isEmpty());
    }

    @Test
    public void lastCommit() {
        assertNotNull(Version.getInstance().getLastCommit());
        assertFalse(Version.getInstance().getLastCommit().isEmpty());
    }

}
