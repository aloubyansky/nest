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
public class LinkNestPathToExpandPathTestCase extends NestBuildTestBase {

    @Test
    public void testMain() throws Exception {

        final File nestBase = IoUtils.mkdir(testDir, "nest_base");
        final File testFile = Util.newFile(nestBase, "test.txt");
        final File aDir = IoUtils.mkdir(nestBase, "a");
        final File a1TestFile = Util.newFile(aDir, "a1TestFile.txt");
        Util.newFile(aDir, "a2TestFile.txt");
        final File bDir = IoUtils.mkdir(aDir, "b");
        Util.newFile(bDir, "b1TestFile.txt");
        IoUtils.mkdir(aDir, "c");
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
            .linkNestPath("a/", "dir_a")
            .linkNestPath("a/a1TestFile.txt", "misc/a1TestFile.txt")
            .linkNestPath("a/b/", "dir_b")
            .linkNestPath("test.txt", "misc/root_test.txt")
            .expand(expandedNest);

        final NestDir expandedTree = NestDir.root();
        expandedTree.newDir("misc")
            .add(testFile, "root_test.txt")
            .add(a1TestFile);
        expandedTree.add(aDir, "dir_a", new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                final String name = pathname.getName();
                return !name.equals(bDir.getName()) && !name.equals(a1TestFile.getName());
            }});
        expandedTree.add(bDir, "dir_b");
        expandedTree.add(dDir);

        expandedTree.assertMatches(expandedNest);
    }
}
