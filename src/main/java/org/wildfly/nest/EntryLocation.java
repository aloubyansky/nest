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

    private final String name;
    private final String relativeToName;
    private final String path;

    public static EntryLocation path(String path) {
        return new EntryLocation(path);
    }

    public static EntryLocation name(String name) {
        return new EntryLocation(name, null);
    }

    public static EntryLocation name(String name, String path) {
        return new EntryLocation(name, path);
    }

    public static EntryLocation name(String name, String relativeToName, String path) {
        return new EntryLocation(name, relativeToName, path);
    }

    EntryLocation(String path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        this.path = path;
        name = null;
        relativeToName = null;
    }

    EntryLocation(String name, String path) {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        this.name = name;
        this.path = path;
        this.relativeToName = null;
    }

    EntryLocation(String name, String relativeTo, String path) {
        if(name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        if(relativeTo == null) {
            throw new IllegalArgumentException("relativeTo is null");
        }
        this.name = name;
        this.path = path;
        this.relativeToName = relativeTo;
    }

    /**
     * Returns the name which resolves to a path relative to which
     * the current location should be resolved.
     * If the relative-to name is not specified, the method will return null.
     *
     * @return  the name of the location relative to which the current location
     *          should be resolved or null if the relative location was not
     *          specified
     */
    public String getRelativeTo() {
        return relativeToName;
    }

    /**
     * Alias for this location if provided.
     *
     * @return  alias for this location or null if none was provided
     */
    public String getName() {
        return name;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((relativeToName == null) ? 0 : relativeToName.hashCode());
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (relativeToName == null) {
            if (other.relativeToName != null)
                return false;
        } else if (!relativeToName.equals(other.relativeToName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if(name != null) {
            buf.append(name).append('=');
        }
        if(relativeToName != null) {
            buf.append('$').append(relativeToName).append('/');
        }
        if(path != null) {
            buf.append(path);
        }
        return buf.toString();
    }
}
