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

import java.io.IOException;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;
import org.wildfly.nest.build.NestBuildContext;
import org.wildfly.nest.build.NestEntrySource;
import org.wildfly.nest.expand.NestExpandContext;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class EntryLocationsAttachmentHandler implements EntryAttachmentHandler {

    private static final String ID = "ENTRY_LOCATIONS";

    private static final byte NAME = 0;
    private static final byte RELATIVE_TO = 1;
    private static final byte PATH = 2;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void fromByteArray(NestExpandContext ctx, byte[] bytes) throws NestException {

        assert bytes != null : "bytes is null";

        final DataFromByteArray byteArray = DataFromByteArray.create(bytes);
        try {
            Byte type = byteArray.readByte();
            while (type != null) {
                switch (type) {
                    case NAME:
                        System.out.println("EntryAttachment.read nest location name " + byteArray.readUTF());
                        break;
                    case RELATIVE_TO:
                        System.out.println("EntryAttachment.read nest location relative-to " + byteArray.readUTF());
                        break;
                    case PATH:
                        System.out.println("EntryAttachment.read nest location path " + byteArray.readUTF());
                        break;
                    default:
                        throw new NestException("Unexpected nest location property type code " + type);
                }
                type = byteArray.readByte();
            }
        } catch (IOException e) {
            throw new NestException("Failed to read nest location attachment", e);
        } finally {
            IoUtils.safeClose(byteArray);
        }
    }

    @Override
    public byte[] toByteArray(NestBuildContext ctx, NestEntrySource entry) throws NestException {

        final EntryLocation nestLocation = entry.getNestEntry().getNestLocation();
        final DataToByteArray data = DataToByteArray.create();
        final byte[] bytes;
        try {
            if (nestLocation.getName() != null) {
                data.writeByte(NAME).writeUTF(nestLocation.getName());
                System.out.println("EntryAttachment.write location name " + nestLocation.getName());
            }
            if (nestLocation.getRelativeTo() != null) {
                data.writeByte(RELATIVE_TO).writeUTF(nestLocation.getRelativeTo());
                System.out.println("EntryAttachment.write relative-to " + nestLocation.getRelativeTo());
                if (nestLocation.getPath() != null) {
                    data.writeByte(PATH).writeUTF(nestLocation.getPath());
                    System.out.println("EntryAttachment.write path " + nestLocation.getPath());
                }
            }
        } catch (IOException e) {
            throw new NestException("Failed to write nest location " + nestLocation, e);
        } finally {
            bytes = data.close();
        }
        return bytes;
    }

}
