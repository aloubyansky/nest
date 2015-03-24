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

package org.wildfly.nest.zip;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 *
 * @author Alexey Loubyansky
 */
public class DataFromByteArray implements Closeable {

    public static DataFromByteArray create(byte[] bytes) {
        return new DataFromByteArray(bytes);
    }

    private final DataInputStream dis;

    private DataFromByteArray(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes is null");
        }
        if (bytes.length == 0) {
            dis = null;
        } else {
            dis = new DataInputStream(new ByteArrayInputStream(bytes));
        }
    }

    public Byte readByte() throws IOException {
        if (dis == null) {
            return null;
        }
        try {
            return dis.readByte();
        } catch (EOFException e) {
            return null;
        }
    }

    public String readUTF() throws IOException {
        if (dis == null) {
            return null;
        }
        try {
            return dis.readUTF();
        } catch (EOFException e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        if (dis != null) {
            dis.close();
        }
    }
}
