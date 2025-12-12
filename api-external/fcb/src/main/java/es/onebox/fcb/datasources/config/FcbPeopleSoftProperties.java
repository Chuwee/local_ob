package es.onebox.fcb.datasources.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("fcb.peoplesoft")
public class FcbPeopleSoftProperties {

    private FcbServiceConfig factures;
    private FcbServiceConfig clients;
    private FcbServiceConfig tresoreria;
    private String channel;
    private String password;

    public FcbServiceConfig getFactures() {
        return factures;
    }

    public void setFactures(FcbServiceConfig factures) {
        this.factures = factures;
    }

    public FcbServiceConfig getClients() {
        return clients;
    }

    public FcbServiceConfig getTresoreria() {
        return tresoreria;
    }

    public void setTresoreria(FcbServiceConfig tresoreria) {
        this.tresoreria = tresoreria;
    }

    public void setClients(FcbServiceConfig clients) {
        this.clients = clients;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
