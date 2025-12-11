package es.onebox.mgmt.queueit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("queueit.integration")
public class QueueItProperties {

    private String apiKey;
    private String secretKey;
    private String host;
    private String customerId;

    private List<QueueItCustomerDescriptor> customerAccounts;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<QueueItCustomerDescriptor> getCustomerAccounts() {
        return customerAccounts;
    }

    public void setCustomerAccounts(List<QueueItCustomerDescriptor> customerAccounts) {
        this.customerAccounts = customerAccounts;
    }
}

