package com.modis.ainimals.ainimals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AinimalsApplicationTests {

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
	}

}
