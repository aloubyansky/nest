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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class DataToByteArray {

    public static DataToByteArray create() {
        return new DataToByteArray();
    }

    private ByteArrayOutputStream byteArray;
    private DataOutputStream dos;

    private DataToByteArray() {
    }

    public DataToByteArray writeByte(byte b) throws IOException {
        dos.writeByte(b);
        return this;
    }

    public DataToByteArray writeUTF(String str) throws IOException {
        dos.writeUTF(str);
        return this;
    }

    public byte[] close() {
        if(byteArray == null) {
            return null;
        }
        IoUtils.safeClose(dos);
        dos = null;
        byte[] bytes = byteArray.toByteArray();
        byteArray = null;
        return bytes;
    }

    protected DataOutputStream getOut() {
        if(byteArray == null) {
            byteArray = new ByteArrayOutputStream();
            dos = new DataOutputStream(byteArray);
        }
        return dos;
    }
}
