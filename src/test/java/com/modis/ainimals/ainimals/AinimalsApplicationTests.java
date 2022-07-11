package com.modis.ainimals.ainimals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AinimalsApplicationTests {
	@Autowired
	ServletContext context;
	
	@Test
	void contextLoads() {
		System.out.println("Context chargé");
		// Pour connaitre l'ip du serveur
		InetAddress Ip = null;
		try {
			Ip = InetAddress.getLocalHost();
			System.out.println("IP" + Ip.getCanonicalHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		 String uploadDir = context.getRealPath("shared");
		 System.out.println("uploadDir : " + uploadDir);
	}

}
