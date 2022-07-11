package com.modis.ainimals.ainimals.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.modis.ainimals.ainimals.upload.ImagesAndLabelsController;

@RestController
@ComponentScan({"com.modis.ainimals.ainimals.update"})
public class UpdateLabels {
	
//	@Autowired
//	ServletContext context;
//	 
//	Logger logger = LoggerFactory.getLogger(ImagesAndLabelsController.class);
//	
//	/**   
//	 * @return 
//	 * @return  
//	 */
//	@PostMapping("/updateLabel")
//	public ModelAndView postUpdateLabel(@RequestParam("photo") String sPhoto, @RequestParam("submit") String sSubmit) {
//		  
//		List <String> listLabels = new ArrayList<>();
//		 String sFilePath = context.getRealPath("shared") + File.separator + "labels-origin.txt";
//		// update fichier img;label
//		
//		// recuperer la liste des labels ayant était saisie par l'utilisateur la première fois  
//		try {
//		    BufferedReader reader = new BufferedReader(new FileReader(sFilePath));
//		    String line;
//		    while ((line = reader.readLine()) != null) {
//		    	listLabels.add(line);
//		    }  
//			reader.close();
//		} catch (IOException e) {
//			logger.error("-- ImagesAndLabelsController.updateLabel() failed ", e);
//		}
//		
//	    // TODO : ajouter le label au fichier labels.txt et trouve la prochaine image
//		
//		// charger une autre image avec les labels si besoin
//		ModelAndView modelView = new ModelAndView("loading");
//		modelView.addObject("labels", listLabels);
////		modelView.addObject("photo", photo); // TODO un nouveau chemin lu dans le fichier labels.txt sans label
//		return modelView;
//	}
//	/**   
//	 * @return  
//	 */
//	@GetMapping("/updateLabel")
//	public void getUpdateLabel(@RequestParam("photo") String sPhoto, @RequestParam("submit") String sSubmit) {
//		List <String> listLabels = new ArrayList<>();
//	}
}
