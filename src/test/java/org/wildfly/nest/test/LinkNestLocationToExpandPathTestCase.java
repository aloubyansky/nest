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

package org.wildfly.nest.test;

import java.io.File;
import java.io.FileFilter;

import org.junit.Test;
import org.wildfly.nest.Nest;
import org.wildfly.nest.test.util.NestDir;
import org.wildfly.nest.test.util.Util;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class LinkNestLocationToExpandPathTestCase extends NestBuildTestBase {

    @Test
    public void testMain() throws Exception {

        final File nestBase = IoUtils.mkdir(testDir, "nest_base");
        final File testFile = Util.newFile(nestBase, "test.txt");
        final File aDir = IoUtils.mkdir(nestBase, "a");
        final File a1TestFile = Util.newFile(aDir, "a1TestFile.txt");
        Util.newFile(aDir, "a2TestFile.txt");
        final File bDir = IoUtils.mkdir(aDir, "b");
        Util.newFile(bDir, "b1TestFile.txt");
        final File cDir = IoUtils.mkdir(aDir, "c");
        final File cFile = Util.newFile(cDir, "c.txt");
        final File dDir = IoUtils.mkdir(nestBase, "d");
        Util.newFile(dDir, "d.txt");

        final File nestZip = Nest.create()
                .add(testFile.getAbsolutePath())
                .add(aDir.getAbsolutePath())
                .add(dDir.getAbsolutePath())
                .build(testDir, "nest.zip");

        final NestDir expectedZipTree = NestDir.from(nestBase);
        assertZipContent(nestZip, expectedZipTree);

        // test expanding
        final File expandedNest = new File(testDir, "expanded-nest");
        Nest.open(nestZip)
            .nameNestLocation("DIR_A", "a/")
            .nameNestLocation("DIR_B", "DIR_A", "b/")
            .nameNestLocation("DIR_C", "a/c/")
            .nameExpandLocation("EXPAND_DIRS")
            .nameExpandLocation("EXPAND_C", "EXPAND_DIRS", "dir_c")

            .linkNestLocation("DIR_A").toPath("dirs/dir_a")
            .linkNestLocation("DIR_B").toPath("EXPAND_DIRS", "dir_b")
            .linkNestLocation("DIR_C").toLocation("EXPAND_C")

            .linkNestPath("DIR_A", "a1TestFile.txt").toPath("misc/a1TestFile.txt")
            .linkNestPath("DIR_A", "c/c.txt").toPath("misc/c.txt")

            .linkNestPath("test.txt").toPath("misc/root_test.txt")

            .linkExpandLocation("EXPAND_DIRS", "dirs")
            .expand(expandedNest);

        final NestDir expandedTree = NestDir.root();
        expandedTree.newDir("misc")
            .add(testFile, "root_test.txt")
            .add(a1TestFile)
            .add(cFile);
        final NestDir dirs = expandedTree.newDir("dirs");
        dirs.add(aDir, "dir_a", new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                final String name = pathname.getName();
                return !name.equals(bDir.getName()) &&
                       !name.equals(cDir.getName()) &&
                       !name.equals(a1TestFile.getName()) &&
                       !name.equals(cFile.getName());
            }});
        dirs.add(bDir, "dir_b");
        dirs.newDir("dir_c");
        expandedTree.add(dDir);

        expandedTree.assertMatches(expandedNest);
    }
}
