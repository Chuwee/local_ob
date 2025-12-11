package es.onebox.mgmt.channels.gateways;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.dto.ConfigSidDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDetailDTO;
import es.onebox.mgmt.channels.gateways.dto.CreateChannelGateway;
import es.onebox.mgmt.channels.gateways.dto.UpdateChannelGateways;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(
        value = ChannelGatewaysController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelGatewaysController {

    private static final String AUDIT_COLLECTION = "CHANNEL_GATEWAY";
    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/gateways";

    private final ChannelGatewaysService channelGatewaysService;

    @Autowired
    public ChannelGatewaysController(ChannelGatewaysService channelGatewaysService){
        this.channelGatewaysService = channelGatewaysService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @GetMapping
    public List<ChannelGatewayDTO> getGateways(@PathVariable Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return channelGatewaysService.getGateways(channelId);
    }

    @Secured({ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGateways(@PathVariable Long channelId,
                               @RequestBody @Valid UpdateChannelGateways channelGateways) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        channelGatewaysService.updateGateways(channelId,channelGateways);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{gatewaySid}/configurations/{configSid}")
    public ChannelGatewayDetailDTO getGateway(@PathVariable Long channelId,
                                              @PathVariable String gatewaySid,
                                              @PathVariable String configSid) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return channelGatewaysService.getGateway(channelId, gatewaySid, configSid);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping(value = "/{gatewaySid}/configurations")
    public ConfigSidDTO createGateway(@PathVariable Long channelId,
                                      @PathVariable String gatewaySid,
                                      @RequestBody @Valid CreateChannelGateway createChannelGateway) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return new ConfigSidDTO(channelGatewaysService.createChannelGatewayConfig(channelId, gatewaySid, createChannelGateway));
    }

    @Secured({ROLE_OPR_MGR})
    @DeleteMapping(value = "/{gatewaySid}/configurations/{configSid}")
    public ResponseEntity<Serializable> deleteGateway(@PathVariable Long channelId,
                                                      @PathVariable String gatewaySid,
                                                      @PathVariable String configSid) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        channelGatewaysService.deleteChannelGatewayConfig(channelId, gatewaySid, configSid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @PutMapping(value = "/{gatewaySid}/configurations/{configSid}")
    public ResponseEntity<Serializable> updateGateway(@PathVariable Long channelId,
                                      @PathVariable String gatewaySid,
                                      @PathVariable String configSid,
                                      @RequestBody @Valid CreateChannelGateway createChannelGateway) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        channelGatewaysService.updateChannelGatewayConfig(channelId, gatewaySid, configSid, createChannelGateway);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
