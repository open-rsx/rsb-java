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
package rsb.introspection;

import rsb.ParticipantId;
import rsb.Scope;

/**
 * Internal data holder for participant information. Used within introspection
 * model.
 *
 * @author swrede
 * @author ssharma
 */
public class ParticipantInfo {

    private final String kind;
    private final ParticipantId id;
    private final ParticipantId parentId;
    private final Scope scope;
    private final Class<?> type;

    public ParticipantInfo(final String kind, final ParticipantId id,
            final ParticipantId parentId, final Scope scope, final Class<?> type) {
        this.kind = kind;
        this.id = id;
        this.parentId = parentId;
        this.scope = scope;
        this.type = type;
    }

    public String getKind() {
        return this.kind;
    }

    public ParticipantId getId() {
        return this.id;
    }

    public ParticipantId getParentId() {
        return this.parentId;
    }

    public Scope getScope() {
        return this.scope;
    }

    public Class<?> getType() {
        return this.type;
    }

}
