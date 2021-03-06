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

package org.wildfly.nest;

import java.io.File;

import org.wildfly.nest.build.NestBuildTask;
import org.wildfly.nest.expand.NestExpandTask;

/**
 *
 * @author Alexey Loubyansky
 */
public class Nest {

    /**
     * Returns a new instance of a builder which can be used to build
     * a new nest.
     *
     * @return  builder to build a nest
     */
    public static NestBuildTask create() {
        return NestBuildTask.FACTORY.create();
    }

    /**
     * Returns a new instance of a builder which can be used to expand
     * an existing nest.
     *
     * @param nestFile  absolute path to the existing nest
     * @return  builder to expand the nest
     */
    public static NestExpandTask open(File nestFile) {
        return NestExpandTask.FACTORY.create(nestFile);
    }
}
