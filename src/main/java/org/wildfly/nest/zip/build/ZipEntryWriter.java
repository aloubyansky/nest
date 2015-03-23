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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.wildfly.nest.NestException;
import org.wildfly.nest.build.EntryBuildContext;
import org.wildfly.nest.build.EntryWriter;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ZipEntryWriter implements EntryWriter<ZipOutputStream> {

    @Override
    public void write(EntryBuildContext<ZipOutputStream> ctx) throws NestException {

        final String srcPath = ctx.getSourcePath();
        final String nestPath = ctx.getNestPath();
        try {
            addToZip(new File(srcPath), nestPath, ctx.getEntryOutputStream());
        } catch (IOException e) {
            throw new NestException("Failed to ZIP entry " + srcPath + " to " + nestPath, e);
        }
    }

    /**
     * Adds an entry to the ZIP at the specified path.
     * If the path is null, the entry will added at the root of the ZIP.
     *
     * @param fileOrDir  file or directory
     * @param path  path relative to the root of the ZIP
     * @param zos  target ZIP
     * @throws IOException
     */
    private static void addToZip(File fileOrDir, String path, ZipOutputStream zos) throws IOException {
        if(fileOrDir.isDirectory()) {
            final String dirName = path == null ? fileOrDir.getName() : path + ZipUtils.ENTRY_SEPARATOR + fileOrDir.getName();
            addDirectoryToZip(fileOrDir, dirName, zos);
        } else {
            addFileToZip(fileOrDir, path, zos);
        }
    }

    private static void addDirectoryToZip(File dir, String dirName, ZipOutputStream zos) throws IOException {

        final ZipEntry dirEntry = new ZipEntry(dirName + ZipUtils.ENTRY_SEPARATOR);
        zos.putNextEntry(dirEntry);
        zos.closeEntry();

        File[] children = dir.listFiles();
        if (children != null) {
            for (File file : children) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, dirName + ZipUtils.ENTRY_SEPARATOR + file.getName(), zos);
                } else {
                    addFileToZip(file, dirName, zos);
                }
            }
        }
    }

    private static void addFileToZip(File file, String parent, ZipOutputStream zos) throws IOException {
        final FileInputStream is = new FileInputStream(file);
        try {
            final String entryName = parent == null ? file.getName() : parent + ZipUtils.ENTRY_SEPARATOR + file.getName();
            zos.putNextEntry(new ZipEntry(entryName));

            final BufferedInputStream bis = new BufferedInputStream(is);
            try {
                IoUtils.copyStream(bis, zos);
            } finally {
                IoUtils.safeClose(bis);
            }

            zos.closeEntry();
        } finally {
            IoUtils.safeClose(is);
        }
    }
}
