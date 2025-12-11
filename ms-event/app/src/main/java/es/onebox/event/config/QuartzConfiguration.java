package es.onebox.event.config;

import es.onebox.core.scheduler.TaskInfo;
import es.onebox.core.scheduler.TaskService;
import es.onebox.event.events.dynamicpricing.DynamicPricingJob;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QuartzConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzConfiguration.class);
    private static final String JOB_NAME = "DYNAMIC_PRICING_SCHEDULER";
    private static final String JOB_CRON = "0 0 3 * * ?";

    @Autowired
    private TaskService taskService;
    @Autowired
    private Scheduler scheduler;
    @Value("${quartz.disable:false}")
    private boolean disableQuartz;

    @PostConstruct
    public void initJobs() throws SchedulerException {
        if (BooleanUtils.isTrue(disableQuartz)) {
            LOGGER.warn("[QUARTZ] Cron jobs disabled for environment");
            scheduler.shutdown();
            return;
        }
        addJob(JOB_NAME, JOB_CRON, DynamicPricingJob.class, new HashMap());
    }

    private void addJob(String jobName, String cron, Class jobClazz, Map data) {
        LOGGER.info("[SCHEDULER] Init {} with cron expression {}", JOB_NAME, JOB_CRON);
        if (!taskService.checkExists(jobName, jobName)) {
            taskService.addJob(buildTaskInfo(jobName, cron, jobClazz, data));
        }
    }

    private TaskInfo buildTaskInfo(String jobName, String cron, Class jobClazz, Map data) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobName(jobName);
        taskInfo.setJobGroup(JOB_NAME);
        taskInfo.setCronExpression(cron);
        taskInfo.setJobClass(jobClazz);
        taskInfo.setData(data);
        return taskInfo;
    }
}
