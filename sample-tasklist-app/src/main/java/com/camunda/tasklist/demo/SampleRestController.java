package com.camunda.tasklist.demo;

import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/sample")
public class SampleRestController {
    private final ZeebeClient zeebeClient;
    private final TasklistService tasklistService;

    @GetMapping("/get-tasks/{searchString}")
    public List<Task> sayHello(@PathVariable String searchString) throws TaskListException {
        log.info("search for variables with value: {}", searchString);
        return tasklistService.search(searchString);
    }

    @GetMapping("/start-process/{foo}")
    public void startProcess(@PathVariable String foo) {
        log.info("start process with demoId: {}", foo);
        var map = new HashMap<String, Object>();
        map.put("demoId", UUID.randomUUID().toString());
        map.put("research", foo);
        var pi = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_userTask")
                .latestVersion()
                .variables(map)
                .send().join();
        log.info("started process instance with id: [{}]", pi.getProcessInstanceKey());
    }

    @PostMapping("/complete-task/{taskId}")
    public void completeTask(@PathVariable String taskId) throws TaskListException {
        log.info("complete task with id: {}", taskId);
        tasklistService.complete(taskId);
    }
}
