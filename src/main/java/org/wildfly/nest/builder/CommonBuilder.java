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
     * Returns a link builder for the named location.
     * The named location must be defined. Otherwise, an exception will be thrown.
     *
     * @param locationName  named nest location
     * @return  link builder
     * @throws NestException
     */
    LinkBuilder<T> linkNestLocation(String locationName) throws NestException;

    LinkBuilder<T> linkNestPath(String nestLocationName, String nestRelativePath) throws NestException;

    LinkBuilder<T> linkNestPath(String nestPath) throws NestException;

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

    interface LinkBuilder<T> {

        T toPath(String path) throws NestException;

        T toPath(String locationName, String path) throws NestException;

        T toLocation(String name) throws NestException;
    }
}
