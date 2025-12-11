package es.onebox.event.sessions.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionStreamingEmailJob implements Job {

    public static final String SESSION_ID = "SESSION_ID";
    public static final String SESSION_STREAMING_EMAIL = "SESSION_STREAMING_EMAIL";

    @Autowired
    private SessionStreamingEmailService sessionStreamingEmailService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Long sessionId = jobExecutionContext.getMergedJobDataMap().getLong(SESSION_ID);
        sessionStreamingEmailService.sendEmails(sessionId, null);
    }

}
