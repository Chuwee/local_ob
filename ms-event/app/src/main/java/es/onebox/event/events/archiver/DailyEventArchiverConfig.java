package es.onebox.event.events.archiver;

import es.onebox.core.scheduler.TaskInfo;
import es.onebox.core.scheduler.TaskService;
import es.onebox.event.common.amqp.streamprogress.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Component
public class DailyEventArchiverConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressService.class);

    private static final String JOB_GROUP = "EVENT_ARCHIVER";
    private static final String JOB_CRON = "0 0 %d ? * *";
    public static final String JOB_SHARD = "shard";

    private final TaskService taskService;
    private final DailyEventArchiverProperties dailyEventArchiverProperties;

    public DailyEventArchiverConfig(TaskService taskService, DailyEventArchiverProperties dailyEventArchiverProperties) {
        this.taskService = taskService;
        this.dailyEventArchiverProperties = dailyEventArchiverProperties;
    }

    @PostConstruct
    private void schedule() {
        for (DailyEventArchiver dailyEventArchiver : dailyEventArchiverProperties.getDailyEventArchiver()) {
            if (dailyEventArchiver.isActive()) {
                String jobName = JOB_GROUP + "_SHARD_" + dailyEventArchiver.getShard();
                String cronExpression = String.format(JOB_CRON, dailyEventArchiver.getHour());
                LOGGER.info("[SCHEDULER] Init {} with cron expression {}", jobName, cronExpression);
                if (!taskService.checkExists(jobName, JOB_GROUP)) {
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setCronExpression(cronExpression);
                    taskInfo.setJobName(jobName);
                    taskInfo.setJobGroup(JOB_GROUP);
                    taskInfo.setJobClass(EventArchiverProcessor.class);
                    taskInfo.setData(Map.of(JOB_SHARD, dailyEventArchiver.getShard()));
                    taskService.addJob(taskInfo);
                }
            }
        }


    }

}
