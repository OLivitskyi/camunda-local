package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;


@Deployment(resources = "Tweet.bpmn")
@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {

    @Test
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("content", "Exercise 4 test - Test name");
        // Start process with Java API and variables
        ProcessInstance processInstance = BpmnAwareTests.runtimeService().startProcessInstanceByKey("TweetProcess", variables);
        assertThat(processInstance).isWaitingAt("ReviewTweetTask");
        assertThat(task()).hasCandidateGroup("management").isNotAssigned();
        List<Task> taskList = taskService()
                .createTaskQuery()
                .taskCandidateGroup("management")
                .processInstanceId(processInstance.getId())
                .list();
        org.assertj.core.api.Assertions.assertThat(taskList).isNotNull();
        org.assertj.core.api.Assertions.assertThat(taskList).hasSize(1);
        Task task = taskList.get(0);
        Map<String, Object> approvedMap = new HashMap<String, Object>();
        approvedMap.put("approved", true);
        taskService().complete(task.getId(), approvedMap);
    }

}
