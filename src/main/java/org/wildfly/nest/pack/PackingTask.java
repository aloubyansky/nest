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

package org.wildfly.nest.pack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
class PackingTask {

    static final Logger log = Logger.getLogger(PackingTask.class.getName());

    static PackingTask forEntries(List<EntrySource> entries) {
        return new PackingTask(entries);
    }

    private Map<String, EntryLocation> sourceLocations = Collections.emptyMap();
    private Map<String, EntryLocation> nestLocations = Collections.emptyMap();
    private Map<String, EntryLocation> unpackLocations = Collections.emptyMap();

    private final List<EntrySource> entries;
    private File zipTo;

    private PackingTask(List<EntrySource> entries) {
        if(entries == null) {
            throw new IllegalArgumentException("entries is null");
        }
        this.entries = entries;
    }

    PackingTask setSourceLocations(Map<String, EntryLocation> sourceLocations) {
        if(sourceLocations == null) {
            throw new IllegalArgumentException("sourceLocations is null");
        }
        this.sourceLocations = sourceLocations;
        return this;
    }

    PackingTask setNestLocations(Map<String, EntryLocation> nestLocations) {
        if(nestLocations == null) {
            throw new IllegalArgumentException("nestLocations is null");
        }
        this.nestLocations = nestLocations;
        return this;
    }

    PackingTask setUnpackLocations(Map<String, EntryLocation> unpackLocations) {
        if(unpackLocations == null) {
            throw new IllegalArgumentException("unpackLocations is null");
        }
        this.unpackLocations = unpackLocations;
        return this;
    }

    PackingTask zipTo(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        this.zipTo = file;
        return this;
    }

    void run() throws NestException {

        if(zipTo == null) {
            throw new NestException("zipTo file was not specified");
        }

        if(zipTo.exists()) {
            zipTo.delete();
        }

        ZipOutputStream zos = null;
        try {
            final FileOutputStream fis = new FileOutputStream(zipTo);
            zos = new ZipOutputStream(new BufferedOutputStream(fis));
            for(EntrySource entry : entries) {
                final String srcPath = resolvePath(entry.getSourceLocation(), sourceLocations);
                if(srcPath == null) {
                    throw new NestException("Failed to resolve source path for location " + entry.getSourceLocation());
                }
                final String nestPath = resolvePath(entry.getNestEntry().getNestLocation(), nestLocations);
                if(log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "adding " + srcPath + " as " + nestPath);
                }
                ZipUtils.addToZip(new File(srcPath), nestPath, zos);
            }
        } catch (IOException e) {
            throw new NestException("Failed to create ZIP " + zipTo.getAbsolutePath(), e);
        } finally {
            IoUtils.safeClose(zos);
        }
    }

    private String resolvePath(EntryLocation location, Map<String, EntryLocation> locations) throws NestException {
        if(location == EntryLocation.DEFAULT) {
            return null;
        }

        final String relativeToName = location.getRelativeTo();
        if(relativeToName == null) {
            return location.getPath();
        }

        final EntryLocation relativeToLocation = locations.get(relativeToName);
        if(relativeToLocation == null) {
            throw new NestException("Missing location definition for " + relativeToName);
        }
        final String resolved = resolvePath(relativeToLocation, locations);
        if(resolved == null) {
            return location.getPath();
        }
        if(location.getPath() == null) {
            return resolved;
        }
        return resolved + ZipUtils.ENTRY_SEPARATOR + location.getPath();
    }
}
