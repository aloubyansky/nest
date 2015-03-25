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

package org.wildfly.nest.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;
import org.wildfly.nest.util.ZipUtils;
import org.wildfly.nest.zip.build.ZipNestBuilder;

/**
 *
 * @author Alexey Loubyansky
 */
class NestBuildTaskImpl implements NestBuildTask, NestBuildContext {

    private Map<String, EntryLocation> sourceLocations = Collections.emptyMap();
    private Map<String, EntryLocation> nestLocations = Collections.emptyMap();
    private Map<String, EntryLocation> expandLocations = Collections.emptyMap();

    private List<NestEntrySource> entries = Collections.emptyList();

    private final EntryExpandToBuilder expandToBuilder = new EntryExpandToBuilderImpl();
    private final EntryUnderBuilder underBuilder = new EntryUnderBuilderImpl();

    private File nestFile;

    @Override
    public NestBuildTask nameSourceLocation(String name) {
        addSourceLocation(EntryLocation.name(name));
        return this;
    }

    @Override
    public NestBuildTask nameSourceLocation(String name, String locationName, String path) {
        addSourceLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public NestBuildTask linkSourceLocation(String name, String path) {
        addSourceLocation(EntryLocation.name(name, path));
        return this;
    }

    @Override
    public NestBuildTask nameNestLocation(String name, String path) {
        addNestLocation(EntryLocation.name(name, path));
        return this;
    }

    @Override
    public NestBuildTask nameNestLocation(String name, String locationName, String path) {
        addNestLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public NestBuildTask nameExpandLocation(String name) {
        addExpandLocation(EntryLocation.name(name));
        return this;
    }

    @Override
    public NestBuildTask nameExpandLocation(String name, String locationName, String path) {
        addExpandLocation(EntryLocation.name(name, locationName, path));
        return this;
    }

    @Override
    public EntryUnderBuilder add(String srcPath) {
        if(srcPath == null) {
            throw new IllegalArgumentException("Path is null");
        }
        addEntry(new NestEntrySource(EntryLocation.path(srcPath)));
        return underBuilder;
    }

    @Override
    public EntryUnderBuilder addLocation(String srcLocationName) {
        if(srcLocationName == null) {
            throw new IllegalArgumentException("Source location name is null");
        }
        EntryLocation srcLocation = assertSourceLocation(srcLocationName);
        addEntry(new NestEntrySource(srcLocation));
        return underBuilder;
    }

    @Override
    public EntryUnderBuilder addLocation(String srcLocation, String relativePath) {
        if(srcLocation == null) {
            throw new IllegalArgumentException("Source location name is null");
        }
        assertSourceLocation(srcLocation);
        addEntry(new NestEntrySource(EntryLocation.path(srcLocation, relativePath)));
        return underBuilder;
    }


    @Override
    public File getNestFile() {
        return nestFile;
    }

    @Override
    public List<NestEntrySource> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public Collection<String> getNestLocationNames() {
        return Collections.unmodifiableCollection(nestLocations.keySet());
    }

    @Override
    public EntryLocation getNestLocation(String name) {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return nestLocations.get(name);
    }

    @Override
    public EntryLocation getSourceLocation(String name) {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return sourceLocations.get(name);
    }

    @Override
    public File build(File dir, String name) throws NestException {
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
        nestFile = new File(dir, name);

        return new ZipNestBuilder().build(this);
    }

    @Override
    public String resolveSourcePath(EntryLocation sourceLocation) throws NestException {
        if(sourceLocation == null) {
            throw new IllegalArgumentException("sourceLocation is null");
        }
        return resolvePath(sourceLocation, sourceLocations, File.separator);
    }

    @Override
    public String resolveNestPath(EntryLocation nestLocation) throws NestException {
        if(nestLocation == null) {
            throw new IllegalArgumentException("nestLocation is null");
        }
        return resolvePath(nestLocation, nestLocations, ZipUtils.ENTRY_SEPARATOR);
    }

    private String resolvePath(EntryLocation location, Map<String, EntryLocation> locations, String separator) throws NestException {
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
        final String resolved = resolvePath(relativeToLocation, locations, separator);
        if(resolved == null) {
            return location.getPath();
        }
        if(location.getPath() == null) {
            return resolved;
        }
        return resolved + separator + location.getPath();
    }

    private EntryLocation assertSourceLocation(String name) {
        final EntryLocation location = sourceLocations.get(name);
        if(location == null) {
            throw new IllegalStateException("Source location not found: " + name);
        }
        return location;
    }

    private EntryLocation assertNestLocation(String name) {
        final EntryLocation location = nestLocations.get(name);
        if(location == null) {
            throw new IllegalStateException("Nest location not found: " + name);
        }
        return location;
    }

    private void addEntry(NestEntrySource entry) {
        switch(entries.size()) {
            case 0:
                entries = Collections.singletonList(entry);
                break;
            case 1:
                entries = new ArrayList<NestEntrySource>(entries);
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

    private void addExpandLocation(EntryLocation el) {
        switch(expandLocations.size()) {
            case 0:
                expandLocations = Collections.<String, EntryLocation>singletonMap(el.getName(), el);
                break;
            case 1:
                expandLocations = new HashMap<String, EntryLocation>(expandLocations);
            default:
                expandLocations.put(el.getName(), el);
        }
    }

    /**
     * Returns the last added entry.
     *
     * @return  last added entry
     */
    private NestEntrySource getLastEntry() {
        assert !entries.isEmpty() : "there are no entries";
        return entries.get(entries.size() - 1);
    }

    class EntryExpandToBuilderImpl extends DelegatingPackingNestBuilder implements EntryExpandToBuilder {

        @Override
        public NestBuildTask expandToLocation(String expandLocation) {
            // TODO Auto-generated method stub
            //return PackingNestBuilderImpl.this;
            throw new UnsupportedOperationException();
        }

        @Override
        public NestBuildTask expandToLocation(String expandLocation, String relativePath) {
            // TODO Auto-generated method stub
            //return PackingNestBuilderImpl.this;
            throw new UnsupportedOperationException();
        }
    }

    class EntryUnderBuilderImpl extends EntryExpandToBuilderImpl implements EntryUnderBuilder {

        @Override
        public EntryExpandToBuilder under(String nestPath) {
            if(nestPath == null) {
                throw new IllegalArgumentException("nestPath is null");
            }
            final NestEntrySource entrySource = getLastEntry();
            entrySource.getNestEntry().setNestLocation(EntryLocation.path(nestPath));
            return expandToBuilder;
        }

        @Override
        public EntryExpandToBuilder underLocation(String nestLocation) {
            if(nestLocation == null) {
                throw new IllegalArgumentException("nestLocation is null");
            }
            final EntryLocation location = assertNestLocation(nestLocation);
            final NestEntrySource entrySource = getLastEntry();
            entrySource.getNestEntry().setNestLocation(location);
            return expandToBuilder;
        }

        @Override
        public EntryExpandToBuilder underLocation(String nestLocation, String relativePath) {
            if(nestLocation == null) {
                throw new IllegalArgumentException("nestLocation is null");
            }
            if(relativePath == null) {
                throw new IllegalArgumentException("relativePath is null");
            }
            assertNestLocation(nestLocation);
            final NestEntrySource entrySource = getLastEntry();
            entrySource.getNestEntry().setNestLocation(EntryLocation.path(nestLocation, relativePath));
            return expandToBuilder;
        }
    }

    class DelegatingPackingNestBuilder implements NestBuildTask {

        @Override
        public NestBuildTask nameSourceLocation(String name) {
            return NestBuildTaskImpl.this.nameSourceLocation(name);
        }

        @Override
        public NestBuildTask nameSourceLocation(String name, String locationName, String path) {
            return NestBuildTaskImpl.this.nameSourceLocation(name, locationName, path);
        }

        @Override
        public NestBuildTask linkSourceLocation(String name, String path) {
            return NestBuildTaskImpl.this.linkSourceLocation(name, path);
        }

        @Override
        public NestBuildTask nameNestLocation(String name, String path) {
            return NestBuildTaskImpl.this.nameNestLocation(name, path);
        }

        @Override
        public NestBuildTask nameNestLocation(String name, String locationName, String path) {
            return NestBuildTaskImpl.this.nameNestLocation(name, locationName, path);
        }

        @Override
        public NestBuildTask nameExpandLocation(String name) {
            return NestBuildTaskImpl.this.nameExpandLocation(name);
        }

        @Override
        public NestBuildTask nameExpandLocation(String name, String locationName, String path) {
            return NestBuildTaskImpl.this.nameExpandLocation(name, locationName, path);
        }

        @Override
        public EntryUnderBuilder add(String srcPath) {
            return NestBuildTaskImpl.this.add(srcPath);
        }

        @Override
        public EntryUnderBuilder addLocation(String locationName, String relativePath) {
            return NestBuildTaskImpl.this.addLocation(locationName, relativePath);
        }

        @Override
        public EntryUnderBuilder addLocation(String srcLocationName) {
            return NestBuildTaskImpl.this.addLocation(srcLocationName);
        }

        @Override
        public File build(File dir, String name) throws NestException {
            return NestBuildTaskImpl.this.build(dir, name);
        }
    }
}
