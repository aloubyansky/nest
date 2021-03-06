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
import java.util.Collection;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestException;
import org.wildfly.nest.build.NestBuildContext;
import org.wildfly.nest.expand.NestExpandContext;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class NestLocationsAttachmentHandler implements NestAttachmentHandler {

    private static final String ID = "NEST_LOCATIONS";

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
                        System.out.println("NestAttachment.read nest location name " + byteArray.readUTF());
                        break;
                    case RELATIVE_TO:
                        System.out.println("NestAttachment.read nest location relative-to " + byteArray.readUTF());
                        break;
                    case PATH:
                        System.out.println("NestAttachment.read nest location path " + byteArray.readUTF());
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
    public byte[] toByteArray(NestBuildContext ctx) throws NestException {

        final Collection<String> nestNames = ctx.getNestLocationNames();
        if (nestNames.isEmpty()) {
            return null;
        }

        final DataToByteArray data = DataToByteArray.create();
        final byte[] bytes;

        try {
            for (String nestName : nestNames) {
                final EntryLocation location = ctx.getNestLocation(nestName);
                try {
                    if(location.getName() == null) {
                        throw new NestException("Nest location is missing name");
                    }

                    data.writeByte(NAME).writeUTF(location.getName());
                    System.out.println("NestAttachment.write nest location name " + location.getName());

                    if (location.getRelativeTo() != null) {
                        data.writeByte(RELATIVE_TO).writeUTF(location.getRelativeTo());
                        System.out.println("    relative-to " + location.getRelativeTo());
                    }
                    if (location.getPath() != null) {
                        data.writeByte(PATH).writeUTF(location.getPath());
                        System.out.println("    path " + location.getPath());
                    }
                } catch (IOException e) {
                    throw new NestException("Failed to write nest location " + location, e);
                }
            }
        } finally {
            bytes = data.close();
        }
        return bytes;
    }

}
