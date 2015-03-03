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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;

/**
 *
 * @author Alexey Loubyansky
 */
class PackingNestBuilderImpl implements PackingNestBuilder {

    private Map<String, EntryLocation> sourceLocations = Collections.emptyMap();
    private Map<String, EntryLocation> nestLocations = Collections.emptyMap();
    private Map<String, EntryLocation> unpackLocations = Collections.emptyMap();

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
    public EntryUnderBuilder addLocation(String srcLocationName) {
        if(srcLocationName == null) {
            throw new IllegalArgumentException("Source location name is null");
        }
        EntryLocation srcLocation = assertSourceLocation(srcLocationName);
        addEntry(new EntrySourceImpl(srcLocation));
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
    public File pack(File dir, String name) throws NestException {
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
        PackingTask.forEntries(entries)
            .setSourceLocations(sourceLocations)
            .setNestLocations(nestLocations)
            .setUnpackLocations(unpackLocations)
            .zipTo(zip)
            .run();
        return zip;
    }

    private EntryLocation assertSourceLocation(String name) {
        final EntryLocation location = sourceLocations.get(name);
        if(location == null) {
            throw new IllegalStateException("Source location not found: " + name);
        }
        return location;
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
        switch(unpackLocations.size()) {
            case 0:
                unpackLocations = Collections.<String, EntryLocation>singletonMap(el.getName(), el);
                break;
            case 1:
                unpackLocations = new HashMap<String, EntryLocation>(unpackLocations);
            default:
                unpackLocations.put(el.getName(), el);
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
        public EntryUnderBuilder addLocation(String srcLocationName) {
            return PackingNestBuilderImpl.this.addLocation(srcLocationName);
        }

        @Override
        public File pack(File dir, String name) throws NestException {
            return PackingNestBuilderImpl.this.pack(dir, name);
        }
    }
}
