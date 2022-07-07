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
import org.springframework.web.servlet.view.RedirectView;

import com.modis.ainimals.ainimals.utils.FileUploadUtil;

@RestController
@ComponentScan({"com.modis.ainimals.ainimals.load"})
public class ImagesAndLabelsController {

	private final AtomicLong counter = new AtomicLong();

	@Autowired
	ServletContext context;
	/**
	 * récupération des labels 
	 * 
	 * @param label
	 * @return
	 */
	@RequestMapping(value = "/loadlabels", method = RequestMethod.GET)
	public ImagesAndLabelsData getlables(@RequestParam(value = "labels") String labels) {
		
		return new ImagesAndLabelsData(counter.incrementAndGet(),  labels);
	}
	
	
	/**
	 * Home page 
	 * récupération des images 
	 * TODO : recuperer les libelles
	 * @param multipartFiles : images à upload
	 * @param multiple labels :  il peut y avoir de 2 à 10 labels
	 * @return 
	 */
	@PostMapping("/upload")
	public RedirectView uploadImage(@RequestParam("files") MultipartFile [] multipartFiles,
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
			// recuperation de la date actuelle pour faire un dossier pour les images qui sont upload
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
	        String uploadDir = context.getRealPath("user-photos");
	        uploadDir = uploadDir + File.separator + dateFormat.format(cal.getTime()).replace("/", "-").replace(" ", "_").replace(":", "-") + File.separator + "upload" + File.separator;
	        System.out.println("-- Path Dir ----- : " + uploadDir);
	       
	        // recuperation des images et sauvegarder dans un dossier de partage
			for (MultipartFile multipartFile : multipartFiles) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		        try {
		        	System.out.println("-- Image name ---- : " + fileName);
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				} catch (IOException e) {
					// TODO LOGGER
					e.printStackTrace();
				}
			} 
			
			// creer fichier txt des labels
			try {
				FileUploadUtil.saveLabelsFile(uploadDir, "labels.txt", listLabels);
				System.out.println("-- File name ---- : labels.txt");
			} catch (IOException e) {
				// TODO LOGGER
				e.printStackTrace();
			}
			// TODO  lancer le script python
			
		}
		 // redirection vers une autre page web
        return new RedirectView("/", true);
	}
	
}

