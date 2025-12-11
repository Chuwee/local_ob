package es.onebox.event.events.archiver;

import es.onebox.event.events.amqp.sessionarchiver.SessionArchiverProducerService;
import es.onebox.event.events.service.EventService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

public class EventArchiverProcessor implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventArchiverProcessor.class);

    private static final int MONTHS_TO_ARCHIVE_EVENT = 25;

    @Autowired
    private EventService eventService;
    @Autowired
    private SessionArchiverProducerService sessionArchiverProducerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int shardId = context.getMergedJobDataMap().getInt(DailyEventArchiverConfig.JOB_SHARD);

        ZonedDateTime archivationDate = ZonedDateTime.now().minusMonths(MONTHS_TO_ARCHIVE_EVENT);
        List<Long> finalizedEventSessionIds = eventService.getSessionIdsToArchive(archivationDate, shardId);
        LOGGER.info("[EVENT ARCHIVER] shard: {} - Enqueue sessions to archive({}): {}",
                shardId, finalizedEventSessionIds.size(), finalizedEventSessionIds);

        for (Long finalizedEventSessionId : finalizedEventSessionIds) {
            sessionArchiverProducerService.archiveSession(finalizedEventSessionId);
        }

    }

}
