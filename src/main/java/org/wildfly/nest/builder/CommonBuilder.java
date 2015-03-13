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

package org.wildfly.nest.builder;

import org.wildfly.nest.NestException;

/**
 *
 * @author Alexey Loubyansky
 */
public interface CommonBuilder<T extends CommonBuilder<T>> {

    /**
     * Defines a new named location inside the package.
     *
     * @param name  new nest location name
     * @param path  path inside the package
     * @return  nest builder
     */
    T nameNestLocation(String name, String path);

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
    T nameNestLocation(String name, String nestLocationName, String path);

    /**
     * Links an existing named nest location to the expand path
     * relative to the base expand directory.
     *
     * @param nestLocationName  nest location name
     * @param expandPath  expand path
     * @return  nest builder
     */
    T linkNestLocation(String nestLocationName, String expandPath) throws NestException;

    /**
     * Links named nest location to the path relative to a named expand location.
     *
     * @param nestLocationName  nest location name
     * @param expandLocationName  expand location name relative to which
     *                            the expand path will be resolved
     * @param relativePath  path relative to the specified expand location
     * @return  nest builder
     * @throws NestException
     */
    T linkNestLocation(String nestLocationName, String expandLocationName, String relativePath) throws NestException;

    /**
     * Links named nest location to the named expand location.
     *
     * @param nestLocationName  nest location name
     * @param expandLocationName  unpack location name
     * @return  nest builder
     * @throws NestException
     */
    T linkNestToExpandLocation(String nestLocationName, String expandLocationName) throws NestException;

    /**
     * Links a nest path to the expand path relative to the base expand directory.
     * If the nest path has already been linked, the existing link will be replaced
     * with the new one.
     *
     * @param nestPath  path inside the nest relative to the root of the nest
     * @param expandPath  the expand path
     * @return  nest builder
     * @throws NestException
     */
    T linkNestPath(String nestPath, String expandPath) throws NestException;

    /**
     * Links a nest path relative to the specified named nest location to the expand path
     * relative to the base expand directory.
     * If the nest path has already been linked, the existing link will be replaced
     * with the new one.
     *
     * @param nestLocationName  named nest location relative to which the nest path
     *                          will be resolved
     * @param relativeNestPath  path relative to the specified named nest location
     * @param expandPath  the expand path
     * @return  nest builder
     * @throws NestException
     */
    T linkNestPath(String nestLocationName, String relativeNestPath, String expandPath) throws NestException;

    /**
     * Links a nest path relative to the specified named nest location to the expand path
     * relative to the specified named expand location.
     * If the nest path has already been linked, the existing link will be replaced
     * with the new one.
     *
     * @param nestLocationName  named nest location relative to which the nest path will be resolved
     * @param nestRelativePath  path relative to the specified named nest location
     * @param expandLocation  named expand path relative to which the expand path will be resolved
     * @param expandRelativePath  expand path relative to the specified named expand location
     * @return  nest builder
     * @throws NestException
     */
    T linkNestPath(String nestLocationName, String nestRelativePath, String expandLocation, String expandRelativePath) throws NestException;

    /**
     * Defines a new named (not linked) expand location.
     *
     * @param name  expand location name
     * @return  nest builder
     */
    T nameExpandLocation(String name);

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
    T nameExpandLocation(String name, String expandLocationName, String path);

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
}
