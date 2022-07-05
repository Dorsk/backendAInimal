package com.modis.ainimals.ainimals.load;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.modis.ainimals.ainimals.utils.FileUploadUtils;

@RestController
public class ImagesAndLabelsController {

	private final AtomicLong counter = new AtomicLong();

	/**
	 * Home page 
	 * récupération des labels 
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
	 * @param label
	 * @return
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public void uploadImage(@RequestParam("image") MultipartFile multipartFile) {
		
		// recuperation des images
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
       
		// recuperation de la date actuelle pour faire un dossier pour les images qui sont upload
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
        String uploadDir = "user-photos/" + dateFormat.format(cal.getTime()).replace(" ", "_") + "/upload/";
 
        try {
			FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
         // redirection vers une autre page web
//        return new RedirectView("/upload", true);
	}
	
}

