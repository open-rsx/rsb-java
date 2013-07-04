/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010, 2011, 2012 CoR-Lab, Bielefeld University
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

// mark-start::body
import rsb.Factory;
import rsb.Informer;

public class InformerExample {

    public static void main(final String[] args) throws Throwable {

        // Get a factory instance to create RSB objects.
        final Factory factory = Factory.getInstance();

        // Create an informer on scope "/exmaple/informer".
        final Informer<Object> informer = factory
                .createInformer("/example/informer");

        // Activate the informer to be ready for work
        informer.activate();

        // Send and event using a method that accepts the data and
        // automatically creates an appropriate event internally.
        informer.send("example payload");

        // As there is no explicit removal model in java, always manually
        // deactivate the informer if it is not needed anymore
        informer.deactivate();

    }

}
// mark-end::body
