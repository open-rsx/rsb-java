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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author jwienke
 */
public class PluginManager {

    private final Map<String, Plugin> loadedPlugins =
            new HashMap<String, Plugin>();

    /**
     * @param pluginNames
     *                        Names of the plugins to load
     * @throws LoadingException
     *                              Loading a plugin failed
     */
    public void loadPlugins(final Set<String> pluginNames)
            throws LoadingException {

        for (final String pluginName : pluginNames) {
            if (this.loadedPlugins.containsKey(pluginName)) {
                throw new LoadingException(
                        "Plugin " + pluginName + " was already loaded");
            }

            final String className = pluginName + "." + "Plugin";

            try {
                final Class<?> pluginClass = Class.forName(className);

                final Plugin instance = (Plugin) pluginClass.newInstance();

                instance.initialize();

                this.loadedPlugins.put(pluginName, instance);

            } catch (final ClassNotFoundException e) {
                throw this.prepareException(className,
                        "cannot be found on the classpath", e);
            } catch (final ClassCastException e) {
                throw this.prepareException(className, "is not an instance of "
                        + Plugin.class.getCanonicalName(), e);
            } catch (final InstantiationException | IllegalAccessException e) {
                throw this.prepareException(className, "cannot be instantiated",
                        e);
            }
        }

    }

    private LoadingException prepareException(final String className,
            final String reason, final Exception cause) {
        return new LoadingException("Plugin class " + className + " " + reason,
                cause);
    }

}
