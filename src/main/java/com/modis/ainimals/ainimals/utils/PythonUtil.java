package com.modis.ainimals.ainimals.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.modis.ainimals.ainimals.upload.ImagesAndLabelsController;

public class PythonUtil {

	@Autowired
	static ServletContext context;
	static Logger logger = LoggerFactory.getLogger(ImagesAndLabelsController.class);
	
	/**
	 *  Lancement de script Python
	 * @param sScriptName
	 * @param uploadDir
	 * @param fileName
	 * @param multipartFile
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int execScript(String sScriptName, String sUploadDir) throws IOException, InterruptedException {
		
		 Process process = null;
	     try{
	             process = Runtime.getRuntime().exec("python "+ sScriptName + " " + sUploadDir);
	     }catch(Exception e) {
	    	// TODO Logger
	        System.out.println("Exception Raised" + e.toString());
	     }
	     InputStream stdout = process.getInputStream();
	     BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
	 
	    int intCode = process.waitFor();
	    // lecture du output du script
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	    	logger.info("-- Output Python | " + line); 
	    }   
	    intCode = process.waitFor();
		return intCode;
    }
}
