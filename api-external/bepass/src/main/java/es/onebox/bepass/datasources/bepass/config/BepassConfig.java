package es.onebox.bepass.datasources.bepass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("bepass")
public class BepassConfig {

    private String postbackUrl;
    private AuthConfig auth;
    private UsersConfig users;
    private EventsConfig events;

    public String getPostbackUrl() {
        return postbackUrl;
    }

    public void setPostbackUrl(String postbackUrl) {
        this.postbackUrl = postbackUrl;
    }


    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

    public UsersConfig getUsers() {
        return users;
    }

    public void setUsers(UsersConfig users) {
        this.users = users;
    }

    public EventsConfig getEvents() {
        return events;
    }

    public void setEvents(EventsConfig events) {
        this.events = events;
    }

}
