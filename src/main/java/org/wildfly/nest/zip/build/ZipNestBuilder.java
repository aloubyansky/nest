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

package org.wildfly.nest.zip.build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;
import org.wildfly.nest.build.EntryBuildContext;
import org.wildfly.nest.build.NestBuildContext;
import org.wildfly.nest.build.NestBuilder;
import org.wildfly.nest.build.NestEntrySource;
import org.wildfly.nest.common.EntryProcessorChain;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ZipNestBuilder implements NestBuilder {

    public static ZipNestBuilder init() {
        return new ZipNestBuilder();
    }

    private final EntryProcessorChain<ZipEntryWriter> chain;

    private ZipNestBuilder() {
        chain = EntryProcessorChain.<ZipEntryWriter>create().add(new ZipEntryWriter()).done();
    }

    @Override
    public File build(NestBuildContext ctx) throws NestException {

        final File nestFile = ctx.getNestFile();
        if(nestFile.exists()) {
            if(nestFile.isDirectory()) {
                throw new NestException("Nest file points a directory " + nestFile.getAbsolutePath());
            }
            nestFile.delete();
        }

        ZipOutputStream zos = null;
        try {
            final FileOutputStream fis = new FileOutputStream(nestFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fis));
            ZipEntryContext entryCtx = new ZipEntryContext(ctx, zos);

            for(NestEntrySource entry : ctx.getEntries()) {
                entryCtx.entry = entry;
                for(ZipEntryWriter writer : chain) {
                    writer.write(entryCtx);
                }
            }
        } catch (IOException e) {
            throw new NestException("Failed to create ZIP " + nestFile.getAbsolutePath(), e);
        } finally {
            IoUtils.safeClose(zos);
        }

        return nestFile;
    }

    class ZipEntryContext implements EntryBuildContext<ZipOutputStream> {

        private static final int SOURCE_LOCATION = 0;
        private static final int NEST_LOCATION = 1;

        private final NestBuildContext ctx;
        private final ZipOutputStream os;
        NestEntrySource entry;

        ZipEntryContext(NestBuildContext ctx, ZipOutputStream os) {
            assert ctx != null : "nest build context is null";
            assert os != null : "nest output stream is null";
            this.ctx = ctx;
            this.os = os;
        }

        @Override
        public NestBuildContext getNestBuildContext() {
            return ctx;
        }

        @Override
        public NestEntrySource getEntrySource() {
            return entry;
        }

        @Override
        public String getSourcePath() throws NestException {
            final String path = resolvePath(entry.getSourceLocation(), SOURCE_LOCATION);
            if(path == null) {
                throw new NestException("Failed to resolve source path for location " + entry.getSourceLocation());
            }
            return path;
        }

        @Override
        public String getNestPath() throws NestException {
            return resolvePath(entry.getNestEntry().getNestLocation(), NEST_LOCATION);
        }

        @Override
        public ZipOutputStream getEntryOutputStream() throws NestException {
            return os;
        }

        private String resolvePath(EntryLocation location, int locationType) throws NestException {
            if(location == EntryLocation.DEFAULT) {
                return null;
            }

            final String relativeToName = location.getRelativeTo();
            if(relativeToName == null) {
                return location.getPath();
            }

            final EntryLocation relativeToLocation = locationType == SOURCE_LOCATION ? ctx.getSourceLocation(relativeToName) : ctx.getNestLocation(relativeToName);
            if(relativeToLocation == null) {
                throw new NestException("Missing location definition for " + relativeToName);
            }
            final String resolved = resolvePath(relativeToLocation, locationType);
            if(resolved == null) {
                return location.getPath();
            }
            if(location.getPath() == null) {
                return resolved;
            }
            return resolved + (locationType == SOURCE_LOCATION ? File.separator : ZipUtils.ENTRY_SEPARATOR) + location.getPath();
        }
    }
}
