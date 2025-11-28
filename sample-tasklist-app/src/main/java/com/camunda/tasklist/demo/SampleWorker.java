package com.camunda.tasklist.demo;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleWorker {

    @JobWorker(type = "my-worker")
    public void handleSampleJob(JobClient client, ActivatedJob job) {
        log.info("Sample worker executed");
        client.newCompleteCommand(job.getKey())
                .variable("task1", "executed")
                .send();
    }
}
