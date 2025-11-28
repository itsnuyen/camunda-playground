package com.camunda.tasklist.demo;

import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/sample")
public class SampleRestController {

    private final TasklistService tasklistService;

    @GetMapping("/get-tasks/{searchString}")
    public List<Task> sayHello(@PathVariable String searchString) throws TaskListException {
        log.info("search for variables with value: {}", searchString);
        return tasklistService.search(searchString);
    }

    @PostMapping("/complete-task/{taskId}")
    public void completeTask(@PathVariable String taskId) throws TaskListException {
        log.info("complete task with id: {}", taskId);
        tasklistService.complete(taskId);
    }
}
