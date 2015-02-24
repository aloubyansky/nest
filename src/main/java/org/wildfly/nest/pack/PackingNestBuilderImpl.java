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

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineSourceAlias(java.lang.String, java.lang.String)
     */
    @Override
    public PackingNestBuilder defineSourceAlias(String name, String path) {
        addSourceLocation(EntryLocation.alias(name, path));
        return this;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineSourceAlias(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PackingNestBuilder defineSourceAlias(String name, String relativeToAlias, String path) {
        addSourceLocation(EntryLocation.alias(name, relativeToAlias, path));
        return this;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineUnderAlias(java.lang.String, java.lang.String)
     */
    @Override
    public PackingNestBuilder defineUnderAlias(String name, String path) {
        addNestLocation(EntryLocation.alias(name, path));
        return this;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineUnderAlias(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PackingNestBuilder defineUnderAlias(String name, String relativeToAlias, String path) {
        addNestLocation(EntryLocation.alias(name, relativeToAlias, path));
        return this;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineUnpackToAlias(java.lang.String)
     */
    @Override
    public PackingNestBuilder defineUnpackToAlias(String name) {
        addTargetLocation(EntryLocation.alias(name));
        return this;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.Nest.NestBuilder#defineUnpackToAlias(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PackingNestBuilder defineUnpackToAlias(String name, String relativeToAlias, String path) {
        addTargetLocation(EntryLocation.alias(name, relativeToAlias, path));
        return this;
    }

    @Override
    public EntryUnderBuilder add(String srcPath) {
        // TODO Auto-generated method stub
        return underBuilder;
    }

    @Override
    public EntryUnderBuilder add(String srcAlias, String relativePath) {
        // TODO Auto-generated method stub
        return underBuilder;
    }

    private void addSourceLocation(EntryLocation el) {
        switch(sourceLocations.size()) {
            case 0:
                sourceLocations = Collections.<String, EntryLocation>singletonMap(el.getAlias(), el);
                break;
            case 1:
                sourceLocations = new HashMap<String, EntryLocation>(sourceLocations);
            default:
                sourceLocations.put(el.getAlias(), el);
        }
    }

    private void addNestLocation(EntryLocation el) {
        switch(nestLocations.size()) {
            case 0:
                nestLocations = Collections.<String, EntryLocation>singletonMap(el.getAlias(), el);
                break;
            case 1:
                nestLocations = new HashMap<String, EntryLocation>(nestLocations);
            default:
                nestLocations.put(el.getAlias(), el);
        }
    }

    private void addTargetLocation(EntryLocation el) {
        switch(targetLocations.size()) {
            case 0:
                targetLocations = Collections.<String, EntryLocation>singletonMap(el.getAlias(), el);
                break;
            case 1:
                targetLocations = new HashMap<String, EntryLocation>(targetLocations);
            default:
                targetLocations.put(el.getAlias(), el);
        }
    }

    class EntryUnpackToBuilderImpl extends DelegatingPackingNestBuilder implements EntryUnpackToBuilder {

        @Override
        public PackingNestBuilder unpackTo(String targetAlias) {
            // TODO Auto-generated method stub
            return PackingNestBuilderImpl.this;
        }

        @Override
        public PackingNestBuilder unpackTo(String targetAlias, String relativePath) {
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
        public EntryUnpackToBuilder underAlias(String nestAlias) {
            // TODO Auto-generated method stub
            return unpackToBuilder;
        }

        @Override
        public EntryUnpackToBuilder under(String nestAlias, String relativePath) {
            // TODO Auto-generated method stub
            return unpackToBuilder;
        }
    }

    class DelegatingPackingNestBuilder implements PackingNestBuilder {

        @Override
        public PackingNestBuilder defineSourceAlias(String name, String path) {
            return defineSourceAlias(name, path);
        }

        @Override
        public PackingNestBuilder defineSourceAlias(String name, String relativeToAlias, String path) {
            return defineSourceAlias(name, relativeToAlias, path);
        }

        @Override
        public PackingNestBuilder defineUnderAlias(String name, String path) {
            return defineUnderAlias(name, path);
        }

        @Override
        public PackingNestBuilder defineUnderAlias(String name, String relativeToAlias, String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PackingNestBuilder defineUnpackToAlias(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PackingNestBuilder defineUnpackToAlias(String name, String relativeToAlias, String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public EntryUnderBuilder add(String srcPath) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public EntryUnderBuilder add(String srcAlias, String relativePath) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
