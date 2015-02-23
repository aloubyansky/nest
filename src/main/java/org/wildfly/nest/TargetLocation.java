package org.wildfly.nest;

import java.io.File;

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



/**
 * Represents information about where the item has to be unpacked to.
 *
 * @author Alexey Loubyansky
 */
public interface TargetLocation {

    /**
     * Returns the alias which to resolves to a path relative to which
     * the current target should be resolved.
     * If the relative-to alias was not specified, the method return null.
     *
     * @return  the alias of the location relative to which the current target
     *          should be resolved or null if the relative location was not
     *          specified
     */
    String getRelativeTo();

    /**
     * Alias for this location if provided.
     *
     * @return  alias for this location or null if none was provided
     */
    String getAlias();

    /**
     * The path returned by this method is resolved relative to the alias
     * returned from {@link #getRelativeTo()} unless the alias returned is null.
     * Otherwise, the returned file path will be considered as absolute.
     * The method may also return null if the path was not specified. In that case,
     * {@link #getRelativeTo()} must return a non-null value, which will be considered
     * the target location this instance represents.
     *
     * @return  file path this location represents or null if the path was not specified
     */
    File getPath();

    /**
     * Resolves to this location to the absolute file path.
     *
     * @param ctx  current context
     * @return  resolved file path this location respresents
     */
    File resolve(NestContext ctx);
}
