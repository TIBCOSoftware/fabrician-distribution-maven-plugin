/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 *
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code.
 * In most instances, the license terms are contained in a file named license.txt.
 */
package org.fabrician.maven.plugins;

import java.io.File;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

public class GridlibPackageMojoTest {

    private String resourceDir = "src/test/resources";
    private String tmpDir = "tmp";
    
    @Test
    public void targzToZip() throws Exception {
        File target = new File(tmpDir, "targzToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"), 
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());

        target = new File(tmpDir, "targzToZipAlt.zip");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"), 
                "myAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
    }

    @Test
    public void targzToTargz() throws Exception {
        File target = new File(tmpDir, "targzToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"),
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        
        target = new File(tmpDir, "targzToTargzAlt.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"),
                "myAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());

        target = new File(tmpDir, "targzToTargzAlt2.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                new String[] { "**\\myOldDir\\*" },
                new File(resourceDir, "distribution"),
                "myAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
    }

    @Test
    public void zipToTargz() throws Exception {
        File target = new File(tmpDir, "zipToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"),
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        
        target = new File(tmpDir, "zipToTargzAlt.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                null,
                new File(resourceDir, "distribution"),
                "MyAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        
        target = new File(tmpDir, "zipToTargzAlt2.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                null,
                new String[] { "**\\myOldDir\\*" },
                new File(resourceDir, "distribution"),
                "MyAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
    }

    @Test
    public void zipToZip() throws Exception {
        File target = new File(tmpDir, "zipToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.zip"),
                null,
                null,
                new File(resourceDir, "distribution"),
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        
        target = new File(tmpDir, "zipToZipAlt.zip");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.zip"),
                null,
                null,
                new File(resourceDir, "distribution"),
                "myAltDir",
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
    }
    
    @Test
    public void dirToZip() throws Exception {
        File target = new File(tmpDir, "dirToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "tmp"),
                null,
                null,
                new File(resourceDir, "distribution"),
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        Assert.assertTrue("foo/dir_file.txt doesn't exist in " + target, CompressUtils.entryExistsInZip(target, "foo/dir_file.txt"));
    }
    
    @Test
    public void dirToTargz() throws Exception {
        File target = new File(tmpDir, "dirToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "tmp"),
                null,
                null,
                new File(resourceDir, "distribution"),
                null,
                new MyMavenProject());
        packager.execute();
        Assert.assertTrue(target + " doesn't exist", target.exists());
        Assert.assertTrue("foo/dir_file.txt doesn't exist in " + target, CompressUtils.entryExistsInTargz(target, "foo/dir_file.txt"));
    }
    
    private class MyMavenProject extends MavenProject {
        @Override
        public Properties getProperties() {
            Properties p = new Properties();
            p.setProperty("distribution.version", "4.4.4.4");
            p.setProperty("distribution.os", "TRS-DOS");
            return p;
        }
    }
}