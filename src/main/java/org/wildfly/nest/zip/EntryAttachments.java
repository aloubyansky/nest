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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.wildfly.nest.NestException;
import org.wildfly.nest.build.EntryBuildContext;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class EntryAttachments<T extends OutputStream> {

    public static <T extends OutputStream> EntryAttachments<T> create() {
        return new EntryAttachments<T>();
    }

    private Map<String, EntryAttachmentHandler<T>> handlers = Collections.emptyMap();

    private EntryAttachments() {
    }

    public EntryAttachments<T> add(EntryAttachmentHandler<T> handler) {
        if(handler == null) {
            throw new IllegalArgumentException("serializer is null");
        }
        if(handler.getId() == null) {
            throw new IllegalArgumentException("Handler didn't provide its id: " + handler);
        }
        switch(handlers.size()) {
            case 0:
                handlers = Collections.singletonMap(handler.getId(), handler);
                break;
            case 1:
                handlers = new HashMap<String, EntryAttachmentHandler<T>>(handlers);
            default:
                handlers.put(handler.getId(), handler);
        }
        return this;
    }

    public byte[] write(EntryBuildContext<T> ctx) throws NestException {

        if (handlers.isEmpty()) {
            return null;
        }

        ByteArrayOutputStream bytesArray = null;
        DataOutputStream dos = null;

        try {
            for (EntryAttachmentHandler<T> writer : handlers.values()) {
                final byte[] bytes = writer.toByteArray(ctx);
                if (bytes != null) {
                    final String id = writer.getId();
                    if (id == null) {
                        throw new IllegalStateException("Attachment handler didn't its id: " + writer);
                    }

                    if (bytesArray == null) {
                        bytesArray = new ByteArrayOutputStream();
                        dos = new DataOutputStream(bytesArray);
                    }

                    dos.writeUTF(id);
                    dos.writeInt(bytes.length);
                    if (bytes.length > 0) {
                        dos.write(bytes, 0, bytes.length);
                    }
                }
            }

            return bytesArray == null ? null : bytesArray.toByteArray();
        } catch (IOException e) {
            throw new NestException("Failed to write bytes", e);
        } finally {
            IoUtils.safeClose(dos);
        }
    }

    public void read(byte[] bytes) throws NestException {

        if (bytes == null || bytes.length == 0) {
            return;
        }
        if (handlers.isEmpty()) {
            return;
        }

        final ByteArrayInputStream bytesStream = new ByteArrayInputStream(bytes);
        final DataInputStream dis = new DataInputStream(bytesStream);
        try {

            String id = readId(dis);
            while (id != null) {
                final EntryAttachmentHandler<?> reader = handlers.get(id);
                if (reader == null) {
                    throw new NestException("Unrecognized handler id: " + id);
                }

                final int length = dis.readInt();
                byte[] attachmentBytes;
                if (length == 0) {
                    attachmentBytes = new byte[0];
                } else {
                    attachmentBytes = new byte[length];
                    if (dis.available() < length) {
                        throw new NestException("The length of the attachment is bigger than the available count.");
                    }
                    dis.readFully(attachmentBytes);
                }
                reader.fromByteArray(attachmentBytes);
                id = readId(dis);
            }
        } catch (IOException e) {
            throw new NestException("Failed to read attachments", e);
        } finally {
            IoUtils.safeClose(dis);
        }
    }

    protected static String readId(DataInputStream dis) throws NestException {
        try {
            return dis.readUTF();
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            throw new NestException("Failed to read reader id", e);
        }
    }
}
