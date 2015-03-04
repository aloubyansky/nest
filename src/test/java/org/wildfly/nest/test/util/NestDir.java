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

package org.wildfly.nest.test.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.wildfly.nest.util.HashUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class NestDir {

    /**
     * Creates a root dir.
     *
     * @return
     */
    public static NestDir root() {
        return create("nest_root");
    }

    /**
     * Creates a new dir for the given name
     *
     * @param name
     * @return
     */
    public static NestDir create(String name) {
        return create(null, name);
    }

    /**
     * Creates a new dir for the given name
     *
     * @param parent
     * @param name
     * @return
     */
    public static NestDir create(NestDir parent, String name) {
        return new NestDir(parent, name);
    }

    /**
     * Creates a NestDir tree equivalent to the passed in dir.
     *
     * @param dir
     * @return
     */
    public static NestDir from(File dir) {
        return from(dir, dir.getName());
    }

    public static NestDir from(File dir, String asName) {
        return from(dir, asName, null);
    }

    public static NestDir from(NestDir parent, File dir) {
        return from(null, dir, null);
    }

    public static NestDir from(File dir, FileFilter filter) {
        return from(dir, dir.getName(), filter);
    }

    public static NestDir from(File dir, String asName, FileFilter filter) {
        return from(null, dir, asName, filter);
    }

    public static NestDir from(NestDir parent, File dir, FileFilter filter) {
        return from(parent, dir, dir.getName(), null);
    }

    public static NestDir from(NestDir parent, File dir, String asName, FileFilter filter) {

        if (dir == null || !dir.isDirectory()) {
            throw new IllegalArgumentException("dir is not a directory");
        }
        final NestDir nestDir = new NestDir(parent, asName);
        final File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) {
                if (filter == null || filter != null && filter.accept(f)) {
                    if (f.isDirectory()) {
                        from(nestDir, f, f.getName(), filter);
                    } else {
                        NestFile.create(nestDir, f);
                    }
                }
            }
        }
        return nestDir;
    }

    private final NestDir parent;
    private final String name;
    private Map<String, NestDir> dirs = Collections.emptyMap();
    private Map<String, NestFile> files = Collections.emptyMap();

    private NestDir(String name) {
        this(null, name);
    }

    private NestDir(NestDir parent, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        this.name = name;
        this.parent = parent;
        if(parent != null) {
            parent.addDir(this);
        }
    }

    /**
     * Directory name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Creates a new child directory given the path segments
     * and returns it.
     *
     * @param segments
     * @return
     */
    public NestDir newDir(String... segments) {
        if(segments == null) {
            throw new IllegalArgumentException("segments is null");
        }
        if(segments.length == 0) {
            throw new IllegalArgumentException("segments is empty");
        }
        NestDir child = new NestDir(this, segments[0]);
        for(int i = 1; i < segments.length; ++i) {
            child = child.newDir(segments[i]);
        }
        return child;
    }

    NestDir addDir(NestDir child) {
        if (child == null) {
            throw new IllegalArgumentException("child is null");
        }
        switch (dirs.size()) {
            case 0:
                dirs = Collections.singletonMap(child.getName(), child);
                break;
            case 1:
                dirs = new HashMap<String, NestDir>(dirs);
            default:
                dirs.put(child.getName(), child);
        }
        return this;
    }

    /**
     * Adds a file or directory to this directory
     * and returns this directory.
     *
     * @param f
     * @return
     */
    public NestDir add(File f) {
        if(f == null) {
            throw new IllegalArgumentException("file is null");
        }
        return add(f, f.getName());
    }

    /**
     * Adds a file or directory to this directory under specific
     * name and returns this directory.
     *
     * @param f
     * @param asName
     * @return
     */
    public NestDir add(File f, String asName) {
        if(f == null) {
            throw new IllegalArgumentException("file is null");
        }
        if(f.isDirectory()) {
            from(this, f, asName, null);
        } else {
            NestFile.create(this, f, asName);
        }
        return this;
    }

    NestDir addFile(NestFile file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        switch (files.size()) {
            case 0:
                files = Collections.singletonMap(file.getName(), file);
            case 1:
                files = new HashMap<String, NestFile>(files);
            default:
                files.put(file.getName(), file);
        }
        return this;
    }

    /**
     * Returns the path to this directory inclusive starting from the root of the nest.
     *
     * @return
     */
    public String getPath() {
        final StringBuilder buf = new StringBuilder();
        appendPath(buf);
        return buf.toString();
    }

    void appendPath(StringBuilder buf) {
        if (parent != null) {
            parent.appendPath(buf);
        }
        buf.append(ZipUtils.ENTRY_SEPARATOR).append(name);
    }

    /**
     * Asserts that the current nest tree matches the passed in directory tree.
     * File content is matched by comparing content hashes.
     *
     * @param dir
     */
    public void assertMatches(File dir) {
        if (dir == null) {
            Assert.fail("dir is null");
        }
        if (!dir.isDirectory()) {
            Assert.fail("dir is not a directory");
        }

        final File[] children = dir.listFiles();
        if (children.length == 0) {
            if (!dirs.isEmpty() || !files.isEmpty()) {
                final StringBuilder buf = new StringBuilder();
                buf.append(dir.getAbsolutePath()).append(" is missing");
                if (!dirs.isEmpty()) {
                    buf.append("directories ").append(dirs.keySet());
                }
                if (!files.isEmpty()) {
                    buf.append(" files ").append(files.keySet());
                }
                Assert.fail(buf.toString());
            }
            return;
        }

        final Set<String> matchedDirNames = new HashSet<String>();
        final Set<String> matchedFileNames = new HashSet<String>();
        for (File child : children) {

            if (child.isDirectory()) {
                final NestDir nestDir = dirs.get(child.getName());
                if (nestDir == null) {
                    Assert.fail(getPath() + " contains unexpected directory " + child.getName());
                }
                matchedDirNames.add(child.getName());
                nestDir.assertMatches(child);
            } else {
                final NestFile nestFile = files.get(child.getName());
                if (nestFile == null) {
                    Assert.fail(getPath() + " contains unexpected file " + child.getName());
                }
                matchedFileNames.add(child.getName());
                try {
                    Assert.assertArrayEquals(
                            "Hashes don't match for " + nestFile.getFile().getAbsolutePath() + " and "
                                    + child.getAbsolutePath(), HashUtils.hashFile(nestFile.getFile()),
                            HashUtils.hashFile(child));
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to calculate hash", e);
                }
            }
        }

        if (matchedDirNames.size() != dirs.size()) {
            HashSet<String> missingDirs = new HashSet<String>(dirs.keySet());
            missingDirs.removeAll(matchedDirNames);
            Assert.fail(getPath() + " is missing directories " + missingDirs);
        }
        if (matchedFileNames.size() != files.size()) {
            HashSet<String> missingFiles = new HashSet<String>(files.keySet());
            missingFiles.removeAll(matchedFileNames);
            Assert.fail(getPath() + " is missing files " + missingFiles);
        }
    }
}
