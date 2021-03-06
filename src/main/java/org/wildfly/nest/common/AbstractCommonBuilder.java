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

package org.wildfly.nest.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestContext;
import org.wildfly.nest.NestEntry;
import org.wildfly.nest.NestException;
import org.wildfly.nest.util.ZipUtils;



/**
 *
 * @author Alexey Loubyansky
 */
public abstract class AbstractCommonBuilder<T extends CommonBuilder<T>> implements CommonBuilder<T>, NestContext {

    private Map<String, EntryLocation> nestLocations = Collections.emptyMap();
    private Map<String, EntryLocation> expandLocations = Collections.emptyMap();

    private Map<String, NestEntry> entries = Collections.emptyMap();

    /**
     * Defines a new named location inside the package.
     *
     * @param name  new nest location name
     * @param path  path inside the package
     * @return  nest builder
     */
    @Override
    public T nameNestLocation(String name, String path) {
        addNestLocation(EntryLocation.name(name, path));
        return (T)this;
    }

    /**
     * Defines a new named location inside the package with the path relative
     * to another named nest location.
     *
     * @param name  new nest location name
     * @param nestLocationName  nest location relative to which the new nest
     *                          location will be resolved
     * @param path  path relative to the specified named nest location
     * @return  nest builder
     */
    @Override
    public T nameNestLocation(String name, String nestLocationName, String path) {
        addNestLocation(EntryLocation.name(name, nestLocationName, path));
        return (T)this;
    }

    @Override
    public LinkBuilder<T> linkNestLocation(String locationName) throws NestException {
        return new LinkLocationImpl(locationName);
    }

    @Override
    public LinkBuilder<T> linkNestPath(String nestLocationName, String nestRelativePath) throws NestException {
        if(nestLocationName == null) {
            throw new IllegalArgumentException("nestLocationName is null");
        }
        if(nestRelativePath == null) {
            throw new IllegalArgumentException("nestRelativePath is null");
        }
        final String locationName = '$' + nestLocationName + ZipUtils.ENTRY_SEPARATOR + nestRelativePath;
        nameNestLocation(locationName, nestLocationName, nestRelativePath);
        return linkNestLocation(locationName);
    }

    @Override
    public LinkBuilder<T> linkNestPath(String nestPath) throws NestException {
        if(nestPath == null) {
            throw new IllegalArgumentException("nestPath is null");
        }
        nameNestLocation(nestPath, nestPath);
        return linkNestLocation(nestPath);
    }

    /**
     * Defines a new named (not linked) expand location.
     *
     * @param name  expand location name
     * @return  nest builder
     */
    @Override
    public T nameExpandLocation(String name) {
        addExpandLocation(EntryLocation.name(name));
        return (T)this;
    }

    /**
     * Defines a new named expand location with the path relative to another
     * named expand location.
     *
     * @param name  new expand location name
     * @param expandLocationName  expand location relative to which the new
     *                            expand location should be resolved
     * @param path  path relative to the specified named expand location
     * @return
     */
    @Override
    public T nameExpandLocation(String name, String expandLocationName, String path) {
        addExpandLocation(EntryLocation.name(name, expandLocationName, path));
        return (T)this;
    }

    @Override
    public Collection<String> getNestLocationNames() {
        return Collections.unmodifiableSet(nestLocations.keySet());
    }

    @Override
    public EntryLocation getNestLocation(String name) {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return nestLocations.get(name);
    }

    protected NestEntry getNestEntry(String nestLocation) {
        return entries.get(nestLocation);
    }

    /** TODO
     * Links a named unpack location to the actual path.
     * If the unpack location name has been defined, the existing named unpack
     * location is linked to the path.
     * Otherwise, the method will define a new named unpack location and link it
     * to the path.
     *
     * @param name  unpack location name
     * @param path  the path
     * @return  nest builder
     *
    T linkUnpackLocation(String name, String path);*/

    public abstract class EntryExpandToBuilder extends AbstractCommonBuilder<T> {


        /** TODO
         * The actual path to which the previously added to the package entry
         * should be unpacked to.
         *
         * @param path  actual target path
         * @return  nest builder
         *
        T unpackTo(String path); */

        /**
         * Specifies a named expand location the previously added to
         * the package entry should be expanded to.
         *
         * @param expandLocationName  named expand location the previously
         *          added to the nest package entry should be expanded to
         * @return  nest builder
         */
        public T expandToLocation(String expandLocationName) {
            getLastEntry().setExpandLocation(EntryLocation.name(expandLocationName));
            return (T)AbstractCommonBuilder.this;
        }

        /**
         * Specifies a relative to the named expand location path
         * the previously added to the nest package entry should be expanded to.
         *
         * @param expandLocationName  named expand location relative to which
         *                            the target expand path should be resolved
         * @param relativePath  path relative to the specified named expand
         *                      location
         * @return  nest builder
         */
        public T expandToLocation(String expandLocationName, String relativePath) {
            getLastEntry().setExpandLocation(EntryLocation.name(expandLocationName, relativePath));
            return (T)AbstractCommonBuilder.this;
        }
    }

    protected Collection<EntryLocation> getNestLocations() {
        return Collections.unmodifiableCollection(nestLocations.values());
    }

    protected EntryLocation assertNestLocation(String name) throws NestException {
        final EntryLocation location = nestLocations.get(name);
        if(location == null) {
            throw new NestException("Nest location not found: " + name);
        }
        return location;
    }

    protected EntryLocation assertExpandLocation(String name) throws NestException {
        final EntryLocation location = expandLocations.get(name);
        if(location == null) {
            throw new NestException("Expand location not found: " + name);
        }
        return location;
    }

    protected void addNestLocation(EntryLocation el) {
        if(el.getName() == null) {
            throw new IllegalArgumentException("entry location is null");
        }
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

    protected void addExpandLocation(EntryLocation el) {
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

    protected void addEntry(NestEntry entry) {
        final String name = entry.getNestLocation().getName();
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        switch(entries.size()) {
            case 0:
                entries = Collections.singletonMap(name, entry);
                break;
            case 1:
                entries = new HashMap<String,NestEntry>(entries);
            default:
                entries.put(name, entry);
        }
    }

    /**
     * Returns the last added entry.
     *
     * @return  last added entry
     */
    protected NestEntry getLastEntry() {
        assert !entries.isEmpty() : "there are no entries";
        return entries.get(entries.size() - 1);
    }

    class LinkLocationImpl implements LinkBuilder<T> {

        final EntryLocation location;

        LinkLocationImpl(String locationName) throws NestException {
            if(locationName == null) {
                throw new IllegalArgumentException("name is null");
            }
            location = assertNestLocation(locationName);
        }

        @Override
        public T toPath(String path) throws NestException {

            if(path == null) {
                throw new IllegalArgumentException("path is null");
            }
            NestEntry entry = entries.get(location.getName());
            if(entry == null) {
                entry = NestEntry.create(location, EntryLocation.path(path));
                AbstractCommonBuilder.this.addEntry(entry);
            } else {
                entry.setExpandLocation(EntryLocation.path(path));
            }

            return (T)AbstractCommonBuilder.this;
        }

        @Override
        public T toPath(String locationName, String path) throws NestException {

            if(locationName == null) {
                throw new IllegalArgumentException("locationName is null");
            }
            if(path == null) {
                throw new IllegalArgumentException("path is null");
            }
            assertExpandLocation(locationName);
            NestEntry entry = entries.get(location.getName());
            if(entry == null) {
                entry = NestEntry.create(location, EntryLocation.path(locationName, path));
                AbstractCommonBuilder.this.addEntry(entry);
            } else {
                entry.setExpandLocation(EntryLocation.path(locationName, path));
            }

            return (T)AbstractCommonBuilder.this;
        }

        @Override
        public T toLocation(String name) throws NestException {

            if(name == null) {
                throw new IllegalArgumentException("name is null");
            }
            final EntryLocation expandLocation = assertExpandLocation(name);
            NestEntry entry = entries.get(location.getName());
            if(entry == null) {
                entry = NestEntry.create(location, expandLocation);
                AbstractCommonBuilder.this.addEntry(entry);
            } else {
                entry.setExpandLocation(expandLocation);
            }

            return (T)AbstractCommonBuilder.this;
        }
    }
}
