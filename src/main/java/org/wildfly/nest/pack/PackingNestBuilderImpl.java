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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
class PackingNestBuilderImpl implements PackingNestBuilder {

    private Map<String, EntryLocation> sourceLocations = Collections.emptyMap();
    private Map<String, EntryLocation> nestLocations = Collections.emptyMap();
    private Map<String, EntryLocation> targetLocations = Collections.emptyMap();

    private List<EntrySource> entries = Collections.emptyList();

    private final EntryUnpackToBuilder unpackToBuilder = new EntryUnpackToBuilderImpl();
    private final EntryUnderBuilder underBuilder = new EntryUnderBuilderImpl();

    @Override
    public PackingNestBuilder nameSourceLocation(String name) {
        addSourceLocation(EntryLocation.name(name));
        return this;
    }

    @Override
    public PackingNestBuilder nameSourceLocation(String name, String locationName, String path) {
        addSourceLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public PackingNestBuilder linkSourceLocation(String name, String path) {
        addSourceLocation(EntryLocation.name(name, path));
        return this;
    }

    @Override
    public PackingNestBuilder nameNestLocation(String name, String path) {
        addNestLocation(EntryLocation.name(name, path));
        return this;
    }

    @Override
    public PackingNestBuilder nameNestLocation(String name, String locationName, String path) {
        addNestLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public PackingNestBuilder nameUnpackLocation(String name) {
        addTargetLocation(EntryLocation.name(name));
        return this;
    }

    @Override
    public PackingNestBuilder nameUnpackLocation(String name, String locationName, String path) {
        addTargetLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public EntryUnderBuilder add(String srcPath) {
        if(srcPath == null) {
            throw new IllegalArgumentException("Path is null");
        }
        addEntry(new EntrySourceImpl(EntryLocation.path(srcPath)));
        return underBuilder;
    }

    @Override
    public EntryUnderBuilder addLocation(String srcLocation, String relativePath) {
        if(srcLocation == null) {
            throw new IllegalArgumentException("Source location name is null");
        }
        assertSourceLocation(srcLocation);
        addEntry(new EntrySourceImpl(EntryLocation.path(srcLocation, relativePath)));
        return underBuilder;
    }

    @Override
    public File pack(File dir, String name) throws IOException {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                throw new IllegalStateException("Failed to create directory " + dir.getAbsolutePath());
            }
        } else if(!dir.isDirectory()) {
            throw new IllegalStateException("The path is not a directory " + dir.getAbsolutePath());
        }

        final File zip = new File(dir, name);
        if(zip.exists()) {
            zip.delete();
        }

        ZipOutputStream zos = null;
        try {
            final FileOutputStream fis = new FileOutputStream(zip);
            zos = new ZipOutputStream(new BufferedOutputStream(fis));
            for(EntrySource entry : entries) {
                final File f = new File(entry.getSourceLocation().getPath());
                ZipUtils.addToZip(f, zos);
            }
        } catch(FileNotFoundException e) {
            throw e;
        } finally {
            IoUtils.safeClose(zos);
        }
        return zip;
    }

    private void assertSourceLocation(String name) {
        if(!sourceLocations.containsKey(name)) {
            throw new IllegalStateException("Location not found: " + name);
        }
    }

    private void addEntry(EntrySource entry) {
        switch(entries.size()) {
            case 0:
                entries = Collections.singletonList(entry);
                break;
            case 1:
                entries = new ArrayList<EntrySource>(entries);
            default:
                entries.add(entry);
        }
    }
    private void addSourceLocation(EntryLocation el) {
        switch(sourceLocations.size()) {
            case 0:
                sourceLocations = Collections.<String, EntryLocation>singletonMap(el.getName(), el);
                break;
            case 1:
                sourceLocations = new HashMap<String, EntryLocation>(sourceLocations);
            default:
                sourceLocations.put(el.getName(), el);
        }
    }

    private void addNestLocation(EntryLocation el) {
        switch(nestLocations.size()) {
            case 0:
                nestLocations = Collections.<String, EntryLocation>singletonMap(el.getName(), el);
                break;
            case 1:
                nestLocations = new HashMap<String, EntryLocation>(nestLocations);
            default:
                nestLocations.put(el.getName(), el);
        }
    }

    private void addTargetLocation(EntryLocation el) {
        switch(targetLocations.size()) {
            case 0:
                targetLocations = Collections.<String, EntryLocation>singletonMap(el.getName(), el);
                break;
            case 1:
                targetLocations = new HashMap<String, EntryLocation>(targetLocations);
            default:
                targetLocations.put(el.getName(), el);
        }
    }

    class EntryUnpackToBuilderImpl extends DelegatingPackingNestBuilder implements EntryUnpackToBuilder {

        @Override
        public PackingNestBuilder unpackToLocation(String unpackLocation) {
            // TODO Auto-generated method stub
            return PackingNestBuilderImpl.this;
        }

        @Override
        public PackingNestBuilder unpackToLocation(String unpackLocation, String relativePath) {
            // TODO Auto-generated method stub
            return PackingNestBuilderImpl.this;
        }
    }

    class EntryUnderBuilderImpl extends EntryUnpackToBuilderImpl implements EntryUnderBuilder {

        @Override
        public EntryUnpackToBuilder under(String nestPath) {
            // TODO Auto-generated method stub
            return unpackToBuilder;
        }

        @Override
        public EntryUnpackToBuilder underLocation(String nestLocation) {
            // TODO Auto-generated method stub
            return unpackToBuilder;
        }

        @Override
        public EntryUnpackToBuilder underLocation(String nestLocation, String relativePath) {
            // TODO Auto-generated method stub
            return unpackToBuilder;
        }
    }

    class DelegatingPackingNestBuilder implements PackingNestBuilder {

        @Override
        public PackingNestBuilder nameSourceLocation(String name) {
            return PackingNestBuilderImpl.this.nameSourceLocation(name);
        }

        @Override
        public PackingNestBuilder nameSourceLocation(String name, String locationName, String path) {
            return PackingNestBuilderImpl.this.nameSourceLocation(name, locationName, path);
        }

        @Override
        public PackingNestBuilder linkSourceLocation(String name, String path) {
            return PackingNestBuilderImpl.this.linkSourceLocation(name, path);
        }

        @Override
        public PackingNestBuilder nameNestLocation(String name, String path) {
            return PackingNestBuilderImpl.this.nameNestLocation(name, path);
        }

        @Override
        public PackingNestBuilder nameNestLocation(String name, String locationName, String path) {
            return PackingNestBuilderImpl.this.nameNestLocation(name, locationName, path);
        }

        @Override
        public PackingNestBuilder nameUnpackLocation(String name) {
            return PackingNestBuilderImpl.this.nameUnpackLocation(name);
        }

        @Override
        public PackingNestBuilder nameUnpackLocation(String name, String locationName, String path) {
            return PackingNestBuilderImpl.this.nameUnpackLocation(name, locationName, path);
        }

        @Override
        public EntryUnderBuilder add(String srcPath) {
            return PackingNestBuilderImpl.this.add(srcPath);
        }

        @Override
        public EntryUnderBuilder addLocation(String locationName, String relativePath) {
            return PackingNestBuilderImpl.this.addLocation(locationName, relativePath);
        }

        @Override
        public File pack(File dir, String name) throws IOException {
            return PackingNestBuilderImpl.this.pack(dir, name);
        }
    }
}
