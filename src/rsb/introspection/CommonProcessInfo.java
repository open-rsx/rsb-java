/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.introspection;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates common process info functionality shared between
 * portable and non-portable subclasses.
 *
 * @author swrede
 */
public abstract class CommonProcessInfo implements ProcessInfo {

    // process id
    protected int pid;
    protected String name;
    protected List<String> arguments = new ArrayList<String>();
    protected String userName;

    // TODO add documentation
    long startTime;

    public CommonProcessInfo() {
        this.userName = readUserName();
    }

    private String readUserName() {
        String userName = System.getProperty("user.name");
        if (userName.isEmpty()) {
            userName = "unknown";
        }
        return userName;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getPid()
     */
    @Override
    public int getPid() {
        return this.pid;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getProgramName()
     */
    @Override
    public String getProgramName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getArguments()
     */
    @Override
    public List<String> getArguments() {
        return this.arguments;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getStartTime()
     */
    @Override
    public long getStartTime() {
        return this.startTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getUserName()
     */
    @Override
    public String getUserName() {
        return this.userName;
    }

}
