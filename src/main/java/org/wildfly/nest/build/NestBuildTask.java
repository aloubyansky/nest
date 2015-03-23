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

import org.wildfly.nest.NestException;


/**
 *
 * @author Alexey Loubyansky
 */
public interface NestBuildTask {

    class FACTORY {
        public static NestBuildTask create() {
            return new NestBuildTaskImpl();
        }
    }

    /**
     * Defines a new (not linked) source location name.
     *
     * @param name  new source location name
     * @return  nest builder
     */
    NestBuildTask nameSourceLocation(String name);

    /**
     * Defines a new source location name with the path relative to another
     * named source location.
     *
     * @param name  new source location name
     * @param sourceLocationName  named source location relative to which new
     *                            source location will be resolved
     * @param path  path relative to the specified named source location
     * @return  nest builder
     */
    NestBuildTask nameSourceLocation(String name, String sourceLocationName, String path);

    /**
     * Links a named source location to the actual path.
     * If the source location name has been defined, the defined source
     * location will be linked to the path.
     * Otherwise, the method will define a new named source location and link
     * it to the path.
     *
     * @param name  source location name
     * @param path  actual source path
     * @return  nest builder
     */
    NestBuildTask linkSourceLocation(String name, String path);

    /**
     * Defines a new named location inside the package.
     *
     * @param name  new nest location name
     * @param path  path inside the package
     * @return  nest builder
     */
    NestBuildTask nameNestLocation(String name, String path);

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
    NestBuildTask nameNestLocation(String name, String nestLocationName, String path);

    /**
     * Defines a new named (not linked) expand location.
     *
     * @param name  expand location name
     * @return  nest builder
     */
    NestBuildTask nameExpandLocation(String name);

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
    NestBuildTask nameExpandLocation(String name, String expandLocationName, String path);

    /**
     * Adds the content at the specified actual path to the package.
     *
     * @param srcPath  actual content path
     * @return  nest builder
     */
    EntryUnderBuilder add(String srcPath);

    /**
     * Adds the content located at the specified named source location.
     *
     * @param srcLocationName  named source location
     * @return  nest builder
     */
    EntryUnderBuilder addLocation(String srcLocationName);

    /**
     * Adds the content located at the path relative to the specified named
     * source location.
     *
     * @param srcLocationName  named source location relative to which the path
     *                         should be resolved
     * @param relativePath  path relative to the specified named source
     *                      location
     * @return  nest builder
     */
    EntryUnderBuilder addLocation(String srcLocationName, String relativePath);

    /**
     * Creates a package with the content added to the nest.
     * If a file already exists at the requested location,
     * the file will be replaced with the newly created one.
     *
     * @param dir  directory where to store the package
     * @param name  name under which the package should be stored
     * @return  created package file
     * @throws NestException  in case packing failed
     */
    File build(File dir, String name) throws NestException;

    public interface EntryExpandToBuilder extends NestBuildTask {

        /**
         * Specifies a named expand location the previously added to
         * the package entry should be expanded to.
         *
         * @param namedExpandLocation  named expand location the previously
         *          added to the package entry should be expanded to
         * @return  nest builder
         */
        NestBuildTask expandToLocation(String namedExpandLocation);

        /**
         * Specifies a relative to the named expand location path
         * the previously added to the package entry should be expanded to.
         *
         * @param namedExpandLocation  named expand location relative to which
         *                             the target expand path should be resolved
         * @param relativePath  path relative to the specified named expand
         *                      location
         * @return  nest builder
         */
        NestBuildTask expandToLocation(String namedExpandLocation, String relativePath);
    }

    public interface EntryUnderBuilder extends EntryExpandToBuilder {

        /**
         * Specifies a path relative to the root of the package
         * the previously added to the package entry should be stored at.
         *
         * @param nestPath  path relative to the root of the package
         * @return  nest builder
         */
        EntryExpandToBuilder under(String nestPath);

        /**
         * Specifies a named nest location at which the previously added to the
         * package entry should be stored.
         *
         * @param nestLocation  named nest location
         * @return  nest builder
         */
        EntryExpandToBuilder underLocation(String nestLocation);

        /**
         * Specifies a path relative to the named nest location at which
         * the previously added to the package entry should be stored.
         *
         * @param nestLocation  named location relative to which the path
         *                      should be resolved
         * @param relativePath  path relative to the specified named nest
         *                      location
         * @return  nest builder
         */
        EntryExpandToBuilder underLocation(String nestLocation, String relativePath);
    }
}
