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
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.wildfly.nest.NestException;
import org.wildfly.nest.expand.AbstractNestExpander;
import org.wildfly.nest.expand.NestExpandContext;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 * @author Alexey Loubyansky
 *
 */
public class ZipNestExpander extends AbstractNestExpander<ZipEntry> {

    private ZipFile zipFile;

    @Override
    protected void prepareToExpand(NestExpandContext ctx) throws NestException {
        try {
            zipFile = new ZipFile(ctx.getNestFile());
        } catch (IOException e) {
            throw new NestException("Failed to open nest file " + ctx.getNestFile().getAbsolutePath(), e);
        }
    }

    @Override
    protected void tidyUpAfterExpand(NestExpandContext ctx) throws NestException {
        IoUtils.safeClose(zipFile);
    }

    @Override
    protected byte[] getNestAttachments(NestExpandContext ctx) throws NestException {
        final ZipEntry root = zipFile.getEntry(ZipUtils.ROOT_ENTRY_NAME);
        return root == null ? null : root.getExtra();
    }

    @Override
    protected Iterable<ZipEntry> getEntries() throws NestException {
        return new Iterable<ZipEntry>(){
            @Override
            public Iterator<ZipEntry> iterator() {
                return new Iterator<ZipEntry>() {

                    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    boolean checkForNestRoot = zipFile.getEntry(ZipUtils.ROOT_ENTRY_NAME) != null;
                    ZipEntry next;

                    @Override
                    public boolean hasNext() {
                        if(checkForNestRoot) {
                            if(entries.hasMoreElements()) {
                                next = entries.nextElement();
                                if(ZipUtils.ROOT_ENTRY_NAME.equals(next.getName())) {
                                    checkForNestRoot = false;
                                    next = null;
                                }
                            }
                        }
                        return entries.hasMoreElements();
                    }

                    @Override
                    public ZipEntry next() {
                        if(next != null) {
                            final ZipEntry tmp = next;
                            next = null;
                            hasNext();
                            return tmp;
                        }
                        return entries.nextElement();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    protected void expandEntry(NestExpandContext ctx, ZipEntry entry) throws NestException {

        final String nestPath = entry.getName();
        final File expandPath = ctx.resolveExpandPath(nestPath);
        //System.out.println("expanding " + nestPath + " to " + expandPath.getAbsolutePath());
        if (entry.isDirectory()) {
            expandPath.mkdirs();
            return;
        }

        if (!expandPath.getParentFile().exists()) {
            expandPath.getParentFile().mkdirs();
        }
        InputStream eis = null;
        try {
            eis = zipFile.getInputStream(entry);
            IoUtils.copy(eis, expandPath);
        } catch (IOException e) {
            throw new NestException("Failed to expand entry " + entry.getName(), e);
        } finally {
            IoUtils.safeClose(eis);
        }

    }
}
