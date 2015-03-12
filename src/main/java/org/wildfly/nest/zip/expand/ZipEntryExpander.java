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
import java.util.zip.ZipEntry;

import org.wildfly.nest.NestException;
import org.wildfly.nest.expand.EntryExpandContext;
import org.wildfly.nest.expand.EntryExpander;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ZipEntryExpander implements EntryExpander<ZipEntry> {

    @Override
    public void process(EntryExpandContext<ZipEntry> ctx) throws NestException {

        final ZipEntry entry = ctx.getEntry();
        assert entry != null : "entry is null";

        final String nestPath = entry.getName();
        final File expandPath = ctx.getNestContext().resolveExpandPath(nestPath);
        //System.out.println("expanding " + nestPath + " to " + expandPath.getAbsolutePath());
        if (entry.isDirectory()) {
            expandPath.mkdirs();
            return;
        }

        if (!expandPath.getParentFile().exists()) {
            expandPath.getParentFile().mkdirs();
        }
        final InputStream eis = ctx.getEntryInputStream();
        try {
            IoUtils.copy(eis, expandPath);
        } catch (IOException e) {
            throw new NestException("Failed to expand entry " + entry.getName(), e);
        } finally {
            IoUtils.safeClose(eis);
        }
    }
}
