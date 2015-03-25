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

package org.wildfly.nest.expand;

import org.wildfly.nest.NestException;
import org.wildfly.nest.zip.EntryAttachments;
import org.wildfly.nest.zip.NestAttachments;

/**
 *
 * @author Alexey Loubyansky
 */
public abstract class AbstractNestExpander<T> implements NestExpander {

    /* (non-Javadoc)
     * @see org.wildfly.nest.expand.NestExpander#expand(org.wildfly.nest.expand.NestExpandContext)
     */
    @Override
    public void expand(NestExpandContext ctx) throws NestException {
        try {
            prepareToExpand(ctx);
            final byte[] nestBytes = getNestAttachments(ctx);
            if(nestBytes != null) {
                NestAttachments.DEFAULT.read(ctx, nestBytes);
            }
            for (T entry : getEntries()) {
                expandEntry(ctx, entry);
                final byte[] bytes = getEntryAttachments(ctx, entry);
                if(bytes != null) {
                    EntryAttachments.DEFAULT.read(ctx, bytes);
                }
            }
        } finally {
            tidyUpAfterExpand(ctx);
        }
    }

    protected abstract Iterable<T> getEntries() throws NestException;

    protected void prepareToExpand(NestExpandContext ctx) throws NestException {
    }

    protected void tidyUpAfterExpand(NestExpandContext ctx) throws NestException {
    }

    protected byte[] getNestAttachments(NestExpandContext ctx) throws NestException {
        return null;
    }

    protected abstract void expandEntry(NestExpandContext ctx, T entry) throws NestException;

    protected byte[] getEntryAttachments(NestExpandContext ctx, T entry) throws NestException {
        return null;
    }
}
