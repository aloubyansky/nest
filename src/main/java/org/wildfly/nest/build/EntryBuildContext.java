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

import java.io.OutputStream;

import org.wildfly.nest.NestException;

/**
 *
 * @author Alexey Loubyansky
 */
public interface EntryBuildContext<T extends OutputStream> {

    NestBuildContext getNestBuildContext();

    /**
     * Entry source path.
     *
     * @return  entry source path
     * @throws NestException
     */
    String getSourcePath() throws NestException;

    /**
     * The path under which this entry should be stored in the nest.
     *
     * @return  entry nest path
     * @throws NestException
     */
    String getNestPath() throws NestException;

    NestEntrySource getEntrySource();

    T getEntryOutputStream() throws NestException;
}
