package org.wildfly.nest;


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



/**
 * Represents information about where the item has to be unpacked to.
 *
 * @author Alexey Loubyansky
 */
public class EntryLocation {

    private final String alias;
    private final String relativeTo;
    private final String path;

    public static EntryLocation path(String path) {
        return new EntryLocation(path);
    }

    public static EntryLocation alias(String alias) {
        return new EntryLocation(alias, null);
    }

    public static EntryLocation alias(String alias, String path) {
        return new EntryLocation(alias, path);
    }

    public static EntryLocation alias(String alias, String relativeToAlias, String path) {
        return new EntryLocation(alias, relativeToAlias, path);
    }

    EntryLocation(String path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        this.path = path;
        alias = null;
        relativeTo = null;
    }

    EntryLocation(String alias, String path) {
        if(alias == null) {
            throw new IllegalArgumentException("alias is null");
        }
        this.alias = alias;
        this.path = path;
        this.relativeTo = null;
    }

    EntryLocation(String alias, String relativeTo, String path) {
        if(alias == null) {
            throw new IllegalArgumentException("alias is null");
        }
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        if(relativeTo == null) {
            throw new IllegalArgumentException("relativeTo is null");
        }
        this.alias = alias;
        this.path = path;
        this.relativeTo = relativeTo;
    }

    /**
     * Returns the alias which to resolves to a path relative to which
     * the current target should be resolved.
     * If the relative-to alias was not specified, the method return null.
     *
     * @return  the alias of the location relative to which the current target
     *          should be resolved or null if the relative location was not
     *          specified
     */
    public String getRelativeTo() {
        return relativeTo;
    }

    /**
     * Alias for this location if provided.
     *
     * @return  alias for this location or null if none was provided
     */
    public String getAlias() {
        return alias;
    }

    /**
     * The path returned by this method is relative to the alias
     * returned from {@link #getRelativeTo()} unless the alias returned is null.
     * Otherwise, the returned path will be considered as absolute.
     * The method may also return null if the path was not specified. In that case,
     * {@link #getRelativeTo()} must return a non-null value, which will be considered
     * the target location this instance represents.
     *
     * @return  the path this location represents or null if the path was not specified
     */
    public String getPath() {
        return path;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((relativeTo == null) ? 0 : relativeTo.hashCode());
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
        EntryLocation other = (EntryLocation) obj;
        if (alias == null) {
            if (other.alias != null)
                return false;
        } else if (!alias.equals(other.alias))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (relativeTo == null) {
            if (other.relativeTo != null)
                return false;
        } else if (!relativeTo.equals(other.relativeTo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if(alias != null) {
            buf.append(alias).append('=');
        }
        if(relativeTo != null) {
            buf.append('$').append(relativeTo).append('/');
        }
        if(path != null) {
            buf.append(path);
        }
        return buf.toString();
    }
}
