/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 *
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code.
 * In most instances, the license terms are contained in a file named license.txt.
 */
package org.fabrician.maven.plugins;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.utils.io.DirectoryScanner;

/**
 * Goal that uploads a grid library to a Silver Fabric Broker
 *
 * @goal upload
 * 
 * @phase install
 */
public class GridlibUploadMojo extends AbstractMojo {
    
    /**
    * Silver Fabric Broker URL
    * @parameter
    *   expression="${brokerUrl}"
    *   default-value="http://localhost:8000/livecluster/gridlibs/archives"
    */
    private String brokerUrl;

    /**
    * Silver Fabric Broker username
    * @parameter
    *   expression="${brokerUsername}"
    *   default-value="admin"
    */
    private String brokerUsername;

    /**
    * Silver Fabric Broker password
    * @parameter
    *   expression="${brokerPassword}"
    *   default-value="admin"
    */
    private String brokerPassword;

    /**
    * Which server credentials to use from the user's settings.xml.  This hasn't been implemented.
    * @parameter
    *   default-value=""
    */
    private String serverId;
    
    /**
    * Overwrite if exists
    * @parameter
    *   expression="${gridlibOverwrite}"
    *   default-value="false"
    */
    private boolean gridlibOverwrite;

    /**
    * A set of file patterns to include in the upload.
    * @parameter alias="includes"
    */
    private String[] mIncludes;

    /**
    * A set of file patterns to exclude from the upload.
    * @parameter alias="excludes"
    */
    private String[] mExcludes;

    public void setExcludes(String[] excludes) { 
        mExcludes = excludes; 
    }
    public void setIncludes(String[] includes) { 
        mIncludes = includes;
    }
    
    public void execute() throws MojoExecutionException {
        String username = brokerUsername;
        String password = brokerPassword;
        if (serverId != null && !"".equals(serverId)) {
            // TODO: implement server_id
            throw new MojoExecutionException("serverId is not yet supported");
        } else if (brokerUsername == null || "".equals(brokerUsername) || brokerPassword == null || "".equals(brokerPassword)) {
            throw new MojoExecutionException("serverId or brokerUsername and brokerPassword must be set");
        }
        
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(mIncludes);
        ds.setExcludes(mExcludes);
        ds.setBasedir(".");
        ds.setCaseSensitive(true);
        ds.scan();
        
        String[] files = ds.getIncludedFiles();
        if (files == null) {
            getLog().info("No files found to upload");  
        } else {
            getLog().info("Found " + files.length + " file to upload");
            
            for (String file : files) {
                File gridlibFilename = new File(file);
                getLog().info("Uploading " + gridlibFilename + " to " + brokerUrl);
                
                DefaultHttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                Credentials cred = new UsernamePasswordCredentials(username, password);
                httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
                
                try {
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entity.addPart("gridlibOverwrite", new StringBody(String.valueOf(gridlibOverwrite), Charset.forName("UTF-8")));
                    entity.addPart("gridlibArchive", new FileBody(gridlibFilename));
        
                    HttpPost method = new HttpPost(brokerUrl);
                    method.setEntity(entity);
                    
                    HttpResponse response = httpclient.execute(method);
                    StatusLine status = response.getStatusLine();
                    int code = status.getStatusCode();
                    if (code >= 400 && code <= 500) {
                        throw new MojoExecutionException("Failed to upload " + gridlibFilename + " to " + brokerUrl + ": " + code);
                    }
                } catch (Exception e) {
                    throw new MojoExecutionException(e.getMessage());
                }  
            }
        }
    }
}
