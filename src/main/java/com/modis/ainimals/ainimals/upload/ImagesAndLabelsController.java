package com.modis.ainimals.ainimals.upload;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
				logger.info("-- Nouveau fichiers ---- : labels-origin.txt");
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
		File image = new File(getnextImage(1)); 
		modelView.addObject("photo", image.getName());
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
		BufferedWriter bufWriter = null;
        FileWriter fileWriter = null;
        int lineNumberEntropy = 0;
        int lineNumberLabel = 0;
        
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
		
		// ajout du label saisi par l'utilisateur 
		 try {
			if (!Files.exists(Paths.get(context.getRealPath("shared") + File.separator + "label.txt"))) { 
	            File newFile = new File(Paths.get(context.getRealPath("shared")) + File.separator + "label.txt");
	            newFile.createNewFile();
	        }
	        BufferedReader file = new BufferedReader(new FileReader(context.getRealPath("shared") + File.separator + "label.txt"));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        while ((line = file.readLine()) != null) { 
	            inputBuffer.append(line);
	            inputBuffer.append('\n');
	        }
	        // ajout du nouveau label
	        inputBuffer.append(mapLabelNumber.get(sSubmit)); 
	        file.close();
	        String inputStr = inputBuffer.toString(); 
	        FileOutputStream fileOut = new FileOutputStream(context.getRealPath("shared") + File.separator + "label.txt");
	        fileOut.write(inputStr.getBytes());
	        fileOut.close(); 

	    } catch (Exception e) {
	        System.out.println("Problem reading file.");
	    }
        
     // Si egal alors plus d'images à labeliser
        lineNumberLabel = countLinesLabels();
        lineNumberEntropy = countLinesEntropy(); 
		if (getnextImage(lineNumberLabel+1)==null) {
			// lancer le python labels.py
			try {
				String sScript = context.getRealPath("scripts") + File.separator + "label.py"; 
				logger.info("-- ImagesAndLabelsController.updateLabel() - START - PythonUtil.execScript label.py ");
				PythonUtil.execScript(sScript, null, -1);
				logger.info("-- ImagesAndLabelsController.updateLabel() - END -  PythonUtil.execScript label.py ");
				int lineNumberPool = countLinesPool(); 
				if (lineNumberPool==0) { // fin des labels
					ModelAndView modelView = new ModelAndView("end"); 
					return modelView;
				}
				
				sScript = context.getRealPath("scripts") + File.separator + "entropy.py"; 
				logger.info("-- ImagesAndLabelsController.updateLabel() - START - PythonUtil.execScript entropy.py ");
				PythonUtil.execScript(sScript, null, lineNumberLabel); 
				logger.info("-- ImagesAndLabelsController.updateLabel() - END -  PythonUtil.execScript entropy.py ");
				
				// reset du fichier label
				File newFile = new File(Paths.get(context.getRealPath("shared")) + File.separator + "label.txt");
	            newFile.delete();
	            newFile.createNewFile(); 
	            
		        lineNumberLabel = countLinesLabels();
		        lineNumberEntropy = countLinesEntropy(); 
		        lineNumberPool = countLinesPool(); 
				if (lineNumberPool==0) { // fin des labels
					ModelAndView modelView = new ModelAndView("end"); 
					return modelView;
				} else { // faire une boucle sur les nouvelles images à labeliser
					ModelAndView modelView = new ModelAndView("updateLabel");
					File image = new File(getnextImage(lineNumberLabel+1));
					modelView.addObject("labels", listLabels);
					modelView.addObject("photo", image.getName()); 
					return modelView;
				}
			} catch (Exception e) {
				logger.error("-- ImagesAndLabelsController.updateLabel() failed ", e); 
			}  
		}
		// il reste des images a labeliser dans le fichier 
		int lineNumberPool = countLinesPool(); 
		if (lineNumberPool==0) { // fin des labels
			ModelAndView modelView = new ModelAndView("end"); 
			return modelView;
		} else { // faire une boucle sur les nouvelles images à labeliser
			ModelAndView modelView = new ModelAndView("updateLabel");
			File image = new File(getnextImage(lineNumberLabel+1));
			modelView.addObject("labels", listLabels);
			modelView.addObject("photo", image.getName()); 
			return modelView;
		} 
	}

	private int countLinesLabels() {
		int lineNumberLabel=0;
		Scanner fileScanner=null;
		try { 
	        // compte les ligne d'entropy
	        File f = new File(context.getRealPath("shared") + File.separator + "label.txt");
	        fileScanner = new Scanner(f); 
	        while(fileScanner.hasNextLine()){
	            fileScanner.nextLine();
	            lineNumberLabel++;
	        } 
	        fileScanner.close();
        } catch (IOException ex) {
        	logger.error("-- ImagesAndLabelsController.updateLabel() failed ", ex);
        }
		return lineNumberLabel;
	}
	
	private int countLinesEntropy() {
		int lineNumberEntropy =-1; // commencer à -1 car la première ligne ne compte pas
		try {
            // compte les ligne d'entropy
			BufferedReader file = new BufferedReader(new FileReader(context.getRealPath("shared") + File.separator + "label.txt")); 
	        String line; 
	        while ((line = file.readLine()) != null) { 
	        	lineNumberEntropy++;
	        } 
	        file.close(); 
        } catch (IOException ex) {
        	logger.error("-- ImagesAndLabelsController.updateLabel() failed ", ex);
        } 
		return lineNumberEntropy;
	}
	
	private int countLinesPool() {
		int lineNumberEntropy =-1; // commencer à -1 car la première ligne ne compte pas
		try {
            // compte les ligne d'entropy
			BufferedReader file = new BufferedReader(new FileReader(context.getRealPath("shared") + File.separator + "pool.csv")); 
	        String line; 
	        while ((line = file.readLine()) != null) { 
	        	lineNumberEntropy++;
	        } 
	        file.close(); 
        } catch (IOException ex) {
        	logger.error("-- ImagesAndLabelsController.updateLabel() failed ", ex);
        } 
		return lineNumberEntropy;
	}
	
	private String getnextImage(int intLine) {
		int lineNumberEntropy =-1; // commencer à -1 car la première ligne ne compte pas
		String sNextImage=null;
		BufferedReader reader=null;
		try { 
			reader = new BufferedReader(new FileReader(context.getRealPath("shared") + File.separator +  "topEntropy.txt"));
		    String line;
		    int nbLine=0;
		    while ((line = reader.readLine()) != null) {
		    	lineNumberEntropy++;
	        	if(lineNumberEntropy==intLine) {
	        		return line;
	        	}
		    }   
        } catch (IOException ex) {
        	logger.error("-- ImagesAndLabelsController.updateLabel() failed ", ex);
        }
		finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) { 
					logger.error("-- ImagesAndLabelsController.updateLabel() failed ", e);
				}
		}
		return sNextImage;
	}
	
	
}

