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
import java.util.List;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestContext;
import org.wildfly.nest.NestException;

/**
 *
 * @author Alexey Loubyansky
 */
public interface NestBuildContext extends NestContext {

    EntryLocation getSourceLocation(String name);

    String resolveSourcePath(EntryLocation location) throws NestException;

    String resolveNestPath(EntryLocation location) throws NestException;

    List<NestEntrySource> getEntries();

    /**
     * Target file to save the built nest to.
     *
     * @return  target file to save the built nest to
     */
    File getNestFile();
}
