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

import org.junit.Test;
import org.wildfly.nest.Nest;
import org.wildfly.nest.test.util.NestDir;
import org.wildfly.nest.test.util.Util;
import org.wildfly.nest.util.IoUtils;


/**
 *
 * @author Alexey Loubyansky
 */
public class SourceLocationsBuildTestCase extends NestBuildTestBase {

    @Test
    public void testMain() throws Exception {

        final File testFile = Util.newFile(testDir, "test.txt");

        final File aBaseDir = IoUtils.mkdir(testDir, "home");
        final File aDir = IoUtils.mkdir(aBaseDir, "a");
        Util.newFile(aDir, "a1TestFile.txt");
        Util.newFile(aDir, "a2TestFile.txt");
        final File aaDir = IoUtils.mkdir(aDir, "aa");
        Util.newFile(aaDir, "aaTestFile.txt");

        final File bBaseDir = IoUtils.mkdir(testDir, "home", "skip", "to");
        final File bDir = IoUtils.mkdir(bBaseDir, "b");
        Util.newFile(bDir, "b1TestFile.txt");

        final File cBaseDir = IoUtils.mkdir(testDir, "origin", "of");
        final File cDir = IoUtils.mkdir(cBaseDir, "c");
        Util.newFile(cDir, "c1TestFile.txt");

        final File nestZip = Nest.create()
            .nameSourceLocation("BASE_LOCATION_A")
            .nameSourceLocation("BASE_LOCATION_B", "BASE_LOCATION_A", "skip")
            .linkSourceLocation("LOCATION_C", cDir.getAbsolutePath())
            .addLocation("BASE_LOCATION_A", "a")
            .addLocation("BASE_LOCATION_B", "to/b")
            .addLocation("LOCATION_C")
            .add(testFile.getAbsolutePath())
            .linkSourceLocation("BASE_LOCATION_A", aBaseDir.getAbsolutePath())
            .build(testDir, "nest.zip");

        final NestDir expectedTree = NestDir.root()
            .add(aDir)
            .add(bDir)
            .add(cDir)
            .add(testFile);

        assertZipContent(nestZip, expectedTree);

        // test expanding
        final File expandedNest = new File(testDir, "expanded-nest");
        Nest.open(nestZip).expand(expandedNest);
        expectedTree.assertMatches(expandedNest);
    }
}
