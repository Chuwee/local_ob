package es.onebox.fcb.config;

import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.salesforce.repository.SalesforceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class FcbSalesforce implements HealthIndicator {

    @Autowired
    private SalesforceRepository salesforceRepository;

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        try {
            salesforceRepository.login();
            builder.up();
        } catch (Exception e) {
            builder.down().withDetail("exception", e.fillInStackTrace().getMessage());
        }
        return builder.build();
    }

}
