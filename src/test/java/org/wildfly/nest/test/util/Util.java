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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class Util {

    static final File TMP_DIR = new File(SecurityActions.getSystemProperty("java.io.tmpdir"));


    /**
     * Creates a randomly named directory
     *
     * @return  created directory
     */
    public static File mkRandomDir() {
        try {
            return IoUtils.mkdir(TMP_DIR, randomString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory", e);
        }
    }

    /**
     * Creates a simple file with random content
     *
     * @return  created file
     */
    public static File newFile(File dir, String fileName) {
        final File f = new File(dir, fileName);
        if(f.exists()) {
            throw new IllegalStateException("File already exists: " + f.getAbsolutePath());
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(randomString().getBytes());
        } catch(IOException e) {
            throw new IllegalStateException("Failed to create file " + f.getAbsolutePath(), e);
        } finally {
            IoUtils.safeClose(fos);
        }
        return f;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
