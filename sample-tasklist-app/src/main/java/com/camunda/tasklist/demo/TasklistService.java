package com.camunda.tasklist.demo;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.tasklist.generated.model.TaskByVariables;
import io.camunda.tasklist.generated.model.TaskSearchRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TasklistService {
    private final CamundaTaskListClient taskListClient;
    public List<Task> search(String searchString) throws TaskListException {
        // Building the search query as in the documention
        TaskSearchRequest request = new TaskSearchRequest()
                .state(TaskSearchRequest.StateEnum.CREATED)
                .taskVariables(Arrays.asList(
                        new TaskByVariables()
                        .name("demoId") // process instance variable set at the beginning
                        .operator(TaskByVariables.OperatorEnum.EQ)
                        .value(String.format("\"%s\"", searchString)))
                );
        log.info("search for variables with value: {}", request.toString());
        return taskListClient.getTasks(request, false);
    }

    public void complete(String taskId) throws TaskListException {
        var variable = new HashMap<String, Object>();
        variable.put("foo", "bar");
        taskListClient.completeTask(taskId, variable);
    }
}
