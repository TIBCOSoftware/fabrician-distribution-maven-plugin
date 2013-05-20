/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 *
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code.
 * In most instances, the license terms are contained in a file named license.txt.
 */
package org.fabrician.maven.plugins;

import java.io.File;

import org.junit.Test;

public class GridlibPackageMojoTest {

    private String resourceDir = "src/test/resources";
    private String tmpDir = "tmp";
    
    @Test
    public void targzToZip() throws Exception {
        File target = new File(tmpDir, "targzToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"), 
                null);
        packager.execute();
        assert(target.exists());

        target = new File(tmpDir, "targzToZipAlt.zip");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"), 
                "myAltDir");
        packager.execute();
        assert(target.exists());
    }

    @Test
    public void targzToTargz() throws Exception {
        File target = new File(tmpDir, "targzToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"),
                null);
        packager.execute();
        assert(target.exists());
        
        target = new File(tmpDir, "targzToTargzAlt.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"),
                "myAltDir");
        packager.execute();
        assert(target.exists());
    }

    @Test
    public void zipToTargz() throws Exception {
        File target = new File(tmpDir, "zipToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"),
                null);
        packager.execute();
        assert(target.exists());
        
        target = new File(tmpDir, "zipToTargzAlt.tar.gz");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.tar.gz"),
                new File(resourceDir, "distribution"),
                "MyAltDir");
        packager.execute();
        assert(target.exists());
    }

    @Test
    public void zipToZip() throws Exception {
        File target = new File(tmpDir, "zipToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.zip"),
                new File(resourceDir, "distribution"),
                null);
        packager.execute();
        assert(target.exists());
        
        target = new File(tmpDir, "zipToZipAlt.zip");
        packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "test.zip"),
                new File(resourceDir, "distribution"),
                "myAltDir");
        packager.execute();
        assert(target.exists());
    }
    
    @Test
    public void dirToZip() throws Exception {
        File target = new File(tmpDir, "dirToZip.zip");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "tmp"),
                new File(resourceDir, "distribution"),
                null);
        packager.execute();
        assert(target.exists());
    }
    
    @Test
    public void dirToTargz() throws Exception {
        File target = new File(tmpDir, "dirToTargz.tar.gz");
        GridlibPackageMojo packager = new GridlibPackageMojo(target, 
                new File(resourceDir, "tmp"),
                new File(resourceDir, "distribution"),
                null);
        packager.execute();
        assert(target.exists());
    }
}