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

package org.wildfly.nest;

/**
 * Represents an entry (file or directory) in a package.
 *
 * @author Alexey Loubyansky
 */
public class NestEntry {

    private EntryLocation nestLocation;
    private EntryLocation unpackLocation;

    public static NestEntry create() {
        return under(EntryLocation.DEFAULT);
    }

    public static NestEntry under(EntryLocation nestLocation) {
        return new NestEntry(nestLocation, null);
    }

    public static NestEntry under(EntryLocation nestLocation, EntryLocation unpackToLocation) {
        return new NestEntry(nestLocation, unpackToLocation);
    }

    NestEntry(EntryLocation nestLocation, EntryLocation targetLocation) {
        if(nestLocation == null) {
            throw new IllegalArgumentException("Nest location is null");
        }
        this.nestLocation = nestLocation;
        this.unpackLocation = targetLocation;
    }

    /**
     * Entry location in the nest.
     *
     * @return  entry location in the nest
     */
    public EntryLocation getNestLocation() {
        return nestLocation;
    }

    /**
     * Sets entry location in the nest to the specified location.
     * If the passed in location is null, the entry will be associated with
     * the nest root.
     *
     * @param location  nest location or null
     */
    public void setNestLocation(EntryLocation location) {
        this.nestLocation = location == null ? EntryLocation.DEFAULT : location;
    }

    /**
     * Returns the target location the entry should be unpacked to.
     *
     * @return  target location the entry should be unpacked to
     */
    public EntryLocation getExpandLocation() {
        return unpackLocation;
    }

    public void setUnpackLocation(EntryLocation location) {
        this.unpackLocation = location == null ? EntryLocation.DEFAULT : location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestLocation == null) ? 0 : nestLocation.hashCode());
        result = prime * result + ((unpackLocation == null) ? 0 : unpackLocation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NestEntry other = (NestEntry) obj;
        if (nestLocation == null) {
            if (other.nestLocation != null)
                return false;
        } else if (!nestLocation.equals(other.nestLocation))
            return false;
        if (unpackLocation == null) {
            if (other.unpackLocation != null)
                return false;
        } else if (!unpackLocation.equals(other.unpackLocation))
            return false;
        return true;
    }
}
