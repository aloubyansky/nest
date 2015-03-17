package org.wildfly.nest;

import org.wildfly.nest.util.ZipUtils;


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
 * Represents location information of the item.
 *
 * @author Alexey Loubyansky
 */
public class EntryLocation {

    public static final EntryLocation DEFAULT = path(".");

    /** name/alias for this location, may be null */
    private final String name;
    /** name/alias of the location relative to which this location should be resolved, may be null */
    private String relativeToName;
    /** the path is absolute if relativeToName is null, otherwise the path is relative, may be null */
    private String path;

    /**
     * Creates a new location based on the absolute path passed in as an argument.
     *
     * @param path  absolute path for which the location should be created
     * @return  location representing the path
     */
    public static EntryLocation path(String path) {
        return new EntryLocation(path);
    }

    /**
     * Creates a new location with the path relative to the named location.
     *
     * @param locationName  location name relative to which new location should be created
     * @param relativePath  relative to the specified named location path
     * @return  new location
     */
    public static EntryLocation path(String locationName, String relativePath) {
        return new EntryLocation(null, locationName, relativePath);
    }

    /**
     * Creates a new location with the specified name.
     *
     * @param locationName  location name
     * @return  new named location
     */
    public static EntryLocation name(String locationName) {
        return new EntryLocation(locationName, null);
    }

    /**
     * Creates a new named location for the given path.
     *
     * @param locationName  location name
     * @param path  absolute path of the location
     * @return  new named location for the specified path
     */
    public static EntryLocation name(String locationName, String path) {
        return new EntryLocation(locationName, path);
    }

    /**
     * Creates a new named location relative to the specified named location.
     *
     * @param locationName  new location name
     * @param relativeToName  named location relative to which the new location will be resolved
     * @param path  relative to the specified named location path
     * @return  new named location relative to the specified named location
     */
    public static EntryLocation name(String locationName, String relativeToName, String path) {
        if(locationName == null) {
            throw new IllegalArgumentException("locationName is null");
        }
        return new EntryLocation(locationName, relativeToName, path);
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

    /**
     * Links this location to the given path.
     * If the location has already been linked to an actual path or relative to another
     * named location, the method will throw an exception.
     *
     * @param path  the path to link this location to
     * @throws NestException
     */
    public void link(String path) throws NestException {
        if(path == null) {
            throw new NestException("path is null");
        }
        assertNotLinked();
        this.path = path;
    }

    /**
     * Links this location to a path relative to the specified named location.
     * If the location has already been linked to an actual path or relative to another
     * named location, the method will throw an exception.
     *
     * @param relativeToName
     * @param path
     * @throws NestException
     */
    public void link(String relativeToName, String path) throws NestException {
        if(relativeToName == null) {
            throw new NestException("relativeToName is null");
        }
//        if(path == null) {
//            throw new NestException("path is null");
//        }
        assertNotLinked();
        this.relativeToName = relativeToName;
        this.path = path;
    }

    /**
     * @throws NestException
     */
    private void assertNotLinked() throws NestException {
        if(this.path != null || this.relativeToName != null) {
            final StringBuilder buf = new StringBuilder("Location has already been linked to ");
            if(relativeToName == null) {
                buf.append(this.path);
            } else {
                buf.append('$').append(relativeToName);
                if(this.path != null) {
                    buf.append(ZipUtils.ENTRY_SEPARATOR).append(this.path);
                }
            }
            throw new NestException(buf.toString());
        }
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
