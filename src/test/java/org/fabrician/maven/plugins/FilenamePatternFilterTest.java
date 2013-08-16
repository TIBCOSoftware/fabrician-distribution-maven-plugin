package org.fabrician.maven.plugins;

import org.junit.Assert;
import org.junit.Test;

public class FilenamePatternFilterTest {

    @Test
    public void simplePatternTests() throws Exception {
        String filename = "foo\\bar\\baz.java"; 

        Assert.assertFalse(isAllowed(filename, "**\\*", "**\\*.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*.java", "**\\*.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*.job", "**\\*.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*.job", "**\\*.job"));
        Assert.assertFalse(isAllowed(filename, null,  "**/*.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*",  "foo\\**\\*.java"));
        Assert.assertFalse(isAllowed(filename, null,  "foo\\bar\\*.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*",  "foo\\bar\\baz.*"));
        Assert.assertFalse(isAllowed(filename, null,  "foo\\bar\\baz.java"));
        Assert.assertFalse(isAllowed(filename, "**\\*",  "**\\baz.java"));

        Assert.assertTrue(isAllowed(filename, null, null));
        Assert.assertTrue(isAllowed(filename, "**\\*", null));
        Assert.assertTrue(isAllowed(filename, null,  "**\\*.jav"));
        Assert.assertTrue(isAllowed(filename, "**\\*.java",  "fo\\**\\*.jav"));
        Assert.assertTrue(isAllowed(filename, "**\\*",  "fo\\**\\*.jav"));
        Assert.assertTrue(isAllowed(filename, null,  "**\\ba.java")); 
    }
        
    private boolean isAllowed(String filename, String include, String exclude) {
        String[] includes = include == null ? null : new String[] { include };
        String[] excludes = exclude == null ? null : new String[] { exclude };
        FilenamePatternFilter filter = new FilenamePatternFilter(includes, excludes);
        boolean result = filter.accept(filename);
        System.out.println(filename + "[excludes=" + exclude +"] " + result);
        return result;
    }
}
