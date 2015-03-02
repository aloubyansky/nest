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

package org.wildfly.nest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Brian Stansberry
 */
public class ZipUtils {

    private static final String ENTRY_SEPARATOR = "/";

    public static void zip(File sourceDir, File zipFile) {
        try {
            final FileOutputStream os = new FileOutputStream(zipFile);
            try {
                final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
                try {
                    for (final File file : sourceDir.listFiles()) {
                        if (file.isDirectory()) {
                            addDirectoryToZip(file, file.getName(), zos);
                        } else {
                            addFileToZip(file, null, zos);
                        }
                    }
                } finally {
                    IoUtils.safeClose(zos);
                }
            } finally {
                IoUtils.safeClose(os);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to zip " + zipFile, e);
        }
    }

    /**
     * Adds an entry at the root of the ZIP.
     *
     * @param fileOrDir  file or directory
     * @param zos  target zip
     * @throws IOException
     */
    public static void addToZip(File fileOrDir, ZipOutputStream zos) throws IOException {
        if(fileOrDir.isDirectory()) {
            addDirectoryToZip(fileOrDir, fileOrDir.getName(), zos);
        } else {
            addFileToZip(fileOrDir, null, zos);
        }
    }

    private static void addDirectoryToZip(File dir, String dirName, ZipOutputStream zos) throws IOException {

        final ZipEntry dirEntry = new ZipEntry(dirName + ENTRY_SEPARATOR);
        zos.putNextEntry(dirEntry);
        zos.closeEntry();

        File[] children = dir.listFiles();
        if (children != null) {
            for (File file : children) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, dirName + ENTRY_SEPARATOR + file.getName(), zos);
                } else {
                    addFileToZip(file, dirName, zos);
                }
            }
        }
    }

    private static void addFileToZip(File file, String parent, ZipOutputStream zos) throws IOException {
        final FileInputStream is = new FileInputStream(file);
        try {
            final String entryName = parent == null ? file.getName() : parent + ENTRY_SEPARATOR + file.getName();
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

    /**
     * unpack...
     *
     * @param zip  the zip
     * @param targetDir  the directory to store the content
     * @throws IOException
     */
    public static void unzip(final File zip, final File targetDir) throws IOException {
        final ZipFile zipFile = new ZipFile(zip);
        try {
            unzip(zipFile, targetDir);
        } finally {
            IoUtils.safeClose(zipFile);
        }
    }

    /**
     * unpack...
     *
     * @param zip the zip
     * @param patchDir the patch dir
     * @throws IOException
     */
    private static void unzip(final ZipFile zip, final File patchDir) throws IOException {
        final Enumeration<? extends ZipEntry> entries = zip.entries();
        while(entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            final String name = entry.getName();
            final File current = new File(patchDir, name);
            if(entry.isDirectory()) {
                continue;
            } else {
                if(! current.getParentFile().exists()) {
                    current.getParentFile().mkdirs();
                }
                final InputStream eis = zip.getInputStream(entry);
                try {
                    IoUtils.copy(eis, current);
                } finally {
                    IoUtils.safeClose(eis);
                }
            }
        }
    }
}
