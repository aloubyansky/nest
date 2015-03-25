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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.wildfly.nest.NestException;
import org.wildfly.nest.build.AbstractNestBuilder;
import org.wildfly.nest.build.NestBuildContext;
import org.wildfly.nest.build.NestEntrySource;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class ZipNestBuilder extends AbstractNestBuilder {

    private ZipOutputStream zipOut;

    private File currentEntrySrc;
    private ZipEntry currentZipEntry;

    @Override
    protected void prepareToBuild(NestBuildContext ctx) throws NestException {

        FileOutputStream fis;
        try {
            fis = new FileOutputStream(ctx.getNestFile());
        } catch (FileNotFoundException e) {
            throw new NestException("Failed to open " + ctx.getNestFile().getAbsolutePath(), e);
        }
        zipOut = new ZipOutputStream(new BufferedOutputStream(fis));
    }

    @Override
    protected void tidyUpAfterBuild(NestBuildContext ctx) throws NestException {

        IoUtils.safeClose(zipOut);
    }

    @Override
    protected void addNestAttachments(NestBuildContext ctx, byte[] bytes) throws NestException {
        final ZipEntry entry = new ZipEntry(ZipUtils.ROOT_ENTRY_NAME);
        entry.setExtra(bytes);
        try {
            zipOut.putNextEntry(entry);
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new NestException("Failed to add root entry", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.wildfly.nest.build.AbstractNestBuilder#buildEntry(org.wildfly.nest.build.NestEntrySource)
     *
    @Override
    protected void buildEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

        final String srcPath = ctx.resolveSourcePath(entry.getSourceLocation());
        final String nestPath = ctx.resolveNestPath(entry.getNestEntry().getNestLocation());
        try {
            addToZip(new File(srcPath), nestPath, zipOut);
        } catch (IOException e) {
            throw new NestException("Failed to ZIP entry " + srcPath + " to " + nestPath, e);
        }
    }

    private static void addToZip(File fileOrDir, String nestPath, ZipOutputStream zos) throws IOException {
        if (fileOrDir.isDirectory()) {
            final String dirName = nestPath == null ? fileOrDir.getName() + ZipUtils.ENTRY_SEPARATOR :
                nestPath + ZipUtils.ENTRY_SEPARATOR + fileOrDir.getName() + ZipUtils.ENTRY_SEPARATOR;
            addDirectoryToZip(fileOrDir, dirName, zos);
        } else {
            addFileToZip(fileOrDir, nestPath == null ? null : nestPath + ZipUtils.ENTRY_SEPARATOR, zos);
        }
    }*/

    @Override
    protected void beginEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

        final String srcPath = ctx.resolveSourcePath(entry.getSourceLocation());
        final String nestPath = ctx.resolveNestPath(entry.getNestEntry().getNestLocation());

        currentEntrySrc = new File(srcPath);

        final StringBuilder entryName = new StringBuilder();
        if (nestPath != null) {
            entryName.append(nestPath).append(ZipUtils.ENTRY_SEPARATOR);
        }
        entryName.append(currentEntrySrc.getName());

        if (currentEntrySrc.isDirectory()) {
            entryName.append(ZipUtils.ENTRY_SEPARATOR);
        }

        currentZipEntry = new ZipEntry(entryName.toString());
    }

    @Override
    protected void addEntryAttachments(NestBuildContext ctx, NestEntrySource entry, byte[] attachments) throws NestException {
        currentZipEntry.setExtra(attachments);
    }

    @Override
    protected void completeEntry(NestBuildContext ctx, NestEntrySource entry) throws NestException {

        try {
            zipOut.putNextEntry(currentZipEntry);

            if (currentZipEntry.isDirectory()) {
                zipOut.closeEntry();

                File[] children = currentEntrySrc.listFiles();
                if (children != null) {
                    for (File file : children) {
                        if (file.isDirectory()) {
                            addDirectoryToZip(file, currentZipEntry.getName() + file.getName() + ZipUtils.ENTRY_SEPARATOR);
                        } else {
                            addFileToZip(file, currentZipEntry.getName());
                        }
                    }
                }
            } else {
                BufferedInputStream bis = null;
                try {
                    final FileInputStream is = new FileInputStream(currentEntrySrc);
                    bis = new BufferedInputStream(is);
                    IoUtils.copyStream(bis, zipOut);
                    zipOut.closeEntry();
                } finally {
                    IoUtils.safeClose(bis);
                }
            }
        } catch (IOException e) {
            throw new NestException("Failed to add " + ctx.resolveSourcePath(entry.getSourceLocation()) + " as " + currentZipEntry.getName(), e);
        } finally {
            currentZipEntry = null;
            currentEntrySrc = null;
        }
    }

    private void addDirectoryToZip(File dir, String dirName) throws IOException {

        final ZipEntry dirEntry = new ZipEntry(dirName);
        zipOut.putNextEntry(dirEntry);
        zipOut.closeEntry();

        File[] children = dir.listFiles();
        if (children != null) {
            for (File file : children) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, dirName + file.getName() + ZipUtils.ENTRY_SEPARATOR);
                } else {
                    addFileToZip(file, dirName);
                }
            }
        }
    }

    private void addFileToZip(File file, String nestPath) throws IOException {
        final FileInputStream is = new FileInputStream(file);
        try {
            final String entryName = nestPath == null ? file.getName() : nestPath + file.getName();
            zipOut.putNextEntry(new ZipEntry(entryName));

            final BufferedInputStream bis = new BufferedInputStream(is);
            try {
                IoUtils.copyStream(bis, zipOut);
            } finally {
                IoUtils.safeClose(bis);
            }

            zipOut.closeEntry();
        } finally {
            IoUtils.safeClose(is);
        }
    }
}
