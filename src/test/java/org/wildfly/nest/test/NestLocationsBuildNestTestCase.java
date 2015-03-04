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

import org.junit.BeforeClass;
import org.junit.Test;
import org.wildfly.nest.Nest;
import org.wildfly.nest.test.util.NestDir;
import org.wildfly.nest.test.util.Util;
import org.wildfly.nest.util.IoUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class NestLocationsBuildNestTestCase extends NestBuildTestBase {

    private static File testFile;
    private static File aBaseDir;
    private static File aDir;
    private static File bBaseDir;
    private static File bDir;
    private static File cDir;

    @BeforeClass
    public static void createSourceStructure() throws Exception {

        testFile = Util.newFile(testDir, "test.txt");

        aBaseDir = IoUtils.mkdir(testDir, "home");
        aDir = IoUtils.mkdir(aBaseDir, "a");
        Util.newFile(aDir, "a1TestFile.txt");
        Util.newFile(aDir, "a2TestFile.txt");
        final File aaDir = IoUtils.mkdir(aDir, "aa");
        Util.newFile(aaDir, "aaTestFile.txt");

        bBaseDir = IoUtils.mkdir(testDir, "home", "skip", "to");
        bDir = IoUtils.mkdir(bBaseDir, "b");
        Util.newFile(bDir, "b1TestFile.txt");

        final File cBaseDir = IoUtils.mkdir(testDir, "origin", "of");
        cDir = IoUtils.mkdir(cBaseDir, "c");
        Util.newFile(cDir, "c1TestFile.txt");
    }

    @Test
    public void testMain() throws Exception {

        final File nestZip = Nest.create()
            .nameNestLocation("NEST_BASE_LOCATION_A", "base-a")
            .nameNestLocation("NEST_LOCATION_C", "base-a")
            .nameNestLocation("NEST_BASE_LOCATION_B", "NEST_BASE_LOCATION_A", "etc")
            .add(testFile.getAbsolutePath()).under("misc")
            .add(aDir.getAbsolutePath()).underLocation("NEST_BASE_LOCATION_A")
            .add(bDir.getAbsolutePath()).underLocation("NEST_BASE_LOCATION_B", "skip/base-b")
            .add(cDir.getAbsolutePath()).underLocation("NEST_LOCATION_C")
            .pack(testDir, "nest.zip");

        final NestDir expectedTree = NestDir.root();
        final NestDir baseA = expectedTree.newDir("base-a")
                .add(aDir)
                .add(cDir);
        baseA.newDir("etc", "skip", "base-b").add(bDir);
        expectedTree.newDir("misc").add(testFile);

        assertZipContent(nestZip, expectedTree);

        // test unpacking
        final File unpackedNest = new File(testDir, "unpacked-nest");
        Nest.open(nestZip).unpack(unpackedNest);
        expectedTree.assertMatches(unpackedNest);
    }
}
