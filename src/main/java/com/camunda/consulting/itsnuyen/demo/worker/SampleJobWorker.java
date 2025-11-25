package com.camunda.consulting.itsnuyen.demo.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SampleJobWorker {

    @JobWorker(type = "my-worker")
    public void handleJobFoo(JobClient client, ActivatedJob job) {
        // do whatever you need to do
        log.info("Handling job: {}", job.toString());
        var myHeaders = job.getCustomHeaders();
        switch (myHeaders.get("type")) {
            case "taskA":
                log.info("Processing task A");
                // TODO worker logic A
                break;
            case  "taskB":
                log.info("Processing task B");
                // TODO worker logic B
                break;
            default:
                log.info("Unknown task type");
                client.newThrowErrorCommand(job)
                        .errorCode("UNKNOWN_TASK_TYPE")
                        .errorMessage("The task type is unknown: " + myHeaders.get("type"))
                        .send()
                        .join();
                break;
        }
    }

}
