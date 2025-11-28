package com.camunda.consulting.itsnuyen.demo;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RequestMapping("/api/process")
@RestController
public class SampleController {
    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/start")
    public String startProcess(@RequestBody ProcessInformation sample) {
        var processRecord = new ProcessInformation(sample.userCreatedProcess, UUID.randomUUID().toString());
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_JobWorker")
                .latestVersion()
                .variables(processRecord)
                .send()
                .join();
        return "Process started";
    }


    record ProcessInformation(String userCreatedProcess, String uniqueIdentifier){}
}
