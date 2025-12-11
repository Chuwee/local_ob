package es.onebox.event.events.quartz;

import es.onebox.event.events.dto.TierDTO;
import es.onebox.event.events.request.TiersFilter;
import es.onebox.event.events.service.EventTierService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReflectTierPriceJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectTierPriceJob.class);

    @Autowired
    private EventTierService eventTierService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Long eventId = jobExecutionContext.getMergedJobDataMap().getLong(ScheduleReflectTierPriceService.EVENT_ID);
        Long tierId = jobExecutionContext.getMergedJobDataMap().getLong(ScheduleReflectTierPriceService.TIER_ID);

        LOGGER.info("[REFLECT TIER PRICE] Executing for eventId: {}, tierId: {}", eventId, tierId);

        TiersFilter filter = new TiersFilter();
        filter.setActive(true);
        List<TierDTO> tiers = eventTierService.getEventTiers(eventId, filter).getData();

        Optional<TierDTO> optionalTier = tiers.stream()
                .filter(t -> t.getId().equals(tierId))
                .findAny();

        if (optionalTier.isPresent()) {
            TierDTO tier = optionalTier.get();
            eventTierService.applyTierChanges(eventId, tier.getId().intValue(), tier.getPriceTypeId().intValue(), tier.getPrice());
        } else {
            LOGGER.error("[REFLECT TIER PRICE] Trying to apply a tier that is not active, no changes were made");
        }


    }
}
