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

package org.wildfly.nest.unpack;

import java.io.File;
import java.io.IOException;

import org.wildfly.nest.NestException;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 *
 */
public class UnpackingNestBuilderImpl implements UnpackingNestBuilder {

    private final File nestFile;

    UnpackingNestBuilderImpl(File nestFile) {
        if(nestFile == null) {
            throw new IllegalArgumentException("nestFile is null");
        }
        if(!nestFile.exists()) {
            throw new IllegalArgumentException("File doesn't exist " + nestFile.getAbsolutePath());
        }
        if(!nestFile.isFile()) {
            throw new IllegalArgumentException(nestFile.getAbsolutePath() + " is not a file.");
        }
        this.nestFile = nestFile;
    }

    @Override
    public void unpack(File dir) throws NestException {

        if(dir == null) {
            throw new IllegalArgumentException("dir is null");
        }

        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                throw new NestException("Failed to create directory " + dir.getAbsolutePath());
            }
        } else if(!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getAbsolutePath() + " is not a directory");
        }

        try {
            ZipUtils.unzip(nestFile, dir);
        } catch (IOException e) {
            throw new NestException("Failed to unpack " + nestFile.getAbsolutePath() + " to " + dir.getAbsolutePath(), e);
        }
    }

    private class DelegatingUnpackingNestBuilder implements UnpackingNestBuilder {

        @Override
        public void unpack(File dir) throws NestException {
            UnpackingNestBuilderImpl.this.unpack(dir);
        }
    }
}
