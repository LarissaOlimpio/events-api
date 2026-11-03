package com.manager.events_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.manager.events_api.infra.email.EmailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class EventsApiApplicationTests {
	@MockitoBean
	private EmailSender emailSender;

	@Test
	void contextLoads() {
	}

}
