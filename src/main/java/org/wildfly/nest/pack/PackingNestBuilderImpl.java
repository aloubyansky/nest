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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wildfly.nest.EntryLocation;

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
        // TODO Auto-generated method stub
        return underBuilder;
    }

    @Override
    public EntryUnderBuilder addLocation(String srcLocation, String relativePath) {
        // TODO Auto-generated method stub
        return underBuilder;
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
            return nameSourceLocation(name);
        }

        @Override
        public PackingNestBuilder nameSourceLocation(String name, String locationName, String path) {
            return nameSourceLocation(name, locationName, path);
        }

        @Override
        public PackingNestBuilder linkSourceLocation(String name, String path) {
            return linkSourceLocation(name, path);
        }

        @Override
        public PackingNestBuilder nameNestLocation(String name, String path) {
            return nameNestLocation(name, path);
        }

        @Override
        public PackingNestBuilder nameNestLocation(String name, String locationName, String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PackingNestBuilder nameUnpackLocation(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PackingNestBuilder nameUnpackLocation(String name, String locationName, String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public EntryUnderBuilder add(String srcPath) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public EntryUnderBuilder addLocation(String locationName, String relativePath) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
