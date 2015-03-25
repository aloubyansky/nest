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
import org.wildfly.nest.zip.EntryAttachments;
import org.wildfly.nest.zip.NestAttachments;

/**
 *
 * @author Alexey Loubyansky
 */
public abstract class AbstractNestBuilder implements NestBuilder {

    public File build(NestBuildContext ctx) throws NestException {
        final File nestFile = ctx.getNestFile();
        if (nestFile.exists()) {
            if (nestFile.isDirectory()) {
                throw new NestException("Nest file points a directory " + nestFile.getAbsolutePath());
            }
            nestFile.delete();
        }

        try {
            prepareToBuild(ctx);
            final byte[] bytes = NestAttachments.DEFAULT.write(ctx);
            if(bytes != null) {
                addNestAttachments(ctx, bytes);
            }
            for (NestEntrySource entry : ctx.getEntries()) {
                buildEntry(ctx, entry);
            }
            return nestFile;
        } finally {
            tidyUpAfterBuild(ctx);
        }
    }

    protected void prepareToBuild(NestBuildContext ctx) throws NestException {
    }

    protected void tidyUpAfterBuild(NestBuildContext ctx) throws NestException {
    }

    protected void addNestAttachments(NestBuildContext ctx, byte[] attachments) throws NestException {
    }

    protected void buildEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

        beginEntry(ctx, entry);

        final byte[] bytes = EntryAttachments.DEFAULT.write(ctx, entry);
        if (bytes != null) {
            addEntryAttachments(ctx, entry, bytes);
        }

        completeEntry(ctx, entry);
    }

    protected void beginEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

    }

    protected void addEntryAttachments(NestBuildContext ctx, NestEntrySource entry, byte[] attachments) throws NestException {

    }

    protected void completeEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

    }
}
