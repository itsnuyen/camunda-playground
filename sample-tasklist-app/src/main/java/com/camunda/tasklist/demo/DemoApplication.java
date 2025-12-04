package com.camunda.tasklist.demo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@SpringBootApplication
@Deployment(resources = "classpath*:/bpmn/**/*.bpmn")
public class DemoApplication {

    @Autowired
    private ZeebeClient zeebe;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String uniqueID = UUID.randomUUID().toString();
            var map = new HashMap<String, Object>();
            map.put("demoId", uniqueID);
            map.put("research", "testtest");
            map.put("check", false);
            var pi = zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Process_userTask")
                    .latestVersion()
                    .variables(map)
                    .send().join();
            log.info("started process instance with id: [{}] and with id [{}]", pi.getProcessInstanceKey(), uniqueID);
        };
    }

}
