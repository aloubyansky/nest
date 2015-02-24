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


/**
 * @author Alexey Loubyansky
 *
 */
public interface PackingNestBuilder {

    class FACTORY {
        public static PackingNestBuilder create() {
            return new PackingNestBuilderImpl();
        }
    }

    PackingNestBuilder defineSourceAlias(String name, String path);

    PackingNestBuilder defineSourceAlias(String name, String relativeToAlias, String path);

    PackingNestBuilder defineUnderAlias(String name, String path);

    PackingNestBuilder defineUnderAlias(String name, String relativeToAlias, String path);

    PackingNestBuilder defineUnpackToAlias(String name);

    PackingNestBuilder defineUnpackToAlias(String name, String relativeToAlias, String path);

    EntryUnderBuilder add(String srcPath);

    EntryUnderBuilder add(String srcAlias, String relativePath);

    public interface EntryUnpackToBuilder extends PackingNestBuilder {

        PackingNestBuilder unpackTo(String targetAlias);

        PackingNestBuilder unpackTo(String targetAlias, String relativePath);
    }

    public interface EntryUnderBuilder extends EntryUnpackToBuilder {

        EntryUnpackToBuilder under(String nestPath);

        EntryUnpackToBuilder underAlias(String nestAlias);

        EntryUnpackToBuilder under(String nestAlias, String relativePath);
    }

}
