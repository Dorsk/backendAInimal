package com.modis.ainimals.ainimals.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletContext;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.modis.ainimals.ainimals.utils.FileUploadUtil;
import com.modis.ainimals.ainimals.utils.PythonUtil;

@RestController
@ComponentScan({"com.modis.ainimals.ainimals.load"})
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
	        
	        // recuperation des images et sauvegarder dans un dossier de partage
			for (MultipartFile multipartFile : multipartFiles) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		        try {
		        	logger.info("-- Image name ---- : " + fileName);
					FileUploadUtil.saveImagesFile(uploadDir, fileName, multipartFile);
				} catch (IOException e) {
					logger.error("-- FileUploadUtil.saveFile() failed ", e);
				}
			} 
			
			// creer fichier txt des labels
			
			try {
				FileUploadUtil.saveLabelsFile(uploadDir, "labels-origin.txt", listLabels);
				logger.info("-- File name ---- : labels-origin.txt");
			} catch (IOException e) {
				logger.error("-- FileUploadUtil.saveLabelsFile() failed ", e);
			}
			
			// lancer le script python
			String sScript = context.getRealPath("scripts");
			sScript = sScript + File.separator + "csv_creation.py";
			logger.info("-- Préparation script ---- : " + sScript);
			try {
				PythonUtil.execScript(sScript, uploadDir);
			} catch (IOException e) {
				logger.error("-- PythonUtil.execScript() failed ", e);
			} catch (InterruptedException e) {
				logger.error("-- PythonUtil.execScript() failed ", e);
			}
			logger.info("-- Fin d'execution du script ---- : " + sScript);
		}
		 // redirection vers une autre page web pour choisir les labels
		ModelAndView modelView = new ModelAndView("loading");
		modelView.addObject("lables", listLabels);
		modelView.addObject("photo", multipartFiles[0].getOriginalFilename());
		return modelView;
	}
	
	
	/**   
	 * @return 
	 * @return 
	 * @return loading.html
	 */
	@GetMapping("/loading")
	public void uploadImage() {
		  
	}
	
}

