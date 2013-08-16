package org.fabrician.maven.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Similar to Ant's DirectoryScanner but performed on String filenames
// Supports: **\*.jar syntax
public class FilenamePatternFilter {
    
    private List<FilePattern> includes = null, excludes = null;
    
    
    public FilenamePatternFilter(String[] stringIncludes, String[] stringExcludes) {
        includes = createPatterns(stringIncludes);
        excludes = createPatterns(stringExcludes);
    }
    
    public boolean accept(String filename) {
        boolean included = includes == null || matches(includes, filename);
        if (included) {
            return excludes == null || !matches(excludes, filename);
        } else {
            return false;
        }
    }
    
    private List<FilePattern> createPatterns(String[] patterns) {
        if (patterns != null && patterns.length > 0) {
            List<FilePattern> p = new ArrayList<FilePattern>();
            for (String pattern : patterns) {
                p.add(new FilePattern(pattern));
            }
            return p;
        } else {
            return null;
        }   
    }
    
    private boolean matches(List<FilePattern> patterns, String filename) {
        for (FilePattern pattern : patterns) {
            if (pattern.matches(filename)) {
                return true;
            }
        }
        return false;
    }

    // Quick and dirty conversion from an Ant DirectoryScanner into simple regex
    private class FilePattern {
        private static final String WILDCARD = "*";
        private static final String D_WILDCARD = "**";
        private static final String WILDCARD_PLACEHOLDER = "WILDCARD_PLACEHOLDER";
        
        private String pattern;
        
        FilePattern(String str) {
            pattern = str.trim();
            pattern = pattern.replace('\\', '/');
            if (pattern.endsWith("/")) {
                pattern += D_WILDCARD;
            }
            
            // **/..    => (.*/)*..
            // ../**/.. => ../(.*/)*..
            pattern = pattern.replace(D_WILDCARD + "/", "(." + WILDCARD_PLACEHOLDER + "/)" + WILDCARD_PLACEHOLDER);
            // ../**    => ../.*
            pattern = pattern.replace("/" + D_WILDCARD, "/." + WILDCARD_PLACEHOLDER);
            // ../a*/.. => ../a.*/..
            pattern = pattern.replace(WILDCARD, "." + WILDCARD_PLACEHOLDER);
            pattern = pattern.replace(WILDCARD_PLACEHOLDER, WILDCARD);
            
            System.out.println("Final regex pattern is " + pattern);
        }
        
        boolean matches(String name) {
            return Pattern.matches(pattern, name.replace('\\', '/'));
        }
    }
}
