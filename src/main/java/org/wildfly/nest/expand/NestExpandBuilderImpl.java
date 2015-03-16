/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.nest.expand;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestEntry;
import org.wildfly.nest.NestException;
import org.wildfly.nest.builder.AbstractCommonBuilder;
import org.wildfly.nest.util.ZipUtils;
import org.wildfly.nest.zip.expand.ZipNestExpander;

/**
 *
 * @author Alexey Loubyansky
 *
 */
public class NestExpandBuilderImpl extends AbstractCommonBuilder<NestExpandBuilder> implements NestExpandBuilder, NestExpandContext {

    private final File nestFile;

    private File baseExpandDir;

    private Map<String, File> linkedNestPaths;

    NestExpandBuilderImpl(File nestFile) {
        if(nestFile == null) {
            throw new IllegalArgumentException("nestFile is null");
        }
        if(!nestFile.exists()) {
            throw new IllegalArgumentException("File doesn't exist " + nestFile.getAbsolutePath());
        }
        if(!nestFile.isFile()) {
            throw new IllegalArgumentException(nestFile.getAbsolutePath() + " is not a file.");
        }
        this.nestFile = nestFile;
    }

    @Override
    public NestExpandBuilder linkExpandLocation(String expandLocationName, String expandPath) throws NestException {
        if(expandLocationName == null) {
            throw new IllegalArgumentException("expandLocationName is null");
        }
        if(expandPath == null) {
            throw new IllegalArgumentException("expandPath is null");
        }
        final EntryLocation entryLocation = assertExpandLocation(expandLocationName);
        entryLocation.link(expandPath);
        return this;
    }

    @Override
    public NestExpandBuilder linkExpandLocation(String expandLocationName, String relativeToName, String expandPath) throws NestException {
        if(expandLocationName == null) {
            throw new IllegalArgumentException("expandLocationName is null");
        }
        if(relativeToName == null) {
            throw new IllegalArgumentException("relativeToName is null");
        }
        if(expandPath == null) {
            throw new IllegalArgumentException("expandPath is null");
        }
        final EntryLocation entryLocation = assertExpandLocation(expandLocationName);
        entryLocation.link(relativeToName, expandPath);
        return this;
    }

    @Override
    public File getNestFile() {
        return nestFile;
    }

    @Override
    public File getBaseExpandDir() {
        return baseExpandDir;
    }

    @Override
    public File resolveExpandPath(String nestPath) {
        assert nestPath != null : "nest path is null";
        return resolveExpandPath(nestPath, nestPath.endsWith(ZipUtils.ENTRY_SEPARATOR));
    }

    protected File resolveExpandPath(String nestPath, boolean cache) {
        if(!linkedNestPaths.isEmpty()) {
            File expandPath = linkedNestPaths.get(nestPath);
            if(expandPath != null) {
                return expandPath;
            }

            int fromIndex = nestPath.endsWith(ZipUtils.ENTRY_SEPARATOR) ? nestPath.length() - 1 - ZipUtils.ENTRY_SEPARATOR.length() : nestPath.length() - 1;
            int sep = nestPath.lastIndexOf(ZipUtils.ENTRY_SEPARATOR, fromIndex);
            if(sep < 0) {
                expandPath = new File(baseExpandDir, nestPath);
            } else {
                String parent = nestPath.substring(0, sep + 1);
                File parentPath = resolveExpandPath(parent, false);
                expandPath = new File(parentPath, nestPath.substring(sep));
            }
            if(cache) {
                linkedNestPaths.put(nestPath, expandPath);
            }
            return expandPath;
        }
        return new File(baseExpandDir, nestPath);
    }

    @Override
    public void expand(File baseExpandDir) throws NestException {

        if(baseExpandDir == null) {
            throw new IllegalArgumentException("base expand dir is null");
        }

        if(baseExpandDir.exists() && !baseExpandDir.isDirectory()) {
            throw new IllegalArgumentException(baseExpandDir.getAbsolutePath() + " is not a directory");
        }

        this.baseExpandDir = baseExpandDir;
        linkNestPaths(baseExpandDir);

        ZipNestExpander.init().expand(this);
    }

    protected void linkNestPaths(File baseDir) throws NestException {
        assert baseDir != null : "base dir is null";
        final Collection<EntryLocation> nestLocations = getNestLocations();
        if(nestLocations.isEmpty()) {
            linkedNestPaths = Collections.emptyMap();
            return;
        }
        final Map<String,String> nestPathByName = new HashMap<String,String>(nestLocations.size());
        linkedNestPaths = new HashMap<String,File>(nestLocations.size());
        for(String nestName : getNestLocationNames()) {
            linkNestLocation(nestName, baseDir, nestPathByName);
        }
    }

    protected File linkNestLocation(String nestName, File base, Map<String,String> nestPathByName) throws NestException {
        String nestPath = nestPathByName.get(nestName);
        if(nestPath != null) {
            return linkedNestPaths.get(nestPath);
        }
        final EntryLocation nestLocation = assertNestLocation(nestName);
        File relativeTo = null;
        if(nestLocation.getRelativeTo() != null) {
            relativeTo = linkNestLocation(nestLocation.getRelativeTo(), base, nestPathByName);
            nestPath = nestLocation.getPath() != null ? nestPathByName.get(nestLocation.getRelativeTo()) + nestLocation.getPath() :
                nestPathByName.get(nestLocation.getRelativeTo());
        } else {
            nestPath = nestLocation.getPath();
            if(nestPath == null) {
                throw new NestException("Nest path is null for location " + nestLocation.getName());
            }
        }
        nestPathByName.put(nestName, nestPath);

        // a path could theoretically be mapped to two different expand paths
        // e.g. a named nest location could be linked to an expand path explicitly
        // then the nest path which corresponds to the named nest location
        // could explicitly be mapped (as a standalone nest path) to another expand path
        // so the following will just pick one of the mappings and use it for both
        File expandPath = linkedNestPaths.get(nestPath);
        if(expandPath != null) {
            return expandPath;
        }

        final NestEntry nestEntry = getNestEntry(nestLocation.getName());
        if(nestEntry != null) {
            final EntryLocation expandLocation = nestEntry.getExpandLocation();
            assert expandLocation != null : "expand location is null";
            expandPath = resolveExpandLocation(expandLocation);
        } else {
            // not linked named location
            final File relativeToDir = relativeTo == null ? base : relativeTo;
            if(nestLocation.getPath() == null) {
                expandPath = relativeToDir;
            } else {
                expandPath = new File(relativeToDir, nestLocation.getPath());
            }
        }
        linkedNestPaths.put(nestPath, expandPath);
        return expandPath;
    }

    protected File resolveExpandLocation(EntryLocation location) throws NestException {
        if(location.getRelativeTo() != null) {
            final EntryLocation relativeTo = assertExpandLocation(location.getRelativeTo());
            final File relativeToDir = resolveExpandLocation(relativeTo);
            if(location.getPath() == null) {
                return relativeToDir;
            } else {
                return new File(relativeToDir, location.getPath());
            }
        }
        final String path = location.getPath();
        if(path == null) {
            throw new NestException("Expand location " + location.getName() + " is missing path.");
        }
        return new File(this.baseExpandDir, path);
    }
}
