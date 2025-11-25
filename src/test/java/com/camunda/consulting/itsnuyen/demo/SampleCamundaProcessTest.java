package com.camunda.consulting.itsnuyen.demo;


import com.camunda.consulting.itsnuyen.demo.records.SampleRecord;
import com.camunda.consulting.itsnuyen.demo.service.SampleService;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static io.camunda.process.test.api.assertions.ElementSelectors.byId;
import static io.camunda.process.test.api.assertions.ElementSelectors.byName;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(
        properties = {
                "camunda.client.worker.defaults.enabled=false", // disable all job workers and enable them selectively by adding properties
                "camunda.client.worker.override.sample-worker-with-return-value.enabled=true" // enable only the sample-worker-with-return-value job worker
        })
@CamundaSpringProcessTest
public class SampleCamundaProcessTest {
    @Autowired
    private CamundaClient client;
    @Autowired
    private CamundaProcessTestContext processTestContext;

    @MockitoBean
    private SampleService sampleService;
    @Autowired
    private CamundaProcessTestContext camundaProcessTestContext;

    @Test
    public void testProcessWithSimpleBPMN() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("price", 1500);
        processTestContext.mockJobWorker("io.camunda:http-json:1").thenComplete(variables);
        Mockito.when(sampleService.getSampleRecord(anyString())).thenReturn(new SampleRecord("foo", "42"));
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/sample_jobworker.bpmn")
                .send().join();


        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_JobWorker")
                        .latestVersion()
                        .variables(variables)
                        .send()
                        .join();
        // then
        var expectedResultsVariable = new HashMap<String, Object>();
        expectedResultsVariable.put("name", "foo");
        expectedResultsVariable.put("answer", "42");
        CamundaAssert.assertThat(processInstance)
                .hasCompletedElement(byId("Activity_SampleWorker"), 1)
                .hasVariables(expectedResultsVariable)
                .isCompleted();
    }


    @Test
    public void testWithRestConnector() throws Exception {
        var restCallResult = new HashMap<String, Object>();
        restCallResult.put("camundaVersion", "8.8.3");
        processTestContext.mockJobWorker("io.camunda:http-json:1").thenComplete(restCallResult);

        client.newDeployResourceCommand()
                .addResourceFromClasspath("./bpmn/sample_connector.bpmn")
                .send().join();


        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_Connector_Example")
                        .latestVersion()
                        .send()
                        .join();
        // then
        var expectedResultsVariable = new HashMap<String, Object>();
        restCallResult.put("camundaVersion", "8.8.3");
        CamundaAssert.assertThat(processInstance)
                .hasCompletedElements(byId("Activity_0enqkvq"), byId("Activity_0ftyy2r"))
                .hasVariables(expectedResultsVariable)
                .isCompleted();
    }


    @Test
    public void testProcessWithGateway() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("mailType", "mail1");
        processTestContext.mockJobWorker("identify-mailtype").thenComplete(variables);
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/example_mail_gateway.bpmn")
                .send().join();


        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_MailType")
                        .latestVersion()
                        .send()
                        .join();
        // then using labels of bpmn is also possible
        CamundaAssert.assertThat(processInstance)
                .hasCompletedElements(byName("mail received"),
                         byName("identify mail type"),
                         byName("extract mail type 1"),
                         byName("mail type 1 extracted")
                        )
                .hasVariables(variables)
                .isCompleted();
    }

    @Test
    public void testProcessWithGatewayMail2() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("mailType", "mail2");
        processTestContext.mockJobWorker("identify-mailtype").thenComplete(variables);
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/example_mail_gateway.bpmn")
                .send().join();


        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_MailType")
                        .latestVersion()
                        .send()
                        .join();
        // then using labels of bpmn is also possible
        CamundaAssert.assertThat(processInstance)
                .hasCompletedElements(byName("mail received"),
                        byName("identify mail type"),
                        byName("extract mail type 2"),
                        byName("mail type 2 extracted")
                )
                .hasVariables(variables)
                .isCompleted();
    }

    @Test
    public void testProcessCallActivity() throws Exception {
        Mockito.when(sampleService.getSampleRecord(anyString())).thenReturn(new SampleRecord("Ultimate Question of Life", "42"));
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/sample_jobworker.bpmn")
                .send().join();
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/call_activity.bpmn")
                .send().join();

        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_CallActivityExample")
                        .latestVersion()
                        .send()
                        .join();
        // then using labels of bpmn is also possible
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Ultimate Question of Life");
        variables.put("answer", "42");
        CamundaAssert.assertThat(processInstance)
                .hasVariables(variables)
                .isCompleted();
    }

    @Test
    public void testProcessMockActivity() throws Exception {
        final Map<String, Object> variables = Map.of(
                "name", "foo",
                "answer", "not ok"
        );
        camundaProcessTestContext.mockChildProcess("Process_JobWorker", variables);
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/call_activity.bpmn")
                .send().join();

        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_CallActivityExample")
                        .latestVersion()
                        .send()
                        .join();
        // then using labels of bpmn is also possible
        CamundaAssert.assertThat(processInstance)
                .hasVariables(variables)
                .isCompleted();
    }

    @Test
    public void testProcessUserTaskSample() throws Exception {
        final Map<String, Object> variables = Map.of(
                "textfield_test", "thisisatest"
        );
        client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/usertask.bpmn")
                .send().join();

        // when
        final ProcessInstanceEvent processInstance =
                client
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Process_usertask")
                        .latestVersion()
                        .send()
                        .join();
        processTestContext.completeUserTask("Activity_MyUserTaskID", variables);
        CamundaAssert.assertThat(processInstance)
                .hasVariables(variables)
                .hasCompletedElements(byName("started test")
                , byName("sample user task"), byName("user task ended"))
                .isCompleted();
    }
}
