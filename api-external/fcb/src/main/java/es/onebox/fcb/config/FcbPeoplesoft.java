package es.onebox.fcb.config;

import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FcbPeoplesoft implements HealthIndicator {

    @Autowired
    private PeopleSoftRepository peopleSoftRepository;

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        try {
            peopleSoftRepository.checkNif("00000001R", "ESP");
            builder.up();
        } catch (Exception e) {
            builder.down().withDetail("exception", e.fillInStackTrace().getMessage());
        }
        return builder.build();
    }

}
