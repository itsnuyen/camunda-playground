package com.camunda.consulting.itsnuyen.demo.worker;

import com.camunda.consulting.itsnuyen.demo.records.SampleRecord;
import com.camunda.consulting.itsnuyen.demo.service.SampleService;
import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.zeebe.client.ZeebeClient;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ExampleWorkers {

    private final SampleService sampleService;

    @JobWorker(type = "sample-worker-with-return-value")
    public SampleRecord workWithReturnType(JobClient jobClient, ActivatedJob job) {
        log.info("Job worked: ProcessInstanceKey wth ReturnType[{}]", job.getProcessInstanceKey());
        var variables = job.getVariablesAsMap();
        log.info("Variables: {}", variables);
        return sampleService.getSampleRecord("question from job with return type");
    }
}
