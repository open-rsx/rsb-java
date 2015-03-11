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
package rsb.util.os;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rsb.util.FileReadingUtilities;

/**
 * Linux-optimized implementation of process info interface.
 *
 * @author swrede
 * @author jwienke
 */
public class LinuxProcessInfo extends PortableProcessInfo {

    private static final Logger LOG = Logger.getLogger(LinuxProcessInfo.class
            .getName());

    private static final String PROC_STAT_FILE_PATH = "/proc/stat";
    private static final String PROC_CMDLINE_FILE_PATH = "/proc/self/cmdline";
    private static final String PROC_SELF_STAT_FILE_PATH = "/proc/self/stat";

    private static final int JIFFY_LENGTH = 10000;

    private static final Pattern PROC_STAT_FILE_PATTERN = Pattern
            .compile("^[\\d]*[\\s]*\\([\\w]*\\)[\\s]*[\\w]*[\\s]*"
                    + "([-+]*[\\d]*[\\s]*){18}([\\d]*)[\\s]*.*");

    private static final Pattern STAT_BOOT_TIME_PATTERN = Pattern
            .compile("btime ([0-9]+)");;

    /**
     * Creates a new instance and initializes provided variables from default
     * file paths.
     */
    public LinuxProcessInfo() {
        this(new File(PROC_CMDLINE_FILE_PATH), new File(PROC_STAT_FILE_PATH),
                new File(PROC_SELF_STAT_FILE_PATH));
    }

    /**
     * Creates a new instance with specified files from the proc filesystem.
     *
     * @param cmdLineFile
     *            path to the /proc/cmdline file
     * @param globalStatFile
     *            path to the /proc/stat file (system stats)
     * @param procStatFile
     *            path to the /proc/self/stat file (process stats)
     */
    public LinuxProcessInfo(final File cmdLineFile, final File globalStatFile,
            final File procStatFile) {
        super();
        final String processName = readProgramNameFromProcFS(cmdLineFile);
        if (processName != null) {
            try {
                this.setProgramName(processName + " "
                        + PortableProcessInfo.determineMainClassName());
            } catch (final NoSuchElementException e) {
                this.setProgramName(processName);
            }
        }
        this.setArguments(readArgumentsFromProcFS(cmdLineFile));
        this.setStartTime(readStartTimeFromProcFS(globalStatFile, procStatFile));
    }

    private static String readProgramNameFromProcFS(final File cmdLineFile) {

        try {

            String programName =
                    FileReadingUtilities.readFirstFileLine(cmdLineFile);

            // clean up
            final int nullPosition = programName.indexOf('\0');
            if (nullPosition >= 0) {
                programName = programName.substring(0, nullPosition);
            }
            programName = programName.trim();

            if (programName.isEmpty()) {
                return null;
            } else {
                return programName;
            }

        } catch (final IOException e) {
            return null;
        }

    }

    private List<String> readArgumentsFromProcFS(final File cmdLineFile) {

        try {

            final String cmdLine =
                    FileReadingUtilities.readFirstFileLine(cmdLineFile);

            final String[] args = cmdLine.split("\0");
            final List<String> argList =
                    new LinkedList<String>(Arrays.asList(args));
            if (!argList.isEmpty()) {
                argList.remove(0);
            }
            return argList;

        } catch (final IOException e) {
            return null;
        }

    }

    private static Long readStartTimeFromProcFS(final File globalStatFile,
            final File procStatFile) {

        LOG.fine("Starting to read process start time from proc filesystem");

        try {

            // CHECKSTYLE.OFF: MagicNumber - just a conversion
            return 1000000 * readComputerStartTime(globalStatFile)
                    + JIFFY_LENGTH * readProcessStartTime(procStatFile);
            // CHECKSTYLE.ON: MagicNumber

        } catch (final IOException e) {
            return null;
        }

    }

    private static long readComputerStartTime(final File globalStatFile)
            throws IOException {
        // then, get the computer start
        final List<String> globalStatLines =
                FileReadingUtilities.readFileLines(globalStatFile);
        Long bootTimeUnixSeconds = null;
        for (final String line : globalStatLines) {
            final Matcher lineMatcher = STAT_BOOT_TIME_PATTERN.matcher(line);
            if (lineMatcher.matches()) {
                bootTimeUnixSeconds = Long.parseLong(lineMatcher.group(1));
            }
        }
        if (bootTimeUnixSeconds == null) {
            LOG.warning("Computer boot time could not be read.");
            throw new IOException(
                    "No line in proc file matches boot time pattern");
        }
        return bootTimeUnixSeconds;
    }

    private static long readProcessStartTime(final File procStatFile)
            throws IOException {
        final String procStatLine =
                FileReadingUtilities.readFirstFileLine(procStatFile);
        final Matcher procStatLineMatcher =
                PROC_STAT_FILE_PATTERN.matcher(procStatLine);
        if (!procStatLineMatcher.matches()) {
            LOG.log(Level.WARNING,
                    "Pattern {0} does not match the line read from {1}, "
                            + "which is:\n{2}",
                    new Object[] { PROC_STAT_FILE_PATTERN, procStatFile,
                            procStatLine });
            throw new IOException("Could not match pattern against proc file");
        }
        return Long.parseLong(procStatLineMatcher.group(2));
    }
}
