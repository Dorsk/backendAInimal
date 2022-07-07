package com.modis.ainimals.ainimals.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	/**
	 *  sauvegarde les images dans un dossier
	 *  
	 * @param uploadDir
	 * @param fileName
	 * @param multipartFile
	 * @throws IOException
	 */
	public static void saveFile(String uploadDir, String fileName,
            MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException("Could not save image file: " + fileName, ioe);
        }      
    }

	public static void saveLabelsFile(String uploadDir, String sFileName, List<String> listLabels) throws IOException {
		 
		Path uploadPath = Paths.get(uploadDir);
		 
		if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
		FileWriter fw = new FileWriter(uploadDir + File.separator + sFileName);
		 
		for (String label : listLabels) {
			fw.write(label);
		}
		fw.close();
	}
	
}
