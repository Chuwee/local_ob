package es.onebox.event.config;

import org.springframework.context.ApplicationContext;

public class AppContext {

    private AppContext() {
    }

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        AppContext.applicationContext = applicationContext;
    }
}
