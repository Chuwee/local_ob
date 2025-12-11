package es.onebox.event.events.archiver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("onebox.scheduler")
public class DailyEventArchiverProperties {

    private List<DailyEventArchiver> dailyEventArchiver;

    public List<DailyEventArchiver> getDailyEventArchiver() {
        return dailyEventArchiver;
    }

    public void setDailyEventArchiver(List<DailyEventArchiver> dailyEventArchiver) {
        this.dailyEventArchiver = dailyEventArchiver;
    }
}
