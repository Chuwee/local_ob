package es.onebox.mgmt.queueit.utils;

import com.queue_it.connector.HttpRequest;
import com.queue_it.connector.KnownUser;
import com.queue_it.connector.integrationconfig.CustomerIntegration;
import com.queue_it.connector.integrationconfig.IntegrationConfigModel;
import com.queue_it.connector.integrationconfig.IntegrationEvaluator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Pattern;

public class QueueITUtils {

    public static IntegrationConfigModel getRequestValidationByConfig(HttpServletRequest request, CustomerIntegration integrationConfig, String url) {
        IntegrationConfigModel matchedConfig = null;
        try {
            String pureUrl = QueueITUtils.getPureUrl(request, url.replace("/spring", ""));
            IntegrationEvaluator configEvaluater = new IntegrationEvaluator();
            matchedConfig = configEvaluater.getMatchedIntegrationConfig(integrationConfig, pureUrl, new HttpRequest(request));
        } catch (Exception ex) {

        }
        return matchedConfig;
    }

    public static String getPureUrl(HttpServletRequest request, String url){
        Pattern pattern = Pattern.compile("([?&])(" + KnownUser.QUEUEIT_TOKEN_KEY + "=[^&]*)", Pattern.CASE_INSENSITIVE);
        String queryString = request.getQueryString();
        String finalUrl = url + (queryString != null ? ("?" + queryString) : "");
        return pattern.matcher(finalUrl).replaceAll("");
    }

}
