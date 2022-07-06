package com.modis.ainimals.ainimals.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class PythonUtil {

	@Autowired
	static ServletContext context;
	
	/**
	 *  Lancement de script Python
	 * @param sScriptName
	 * @param uploadDir
	 * @param fileName
	 * @param multipartFile
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int execScript(String sScriptName, String sUploadDir, String sFileName,
            MultipartFile multipartFile) throws IOException, InterruptedException {
		
		ProcessBuilder processBuilder = new ProcessBuilder("python", getPythonScriptPath(sScriptName));
	    processBuilder.redirectErrorStream(true);
	    Process process = processBuilder.start();
	    int intCode = process.waitFor();
	    // lecture du output du script
	    BufferedReader bfr = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    while ((line = bfr.readLine()) != null) {
	    	// TODO Logger
	    	System.out.println(line);
	    }   
	    intCode = process.waitFor();
		return intCode;
    }

	private static String getPythonScriptPath(String sScriptName) {
		   
		return context.getRealPath("/") + File.separator + "python" + File.separator + sScriptName;
	}
	
}
