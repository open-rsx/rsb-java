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
package rsb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to help reading contents of files.
 *
 * @author jwienke
 */
public final class FileReadingUtilities {

    private FileReadingUtilities() {
        // prevent utility class instantiation
    }

    /**
     * Reads the complete contents of a file into a list of lines. Any reading
     * error is reported as an {@link IOException}.
     *
     * @param file
     *            file to read
     * @return file lines
     * @throws IOException
     *             any error reading the file
     */
    public static List<String> readFileLines(final File file)
            throws IOException {

        if (file == null) {
            throw new IOException("File cannot be null");
        }
        if (!file.isFile()) {
            throw new IOException("File must be a real file and nothing else");
        }

        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try {

            final List<String> lines = new LinkedList<String>();

            String line;
            while ((line = reader.readLine()) != null) { // NOPMD: common
                lines.add(line);
            }

            return lines;

        } finally {
            reader.close();
        }

    }

    /**
     * Reads the first line of a file into a string. Any reading error is
     * reported as an {@link IOException}.
     *
     * This method is not intended for large files since all lines are read into
     * memory before selecting the first line
     *
     * @param file
     *            file to read
     * @return first file line
     * @throws IOException
     *             any error reading the file, also if file is empty
     */
    public static String readFirstFileLine(final File file) throws IOException {

        final List<String> lines = readFileLines(file);
        if (lines.isEmpty()) {
            throw new IOException("File does not contain lines");
        }
        return lines.get(0);

    }

}
