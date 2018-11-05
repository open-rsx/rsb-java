/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2018 CoR-Lab, Bielefeld University
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

package rsb.plugin;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import rsb.plugin.correct.Plugin;

/**
 * @author jwienke
 */
public class PluginManagerTest {

    @Test
    public void loadsCorrectly() throws Exception {

        final Set<String> names = new HashSet<>();
        names.add(Plugin.class.getPackage().getName());

        Plugin.resetInitializeState();

        new PluginManager().loadPlugins(names);

        assertTrue(Plugin.wasInitializeCalled());

    }

    @Test(expected = LoadingException.class)
    public void doubleLoadingException() throws Exception {

        final Set<String> names = new HashSet<>();
        names.add(Plugin.class.getPackage().getName());

        final PluginManager manager = new PluginManager();
        manager.loadPlugins(names);
        manager.loadPlugins(names);

    }

    @Test(expected = LoadingException.class)
    public void unknownException() throws Exception {

        final Set<String> names = new HashSet<>();
        names.add("this.does.not.exist");

        new PluginManager().loadPlugins(names);

    }

    @Test(expected = LoadingException.class)
    public void passThroughException() throws Exception {

        final Set<String> names = new HashSet<>();
        names.add(rsb.plugin.failing.Plugin.class.getPackage().getName());

        new PluginManager().loadPlugins(names);

    }

    @Test(expected = LoadingException.class)
    public void inappropriateClassExceptopn() throws Exception {

        final Set<String> names = new HashSet<>();
        names.add(rsb.plugin.nottheclass.Plugin.class.getPackage().getName());

        new PluginManager().loadPlugins(names);

    }

}
