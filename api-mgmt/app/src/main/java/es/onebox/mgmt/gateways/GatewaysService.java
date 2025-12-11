package es.onebox.mgmt.gateways;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.gateways.converter.GatewayConfigConverter;
import es.onebox.mgmt.gateways.dto.GatewayConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GatewaysService {
    private final String ONEBOX_ACCOUNTNG_GATEWAY = "oneboxAccounting";

    @Autowired
    private ApiPaymentDatasource apiPaymentDatasource;

    public GatewayConfigDTO gatewayConfig(String gatewaySid) {
        if (CommonUtils.isBlank(gatewaySid)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_SID);
        }

        GatewayConfig gatewayConfig = apiPaymentDatasource.getGatewayConfig(gatewaySid);
        List<String> fields =
            gatewaySid.equals(ONEBOX_ACCOUNTNG_GATEWAY) ? new ArrayList<>()
                                                        : apiPaymentDatasource.getGatewayConfigFields(gatewaySid);

        return GatewayConfigConverter.fromDTO(gatewayConfig, fields);
    }

    public List<GatewayConfigDTO> getGateways() {
        List<GatewayConfig> gateways = apiPaymentDatasource.getGateways();
        return GatewayConfigConverter.fromDTO(gateways);
    }
}
