package com.ealanta.bootiful;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

@Log
class BootifulApplicationTests {

	@Test
	void contextLoads() {
		String msg = String.format("The java version is %s", System.getProperty("java.version"));
		log.info(msg);
	}

}
