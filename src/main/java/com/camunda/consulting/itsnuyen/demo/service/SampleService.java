package com.camunda.consulting.itsnuyen.demo.service;

import com.camunda.consulting.itsnuyen.demo.records.SampleRecord;
import org.springframework.stereotype.Service;

@Service
public class SampleService {
    public SampleRecord getSampleRecord(String question) {
        return new SampleRecord(question, "42");
    }
}
