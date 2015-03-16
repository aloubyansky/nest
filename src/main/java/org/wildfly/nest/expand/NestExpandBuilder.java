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

import org.wildfly.nest.NestException;
import org.wildfly.nest.builder.CommonBuilder;

/**
 *
 * @author Alexey Loubyansky
 */
public interface NestExpandBuilder extends CommonBuilder<NestExpandBuilder> {

    class FACTORY {
        public static NestExpandBuilder create(File nestFile) {
            return new NestExpandBuilderImpl(nestFile);
        }
    }

    /**
     * Links a named expand location to the expand path relative to the base expand directory.
     * If the expand location with the given name has not been defined or
     * the location has already been linked, the method will throw an exception.
     *
     * @param expandLocationName  expand location name to link
     * @param expandPath  the path to link to
     * @return  nest builder
     */
    NestExpandBuilder linkExpandLocation(String expandLocationName, String expandPath) throws NestException;

    /**
     * Links a named expand location to an expand path relative to another expand location.
     * If the expand location with the given name has not been defined or
     * the location has already been linked, the method will throw an exception.
     *
     * @param expandLocationName  expand location to link
     * @param relativeToLocation  expand location relative to which the expand path will be resolved
     * @param path  expand path relative to the specified expand location name
     * @return  nest builder
     * @throws NestException
     */
    NestExpandBuilder linkExpandLocation(String expandLocationName, String relativeToLocation, String path) throws NestException;

    /**
     * Expands the nest package into the specified directory.
     *
     * @param expandBaseDir  target expand directory
     * @throws NestException
     */
    void expand(File expandBaseDir) throws NestException;
}
