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

import org.wildfly.nest.util.ZipUtils;

/**
 * Nest file linked to the actual file.
 *
 * @author Alexey Loubyansky
 */
public class NestFile {

    /**
     * Creates a new nest file linked to the specified file.
     *
     * @param f
     * @return
     */
    public static NestFile create(File f) {
        return create(null, f, f.getName());
    }

    public static NestFile create(File f, String asName) {
        return create(null, f, asName);
    }

    public static NestFile create(NestDir parent, File f) {
        return create(parent, f, f.getName());
    }

    public static NestFile create(NestDir parent, File f, String asName) {
        return new NestFile(parent, f, asName);
    }

    private final NestDir parent;
    private final String name;
    private final File f;

    private NestFile(File f) {
        this(f, f.getName());
    }

    private NestFile(File f, String asName) {
        this(null, f, asName);
    }

    private NestFile(NestDir parent, File f) {
        this(parent, f, f.getName());
    }

    private NestFile(NestDir parent, File f, String asName) {
        if(f == null) {
            throw new IllegalArgumentException("file is null");
        }
        if(asName == null) {
            throw new IllegalArgumentException("asName is null");
        }
        this.name = asName;
        this.f = f;
        this.parent = parent;
        if(parent != null) {
            parent.addFile(this);
        }
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return f;
    }

    public String getPath() {
        final StringBuilder buf = new StringBuilder();
        if(parent != null) {
            parent.appendPath(buf);
        }
        buf.append(ZipUtils.ENTRY_SEPARATOR).append(getName());
        return buf.toString();
    }
}
