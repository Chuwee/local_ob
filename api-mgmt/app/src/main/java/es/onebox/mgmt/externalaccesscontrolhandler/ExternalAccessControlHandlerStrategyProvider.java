package es.onebox.mgmt.externalaccesscontrolhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExternalAccessControlHandlerStrategyProvider {

    private static final String FORTRESS = "fortress";
    private final Map<String, ExternalAccessControlHandler> externalTransfers;

    @Autowired
    public ExternalAccessControlHandlerStrategyProvider(Map<String, ExternalAccessControlHandler> externalTransfers) {
        this.externalTransfers = externalTransfers;
    }

    public ExternalAccessControlHandler provide(String accessControl) {
        if (accessControl.contains(FORTRESS)) {
            return externalTransfers.get(FORTRESS);
        } else {
            return externalTransfers.get(accessControl);
        }
    }
}
