package es.onebox.event.events.quartz;

import es.onebox.core.scheduler.TaskInfo;
import es.onebox.core.scheduler.TaskService;
import es.onebox.event.common.utils.CronUtils;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.List;

@Service
public class ScheduleReflectTierPriceService {

    public static final String EVENT_ID = "eventId";
    public static final String TIER_ID = "tierId";
    private static final String REFRESH_EVENT_GROUP = "REFRESH_EVENT_TIER_GROUP";
    private static final String REFRESH_EVENT_NAME_SUFFIX = "REFRESH_EVENT_TIER";

    @Autowired
    private TaskService taskService;
    @Autowired
    private TierDao tierDao;

    public void schedule(Long eventId, Long tierId, TemporalAccessor temporalAccessor) {
        taskService.addJob(buildTaskInfo(eventId, tierId, temporalAccessor));
    }

    public void unschedule(Long tierId) {
        taskService.delete(buildJobName(tierId), REFRESH_EVENT_GROUP);
    }

    public void unscheduleAllForEvent(Long eventId) {
        List<TierRecord> tiers = tierDao.findByEventId(eventId.intValue(), null, null, null);
        unschedule(tiers);
    }

    public void updateSchedule(Long eventId, Long tierId, TemporalAccessor temporalAccessor) {
        if (taskService.checkExists(buildJobName(tierId), REFRESH_EVENT_GROUP)) {
            taskService.edit(buildTaskInfo(eventId, tierId, temporalAccessor));
        } else {
            schedule(eventId, tierId, temporalAccessor);
        }
    }

    public void unscheduleForPriceType(Long priceTypeId) {
        List<CpanelTierRecord> tiers = tierDao.findByPriceType(priceTypeId.intValue());
        tiers.forEach(t -> unschedule(t.getIdtier().longValue()));
    }

    public void unscheduleAllForVenueTemplate(Long venueTemplateId) {
        List<TierRecord> tiers = tierDao.findByVenueTemplate(venueTemplateId.intValue());
        unschedule(tiers);
    }

    private void unschedule(List<TierRecord> tiers) {
        tiers.stream()
                .map(TierRecord::getIdtier)
                .map(Integer::longValue)
                .forEach(this::unschedule);
    }

    private TaskInfo buildTaskInfo(Long eventId, Long tierId, TemporalAccessor dateTime) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobName(buildJobName(tierId));
        taskInfo.setJobGroup(REFRESH_EVENT_GROUP);
        taskInfo.setCronExpression(CronUtils.buildCron(dateTime));

        HashMap<String, Long> data = new HashMap<>();
        data.put(EVENT_ID, eventId);
        data.put(TIER_ID, tierId);
        taskInfo.setData(data);

        taskInfo.setJobClass(ReflectTierPriceJob.class);
        return taskInfo;
    }

    private String buildJobName(Long tierId) {
        return REFRESH_EVENT_NAME_SUFFIX + "_" + tierId;
    }

}
