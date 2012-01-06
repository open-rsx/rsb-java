/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
package rsb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A scope defines a channel of the hierarchical unified bus covered by RSB. It
 * is defined by a surface syntax like "/a/deep/scope".
 * 
 * @author jwienke
 */
public class Scope {

	private List<String> components = new ArrayList<String>();

	private static final String COMPONENT_SEPARATOR = "/";
	private static final Pattern COMPONENT_REGEX = Pattern
			.compile("^[a-zA-Z0-9]+$");

	private Scope() {
	}

	/**
	 * Parses a scope from a string representation.
	 * 
	 * @param stringRep
	 *            string representation of the scope
	 * @throws IllegalArgumentException
	 *             if the given string does not have the right syntax
	 */
	public Scope(String stringRep) {

		if (stringRep.isEmpty()) {
			throw new IllegalArgumentException("Empty scope is invalid.");
		}
		if (!stringRep.startsWith(COMPONENT_SEPARATOR)) {
			throw new IllegalArgumentException(
					"Scope must start with a slash, given was '" + stringRep
							+ "'.");
		}

		// append missing trailing slash
		if (!stringRep.endsWith(COMPONENT_SEPARATOR)) {
			stringRep.concat(COMPONENT_SEPARATOR);
		}

		String[] rawComponents = stringRep.split(COMPONENT_SEPARATOR);

		for (int i = 1; i < rawComponents.length; ++i) {
			if (!COMPONENT_REGEX.matcher(rawComponents[i]).matches()) {
				throw new IllegalArgumentException(
						"Invalid character in component '" + rawComponents[i]
								+ "'. Given was scope '" + stringRep + "'.");
			}

			components.add(rawComponents[i]);
		}

	}

	/**
	 * Returns all components of the scope as an ordered list. Components are
	 * the names between the separator character '/'. The first entry in the
	 * list is the highest level of hierarchy. The scope '/' returns an empty
	 * list.
	 * 
	 * @return components of the represented scope as ordered list with highest
	 *         level as first entry
	 */
	public List<String> getComponents() {
		return new ArrayList<String>(components);
	}

	/**
	 * Creates a new scope that is a sub-scope of this one with the subordinated
	 * scope described by the given argument. E.g.
	 * "/this/is/".concat("/a/test/") results in "/this/is/a/test".
	 * 
	 * @param childScope
	 *            child to concatenate to the current scope for forming a
	 *            sub-scope
	 * @return new scope instance representing the created sub-scope
	 */
	public Scope concat(Scope childScope) {
		Scope result = new Scope();
		result.components.addAll(components);
		result.components.addAll(childScope.components);
		return result;
	}

	/**
	 * Tests whether this scope is a sub-scope of the given other scope, which
	 * means that the other scope is a prefix of this scope. E.g. "/a/b/" is a
	 * sub-scope of "/a/".
	 * 
	 * @param other
	 *            other scope to test
	 * @return <code>true</code> if this is a sub-scope of the other scope,
	 *         equality gives <code>false</code>, too
	 */
	public boolean isSubScopeOf(Scope other) {
		if (components.size() <= other.components.size()) {
			return false;
		}
		return other.components.equals(components.subList(0,
				other.components.size()));
	}

	/**
	 * Inverse operation of {@link #isSubScopeOf(Scope)}.
	 * 
	 * @param other
	 *            other scope to test
	 * @return <code>true</code> if this scope is a strict super scope of the
	 *         other scope. equality also gives <code>false</code>.
	 */
	public boolean isSuperScopeOf(Scope other) {
		if (components.size() >= other.components.size()) {
			return false;
		}
		return components
				.equals(other.components.subList(0, components.size()));
	}

	/**
	 * Generates all super scopes of this scope including the root scope "/".
	 * The returned list of scopes is ordered by hierarchy with "/" being the
	 * first entry.
	 * 
	 * @param includeSelf
	 *            if set to <code>true</code>, this scope is also included as
	 *            last element of the returned list
	 * @return list of all super scopes ordered by hierarchy, "/" being first
	 */
	public List<Scope> superScopes(boolean includeSelf) {

		List<Scope> superScopes = new ArrayList<Scope>();

		int lastIndex = components.size();
		if (!includeSelf) {
			lastIndex -= 1;
		}

		for (int i = 0; i <= lastIndex; ++i) {
			Scope superScope = new Scope();
			superScope.components = components.subList(0, i);
			superScopes.add(superScope);
		}

		return superScopes;

	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Scope)) {
			return false;
		}
		Scope other = (Scope) obj;
		return components.equals(other.components);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(COMPONENT_SEPARATOR);
		for (String com : components) {
			builder.append(com);
			builder.append(COMPONENT_SEPARATOR);
		}
		return builder.toString();
	}

}
