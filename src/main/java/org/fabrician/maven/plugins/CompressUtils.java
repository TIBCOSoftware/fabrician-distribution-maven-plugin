/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 *
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code.
 * In most instances, the license terms are contained in a file named license.txt.
 */
package org.fabrician.maven.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public class CompressUtils {

    public static boolean isZip(File f) {
        return f.getName().endsWith(".zip");
    }

    public static boolean isTargz(File f) {
        return f.getName().endsWith("tar.gz");
    }

    public static void copyZipToArchiveOutputStream(File zipSrc, ArchiveOutputStream out, String alternateBaseDir) throws IOException {
        copyZipToArchiveOutputStream(zipSrc, out, alternateBaseDir);
    }
    public static void copyZipToArchiveOutputStream(File zipSrc, FilenamePatternFilter filter, ArchiveOutputStream out, String alternateBaseDir) throws IOException {
        ZipFile zip = new ZipFile(zipSrc);
        for (Enumeration<ZipArchiveEntry> zipEnum = zip.getEntries(); zipEnum.hasMoreElements(); ) {
            ZipArchiveEntry source = zipEnum.nextElement();
            if (filter != null && !filter.accept(source.getName())) {
                System.out.print("Excluding " + source.getName());
                continue;
            }
            InputStream in = null;
            try {
                in = zip.getInputStream(source);
                out.putArchiveEntry(createArchiveEntry(source, out, alternateBaseDir));
                IOUtils.copy(in, out);
                out.closeArchiveEntry();
            } finally {
                close(in);
            }
        }
    }

    public static void copyTargzToArchiveOutputStream(File targzSrc, ArchiveOutputStream out, String alternateBaseDir) throws IOException {
        copyTargzToArchiveOutputStream(targzSrc, null, out, alternateBaseDir);
    }
    public static void copyTargzToArchiveOutputStream(File targzSrc, FilenamePatternFilter filter, ArchiveOutputStream out, String alternateBaseDir) throws IOException {
        FileInputStream fin = null;
        CompressorInputStream zipIn = null;
        TarArchiveInputStream tarIn = null;
        try {
            fin = new FileInputStream(targzSrc);
            zipIn = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, fin);
            tarIn = new TarArchiveInputStream(zipIn);
            TarArchiveEntry entry = tarIn.getNextTarEntry();
            while (entry != null) {
                if (filter != null && !filter.accept(entry.getName())) {
                    System.out.print("Excluding " + entry.getName());
                } else {
                    out.putArchiveEntry(createArchiveEntry(entry, out, alternateBaseDir));
                    IOUtils.copy(tarIn, out);
                    out.closeArchiveEntry();
                }
                entry = tarIn.getNextTarEntry();
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            close(zipIn);
            close(tarIn);
            close(fin);
        }
    }
    
    /**
     * This attempts to mimic the filtering in the maven-assembly-plugin at a basic level.  It wasn't clear how to use the archivers directly 
     * in that plugin.
     * Supports ${x} only.
     */
    public static void copyFilteredDirToArchiveOutputStream(File baseDir, Properties replacements, ArchiveOutputStream out) throws IOException {
        File[] files = baseDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String contents = transform(file, replacements);
                ByteArrayInputStream in = new ByteArrayInputStream(contents.getBytes());
                ArchiveEntry entry = null;
                if (out instanceof TarArchiveOutputStream) {
                    entry = new TarArchiveEntry(file.getName());
                    ((TarArchiveEntry) entry).setSize(contents.getBytes().length);
                    ((TarArchiveEntry) entry).setModTime(file.lastModified());
                } else {
                    entry = new ZipArchiveEntry(file.getName());
                    ((ZipArchiveEntry)entry).setSize(contents.getBytes().length);
                    ((ZipArchiveEntry)entry).setTime(file.lastModified());
                }
                out.putArchiveEntry(entry);
                IOUtils.copy(in, out);
                out.closeArchiveEntry();
            }
        }
    }
        
    private static String transform(File f, Properties p) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(f);
            IOUtils.copy(fis, baos);
        } finally {
            close(fis);
        }
        String contents = new String(baos.toByteArray());
        for (Entry<Object, Object> entry : p.entrySet()) {
            contents = contents.replaceAll("\\$\\{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return contents;
    }

    public static void copyDirToArchiveOutputStream(File baseDir, ArchiveOutputStream out) throws IOException {
        copyDirToArchiveOutputStream(baseDir, null, out);
    }
    public static void copyDirToArchiveOutputStream(File baseDir, FilenamePatternFilter filter, ArchiveOutputStream out) throws IOException {
        File[] files = baseDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (filter != null && !filter.accept(file.toString())) {
                    System.out.print("Excluding " + file);
                    continue;
                }
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    out.putArchiveEntry(createArchiveEntry(file, out));
                    IOUtils.copy(fis, out);
                    out.closeArchiveEntry();
                } finally {
                    close(fis);
                }
            }
        }
    }
    
    public static void close(OutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void close(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArchiveEntry createArchiveEntry(ArchiveEntry entry, OutputStream out, String alternateBaseDir) throws IOException {
        String substitutedName = substituteAlternateBaseDir(entry, alternateBaseDir);
        if (out instanceof TarArchiveOutputStream) {
            TarArchiveEntry newEntry = new TarArchiveEntry(substitutedName);
            newEntry.setSize(entry.getSize());
            newEntry.setModTime(entry.getLastModifiedDate());

            if (entry instanceof TarArchiveEntry) {
                TarArchiveEntry old = (TarArchiveEntry)entry;
                newEntry.setSize(old.getSize());
                newEntry.setIds(old.getUserId(), old.getGroupId());
                newEntry.setNames(old.getUserName(), old.getGroupName());
            }
            return newEntry;
        } else if (entry instanceof ZipArchiveEntry) {
            ZipArchiveEntry old = (ZipArchiveEntry)entry;
            ZipArchiveEntry zip = new ZipArchiveEntry(substitutedName);
            zip.setInternalAttributes(old.getInternalAttributes());
            zip.setExternalAttributes(old.getExternalAttributes());
            zip.setExtraFields(old.getExtraFields(true));
            return zip;
        } else {
            return new ZipArchiveEntry(substitutedName);
        }
    }
    
    private static ArchiveEntry createArchiveEntry(File f, OutputStream out) {
        if (out instanceof TarArchiveOutputStream) {
            return new TarArchiveEntry(f, f.getName());
        } else {
            return new ZipArchiveEntry(f, f.getName());
        }
    }
    
    private static String substituteAlternateBaseDir(ArchiveEntry entry, String alternateBaseDir) {
        String name = entry.getName();
        if (alternateBaseDir == null || "".equals(alternateBaseDir)) {
            return name;
        }
        String[] dirs = name.split("\\/");
        String newName = name.replaceFirst(dirs[0], alternateBaseDir);
        
        if (dirs.length < 2) {
            if (entry.isDirectory()) {
                return newName;
            } else {
                return name;
            }
        } else {
            return newName;
        }
    }
}
