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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Linux-optimized implementation of process info interface.
 *
 * @author swrede
 */
public class LinuxProcessInfo extends CommonProcessInfo {

    private static final String PROC_STAT_FILE_PATH = "/proc/stat";

    private static final Logger LOG = Logger.getLogger(LinuxProcessInfo.class
            .getName());

    private static final String PROC_CMDLINE_FILE_PATH = "/proc/self/cmdline";
    private static final String PROC_SELF_STAT_FILE_PATH = "/proc/self/stat";

    /**
     * Creates a new instance and initializes provided variables.
     */
    public LinuxProcessInfo() {
        super();
        initialize();
    }

    private void initialize() {
        this.setPid(readPIDFromProcFS());
        this.setProgramName(readProgramNameFromProcFS());
        this.setArguments(readArgumentsFromProcFS());
        this.setStartTime(readStartTimeFromProcFS());
    }

    // TODO refactoring
    private String readProgramNameFromProcFS() {
        final StringBuilder pname = new StringBuilder();
        try {
            final FileInputStream fstream2 =
                    new FileInputStream(PROC_CMDLINE_FILE_PATH);
            final DataInputStream in2 = new DataInputStream(fstream2);
            final BufferedReader br2 =
                    new BufferedReader(new InputStreamReader(in2));
            String strLine2;
            while ((strLine2 = br2.readLine()) != null) {
                pname.append(strLine2);
            }
            in2.close();
        } catch (final IOException e) {
            LOG.log(Level.INFO, "Exception while reading program name from "
                    + PROC_CMDLINE_FILE_PATH, e);
        }
        final String programName =
                pname.subSequence(0, pname.indexOf("\0")).toString();
        if (programName.isEmpty()) {
            return "N/A";
        } else {
            return programName;
        }
    }

    // TODO refactoring
    private List<String> readArgumentsFromProcFS() {
        final List<String> parsedArguments = new ArrayList<String>();
        final StringBuilder cmd = new StringBuilder();
        try {
            final FileInputStream fstreamcmd =
                    new FileInputStream(PROC_CMDLINE_FILE_PATH);
            final DataInputStream incmd = new DataInputStream(fstreamcmd);
            final BufferedReader brcmd =
                    new BufferedReader(new InputStreamReader(incmd));
            String strLinecmd;

            while ((strLinecmd = brcmd.readLine()) != null) {
                cmd.append(strLinecmd);
            }

            incmd.close();
        } catch (final IOException e) {
            LOG.log(Level.INFO,
                    "Exception while reading program arguments from "
                            + PROC_CMDLINE_FILE_PATH, e);
        }

        // TODO review and refactor this parsing code
        while (cmd.indexOf("\0") != -1) {
            cmd.replace(cmd.indexOf("\0"), cmd.indexOf("\0") + 1, " ");
            // this.arguments.add(cmd.substring(cmd.indexOf("\0") -1 ,
            // cmd.indexOf("\0")).toString());
        }
        // initialize to null so that repeated call of function does not lead to
        // appending to last value
        final String[] args = cmd.toString().split(" ");
        for (final String string : args) {
            parsedArguments.add(string);
        }
        // remove first string (program name)
        parsedArguments.remove(0);
        return parsedArguments;
    }

    // TODO refactoring
    private long readStartTimeFromProcFS() {

        long startTimeBootJiffies = 0;
        final StringBuilder s = new StringBuilder();
        try {
            final FileInputStream fstream1 =
                    new FileInputStream(PROC_SELF_STAT_FILE_PATH);
            final DataInputStream in1 = new DataInputStream(fstream1);
            final BufferedReader br1 =
                    new BufferedReader(new InputStreamReader(in1));
            String strLine1;

            while ((strLine1 = br1.readLine()) != null) {
                s.append(strLine1);
            }

            in1.close();
        } catch (final IOException e) {
            LOG.log(Level.INFO,
                    "Exception while reading process start time from "
                            + PROC_SELF_STAT_FILE_PATH, e);
        }

        // TODO check this for possible errors and refactor it
        final Pattern p1 =
                Pattern.compile("^[\\d]*[\\s]*\\([\\w]*\\)[\\s]*[\\w]*[\\s]*([-+]*[\\d]*[\\s]*){18}([\\d]*)[\\s]*.*");
        final Matcher m1 = p1.matcher(s);
        if (m1.matches()) {
            startTimeBootJiffies = Long.parseLong(m1.group(2));
        }
        long bootTimeUNIXSeconds = 0;
        try {
            final FileInputStream fstream2 =
                    new FileInputStream(PROC_STAT_FILE_PATH);
            final DataInputStream in2 = new DataInputStream(fstream2);
            final BufferedReader br2 =
                    new BufferedReader(new InputStreamReader(in2));
            String strLine2;
            while ((strLine2 = br2.readLine()) != null) {
                final Pattern p = Pattern.compile("btime ([0-9]+)");
                final Matcher m = p.matcher(strLine2);
                if (m.matches()) {
                    bootTimeUNIXSeconds = Long.parseLong(m.group(1));
                }
            }
            // Close the input stream
            in2.close();
        } catch (final IOException e) {
            LOG.log(Level.INFO,
                    "Exception while reading system boot time from "
                            + PROC_STAT_FILE_PATH, e);
            bootTimeUNIXSeconds = 0;
        }

        final long startTime =
                (1000000 * bootTimeUNIXSeconds + 10000 * startTimeBootJiffies);
        return startTime;
    }

    private int readPIDFromProcFS() {
        int processId = 0;
        final File procFSFile = new File(PROC_SELF_STAT_FILE_PATH);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(procFSFile));
            final String entry = reader.readLine();

            // TODO still need to check this for simplification
            // or whether to write a full parser for the procfs process info
            // format
            final Pattern p = Pattern.compile("^([\\d]*)[\\s]*.*");
            final Matcher m = p.matcher(entry);
            if (m.matches()) {
                processId = Integer.parseInt(m.group(1));
            } else {
                LOG.info("Could not parse or convert process id from "
                        + PROC_SELF_STAT_FILE_PATH + " entry.");
            }
        } catch (final IOException exception) {
            LOG.log(Level.INFO, "Exception while process entry from "
                    + PROC_SELF_STAT_FILE_PATH, exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    LOG.log(Level.FINE,
                            "Exception while closing input stream for "
                                    + PROC_SELF_STAT_FILE_PATH, e);
                }
            }
        }
        return processId;
    }

}
