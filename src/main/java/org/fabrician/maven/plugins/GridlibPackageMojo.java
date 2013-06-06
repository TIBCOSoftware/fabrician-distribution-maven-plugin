/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 *
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code.
 * In most instances, the license terms are contained in a file named license.txt.
 */
package org.fabrician.maven.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Packages a grid libary given the grid-library.xml resource directory and 3rd party directory or zip/tar.gz.
 *
 * @goal package
 * 
 * @phase package
 */
public class GridlibPackageMojo extends AbstractMojo {
    
    /**
    * The target name of the distribution grid library.  The extension must be tar.gz or zip.
    * @required
    * @parameter
    *   expression="${distroFilename}"
    *   default-value=""
    */
    private File distroFilename;
    
    /**
    * The file or directory that contains the software to bundle.
    * @required
    * @parameter
    *   expression="${distroSource}"
    *   default-value=""
    */
    private File distroSource;

    /**
    * The directory that contains the grid library resources such as a grid-library.xml.
    * @parameter
    *   expression="${distroResources}"
    *   default-value="src/main/resources/distribution"
    */
    private File distroResources;
    
    /**
    * The alternate root directory name in the resulting grid library.  Useful when the software zip or tar.gz basedir changes
    * across versions. 
    * For example, a.zip:foo/bar/x.html -> b.tar.gz:myAltDir/bar/x.html when distroAlternateRootDirectory is set to myAltDir.
    * @parameter
    *   default-value=""
    */
    private String distroAlternateRootDirectory;

    /**
    * Whether to apply resource filter to the distroResources file set.
    * @parameter
    *   default-value="true"
    */
    private boolean filtered = true;
    
    /**
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;
   
    public GridlibPackageMojo() {}
    
    // for tests only
    public GridlibPackageMojo(File distroFilename, File distroSource, File distroResources, String distroAlternateRootDirectory, MavenProject project) {
        this.distroFilename = distroFilename;
        this.distroSource = distroSource;
        this.distroResources = distroResources;
        this.distroAlternateRootDirectory = distroAlternateRootDirectory;
        this.project = project;
    }
    
    public void execute() throws MojoExecutionException {
        if (!distroSource.exists()) {
            throw new MojoExecutionException(distroSource + " does not exist");
        }

        if (CompressUtils.isZip(distroFilename)) {
            createZip();
        } else if (CompressUtils.isTargz(distroFilename)) {
            createTar();
        } else {
            throw new MojoExecutionException("unsupported grid library extension: " + distroFilename);
        }
    }
    
    private void createZip() throws MojoExecutionException {
        distroFilename.getParentFile().mkdirs();
        ZipArchiveOutputStream out = null;
        try {
            out = new ZipArchiveOutputStream(distroFilename);
            if (distroSource.isDirectory()) {
                CompressUtils.copyDirToArchiveOutputStream(distroSource, out);
            } else if (CompressUtils.isZip(distroSource)) {
                CompressUtils.copyZipToArchiveOutputStream(distroSource, out, distroAlternateRootDirectory);
            } else if (CompressUtils.isTargz(distroSource)) {
                CompressUtils.copyTargzToArchiveOutputStream(distroSource, out, distroAlternateRootDirectory);
            } else {
                throw new MojoExecutionException("Unspported source type: " + distroSource);
            }
            if (distroResources != null && !"".equals(distroResources)) {
                if (filtered) {
                    CompressUtils.copyFilteredDirToArchiveOutputStream(distroResources, getFilterProperties(), out);
                } else {
                    CompressUtils.copyDirToArchiveOutputStream(distroResources, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage());
        } finally {
            CompressUtils.close(out);
        }
    }
    
    private void createTar() throws MojoExecutionException {
        distroFilename.getParentFile().mkdirs();
        FileOutputStream out = null;
        CompressorOutputStream cout = null;
        TarArchiveOutputStream tout = null;
        try {
            out = new FileOutputStream(distroFilename);
            cout = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, out);
            tout = new TarArchiveOutputStream(cout);
            tout.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            if (distroSource.isDirectory()) {
                CompressUtils.copyDirToArchiveOutputStream(distroSource, tout);
            } else if (CompressUtils.isZip(distroSource)) {
                CompressUtils.copyZipToArchiveOutputStream(distroSource, tout, distroAlternateRootDirectory);
            } else if (CompressUtils.isTargz(distroSource)) {
                CompressUtils.copyTargzToArchiveOutputStream(distroSource, tout, distroAlternateRootDirectory);
            } else {
                throw new MojoExecutionException("Unspported source type: " + distroSource);
            }
            if (distroResources != null && !"".equals(distroResources)) {
                if (filtered) {
                    CompressUtils.copyFilteredDirToArchiveOutputStream(distroResources, getFilterProperties(), tout);
                } else {
                    CompressUtils.copyDirToArchiveOutputStream(distroResources, tout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException(e.getMessage());
        } finally {
            CompressUtils.close(tout);
            CompressUtils.close(cout);
            CompressUtils.close(out);
        }
    }
    
    private Properties getFilterProperties() {
        Properties props = null;
        // just the user supplied for now
        if (project != null && filtered) {
            props = project.getProperties();
        }
        return props;
    }
}
