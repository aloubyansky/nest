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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wildfly.nest.Nest;
import org.wildfly.nest.util.HashUtils;
import org.wildfly.nest.util.IoUtils;
import org.wildfly.nest.util.ZipUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class AbsolutePathNestBuildTestCase {

    private static File testDir;

    @BeforeClass
    public static void init() {
        testDir = Util.mkRandomDir();
    }

    @AfterClass
    public static void cleanUp() {
        IoUtils.recursiveDelete(testDir);
    }

    @Test
    public void testMain() throws Exception {

        final File testFile = Util.newFile(testDir, "test.txt");
        final File aDir = IoUtils.mkdir(testDir, "a");
        Util.newFile(aDir, "a1TestFile.txt");
        Util.newFile(aDir, "a2TestFile.txt");
        final File bDir = IoUtils.mkdir(aDir, "b");
        Util.newFile(bDir, "b1TestFile.txt");
        IoUtils.mkdir(aDir, "c");

        final File nest = Nest.create()
                .add(testFile.getAbsolutePath())
                .add(aDir.getAbsolutePath())
                .pack(testDir, "nest.zip");

        assertContent(nest, testFile, aDir);
    }

    static void assertContent(File zip, File... root) {
        File nest;
        try {
            nest = IoUtils.mkdir(testDir, "assert_nest");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory: " + e.getLocalizedMessage(), e);
        }

        try {
            ZipUtils.unzip(zip, nest);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to unzip the nest: " + e.getMessage());
        }

        final Comparator<File> filenameComparator = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }};
        final List<File> expected = new ArrayList<File>(Arrays.asList(root));
        Collections.sort(expected, filenameComparator);
        final List<File> nested = new ArrayList<File>(Arrays.asList(nest.listFiles()));
        Collections.sort(nested, filenameComparator);

        Assert.assertEquals(expected.size(), nested.size());
        for(int i = 0; i < expected.size(); ++i) {
            final File expectedRoot = expected.get(i);
            final File nestedRoot = nested.get(i);
            try {
                Assert.assertArrayEquals(HashUtils.hashFile(expectedRoot), HashUtils.hashFile(nestedRoot));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to calculate hash: " + e.getLocalizedMessage());
            }
        }
    }
}
