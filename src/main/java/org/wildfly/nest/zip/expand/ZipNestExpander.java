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

package org.wildfly.nest.zip.expand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.wildfly.nest.NestException;
import org.wildfly.nest.expand.EntryExpandContext;
import org.wildfly.nest.expand.EntryExpander;
import org.wildfly.nest.expand.EntryExpanderChain;
import org.wildfly.nest.expand.NestExpandContext;
import org.wildfly.nest.expand.NestExpander;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ZipNestExpander implements NestExpander {

    public static ZipNestExpander init() {
        return new ZipNestExpander();
    }

    private final EntryExpanderChain<ZipEntry> chain;

    private ZipNestExpander() {
        chain = EntryExpanderChain.<ZipEntry>create().add(new ZipEntryExpander()).done();
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.unpack.NestExpander#expand(org.wildfly.nest.unpack.NestExpandContext)
     */
    @Override
    public void expand(NestExpandContext ctx) throws NestException {

        final File nestFile = ctx.getNestFile();
        assert nestFile != null : "nest file is null";

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(nestFile);
            final ZipEntryExpandContext entryCtx = new ZipEntryExpandContext(ctx, zipFile);

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                entryCtx.entry = entry;

                for(EntryExpander<ZipEntry> entryExpander : chain) {
                    entryExpander.process(entryCtx);
                }
            }
        } catch (IOException e) {
            throw new NestException("Failed to unpack " + nestFile.getAbsolutePath(), e);
        } finally {
            if(zipFile != null) {
                IoUtils.safeClose(zipFile);
            }
        }


    }

    private static final class ZipEntryExpandContext implements EntryExpandContext<ZipEntry> {

        final NestExpandContext ctx;
        final ZipFile zipFile;
        ZipEntry entry;

        ZipEntryExpandContext(NestExpandContext ctx, ZipFile zipFile) {
            assert ctx != null : "nest expand context is null";
            assert zipFile != null : "zip file is null";
            this.ctx = ctx;
            this.zipFile = zipFile;
        }

        @Override
        public NestExpandContext getNestContext() {
            return ctx;
        }

        @Override
        public ZipEntry getEntry() {
            return entry;
        }

        @Override
        public InputStream getEntryInputStream() throws NestException {
            try {
                return zipFile.getInputStream(entry);
            } catch (IOException e) {
                throw new NestException("Failed to open input stream for entry " + entry.getName(), e);
            }
        }
    }
}
