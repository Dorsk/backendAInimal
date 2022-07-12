package com.modis.ainimals.ainimals.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView; 

import com.modis.ainimals.ainimals.utils.FileUploadUtil;
import com.modis.ainimals.ainimals.utils.PythonUtil;

@RestController
@ComponentScan({"com.modis.ainimals.ainimals.upload"})
public class ImagesAndLabelsController {

	@Autowired
	ServletContext context;
	 
	Logger logger = LoggerFactory.getLogger(ImagesAndLabelsController.class);
	 
	/** 
	 * récupération des images 
	 * TODO : recuperer les libelles
	 * @param multipartFiles : images à upload
	 * @param multiple labels :  il peut y avoir de 2 à 10 labels
	 * @return 
	 */
	@PostMapping("/upload")
	public ModelAndView uploadImage(@RequestParam("files") MultipartFile [] multipartFiles,
			@RequestParam("label1") String sLabel1, @RequestParam("label2") String sLabel2, @RequestParam("label3") String sLabel3,
			@RequestParam("label4") String sLabel4, @RequestParam("label5") String sLabel5, @RequestParam("label6") String sLabel6,
			@RequestParam("label7") String sLabel7, @RequestParam("label8") String sLabel8, @RequestParam("label9") String sLabel9,
			@RequestParam("label10") String sLabel10) {
		
		// check labels utilisables
		List <String> listLabels = new ArrayList<>();
    	if(sLabel1!= null && !sLabel1.isEmpty() && !sLabel1.isBlank())
    		listLabels.add(sLabel1);
    	if(sLabel2!= null && !sLabel2.isEmpty() && !sLabel2.isBlank())
    		listLabels.add(sLabel2);
    	if(sLabel3!= null && !sLabel3.isEmpty() && !sLabel3.isBlank())
    		listLabels.add(sLabel3);
    	if(sLabel4!= null && !sLabel4.isEmpty() && !sLabel4.isBlank())
    		listLabels.add(sLabel4);
    	if(sLabel5!= null && !sLabel5.isEmpty() && !sLabel5.isBlank())
    		listLabels.add(sLabel5);
    	if(sLabel6!= null && !sLabel6.isEmpty() && !sLabel6.isBlank())
    		listLabels.add(sLabel6);
    	if(sLabel7!= null && !sLabel7.isEmpty() && !sLabel7.isBlank())
    		listLabels.add(sLabel7);
    	if(sLabel8!= null && !sLabel8.isEmpty() && !sLabel8.isBlank())
    		listLabels.add(sLabel8);
    	if(sLabel9!= null && !sLabel9.isEmpty() && !sLabel9.isBlank())
    		listLabels.add(sLabel9);
    	if(sLabel10!= null && !sLabel10.isEmpty() && !sLabel10.isBlank())
    		listLabels.add(sLabel10);
    	
		// bloquage si 0 image à upload
		if(multipartFiles.length > 1 || (multipartFiles.length == 1 && !multipartFiles[0].getOriginalFilename().equals(""))) {
			// recuperation du dossier de partage pour les images qui sont upload
	        String uploadDir = context.getRealPath("shared");
	        String uploadDirImages = context.getRealPath("shared")+ File.separator + "img";
	        // recuperation des images et sauvegarder dans un dossier de partage
			for (MultipartFile multipartFile : multipartFiles) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		        try {
		        	logger.info("-- Image name ---- : " + fileName);
					FileUploadUtil.saveImagesFile(uploadDirImages, fileName, multipartFile);
				} catch (IOException e) {
					logger.error("-- FileUploadUtil.saveFile() failed ", e);
				}
			} 
			
			// creer fichier txt des labels
			try {
				FileUploadUtil.saveLabelsFile(uploadDir, "labels-origin.txt", listLabels);
				logger.info("-- Nouveau fichiers ---- : labels-origin.txt  et labels.txt");
			} catch (IOException e) {
				logger.error("-- FileUploadUtil.saveLabelsFile() failed ", e);
			}
			
			// lancer les scripts python 
			logger.info("-- Préparation scripts ---- ");
			try {
				String sScript = context.getRealPath("scripts");
				sScript = sScript + File.separator + "csv_creation.py"; 
				PythonUtil.execScript(sScript,uploadDirImages, -1);
				
				sScript = context.getRealPath("scripts");
				String sScript2 = sScript + File.separator + "entropy.py";
				PythonUtil.execScript(sScript2, null, listLabels.size());
			} catch (IOException e) {
				logger.error("-- PythonUtil.execScript() failed csv_creation.py ", e);
			} catch (InterruptedException e) {
				logger.error("-- PythonUtil.execScript() failed entropy.py ", e);
			}
			logger.info("-- Fin d'execution des scripts ---- ");
		}
		 // redirection vers une autre page web pour choisir les labels
		ModelAndView modelView = new ModelAndView("updateLabel");
		modelView.addObject("labels", listLabels);
		modelView.addObject("photo", multipartFiles[0].getOriginalFilename());
		return modelView;
	}
	 
	/**   
	 * @return  
	 */
	@GetMapping("/updateLabel")
	public ModelAndView getUpdateLabel(@RequestParam("photo") String sPhoto, @RequestParam("submit") String sSubmit) {
		List <String> listLabels = new ArrayList<>();
		Map<String, Integer> mapLabelNumber = new HashMap<>();
		String sFilePath = context.getRealPath("shared") + File.separator + "labels-origin.txt";
		String sFilePathNumber = context.getRealPath("shared") + File.separator + "topEntropy.txt";
		// update fichier img;label
		
		// recuperer la liste des labels ayant était saisie par l'utilisateur la première fois  
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(sFilePath));
		    String line;
		    int nbLine=0;
		    while ((line = reader.readLine()) != null) {
		    	listLabels.add(line);
		    	mapLabelNumber.put(line, nbLine);
		    	nbLine++;
		    }  
			reader.close();
		} catch (IOException e) {
			logger.error("-- ImagesAndLabelsController.updateLabel() failed ", e);
		}
		
	    // TODO : ajouter le label au fichier labels.txt et trouve la prochaine image
		 try {
		        // input the file content to the StringBuffer "input"
		        BufferedReader file = new BufferedReader(new FileReader(sFilePathNumber));
		        StringBuffer inputBuffer = new StringBuffer();
		        String line;

		        while ((line = file.readLine()) != null) {
		        	if(line.contains(sPhoto)) {
		        		line = line + mapLabelNumber.get(sSubmit);
		        	}
		            inputBuffer.append(line);
		            inputBuffer.append('\n');
		        }
		        file.close();
		        String inputStr = inputBuffer.toString();
    
		        // write the new string with the replaced line OVER the same file
		        FileOutputStream fileOut = new FileOutputStream(sFilePathNumber);
		        fileOut.write(inputStr.getBytes());
		        fileOut.close();

		    } catch (Exception e) {
		        System.out.println("Problem reading file.");
		    }
		
		// charger une autre image avec les labels si besoin
		ModelAndView modelView = new ModelAndView("loading");
		modelView.addObject("labels", listLabels);
//		modelView.addObject("photo", photo); // TODO un nouveau chemin lu dans le fichier labels.txt sans label
		return modelView;
	}

}

