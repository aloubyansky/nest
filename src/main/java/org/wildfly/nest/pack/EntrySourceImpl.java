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

package org.wildfly.nest.pack;

import org.wildfly.nest.EntryLocation;
import org.wildfly.nest.NestEntry;

/**
 *
 * @author Alexey Loubyansky
 */
class EntrySourceImpl implements EntrySource {

    private final NestEntry nestEntry;
    private final EntryLocation srcLocation;

    EntrySourceImpl(EntryLocation srcLocation) {
        this(NestEntry.create(), srcLocation);
    }

    EntrySourceImpl(NestEntry nestEntry, EntryLocation srcLocation) {
        if(nestEntry == null) {
            throw new IllegalArgumentException("entry is null");
        }
        if(srcLocation == null) {
            throw new IllegalArgumentException("source location is null");
        }
        this.nestEntry = nestEntry;
        this.srcLocation = srcLocation;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.pack.EntrySource#getNestEntry()
     */
    @Override
    public NestEntry getNestEntry() {
        return nestEntry;
    }

    /* (non-Javadoc)
     * @see org.wildfly.nest.pack.EntrySource#getSourceLocation()
     */
    @Override
    public EntryLocation getSourceLocation() {
        return srcLocation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestEntry == null) ? 0 : nestEntry.hashCode());
        result = prime * result + ((srcLocation == null) ? 0 : srcLocation.hashCode());
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
        EntrySourceImpl other = (EntrySourceImpl) obj;
        if (nestEntry == null) {
            if (other.nestEntry != null)
                return false;
        } else if (!nestEntry.equals(other.nestEntry))
            return false;
        if (srcLocation == null) {
            if (other.srcLocation != null)
                return false;
        } else if (!srcLocation.equals(other.srcLocation))
            return false;
        return true;
    }
}
