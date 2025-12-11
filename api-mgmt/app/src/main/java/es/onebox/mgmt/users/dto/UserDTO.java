package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.operators.dto.OperatorDTO;
import es.onebox.mgmt.realms.dto.RoleDTO;
import es.onebox.mgmt.users.enums.MFAType;
import es.onebox.mgmt.users.enums.UserStatus;

import java.io.Serial;
import java.util.List;

public class UserDTO extends BaseUserDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;

    private OperatorDTO operator;
    private ProducerDTO producer;

    private String timezone;
    private String currency;
    @JsonProperty("use_multicurrency")
    private Boolean useMulticurrency;
    private UserStatus status;
    private List<RoleDTO> roles;

    private String apikey;
    @JsonProperty("mfa_type")
    private MFAType mfaType;
    private VisibilityDTO visibility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OperatorDTO getOperator() {
        return operator;
    }

    public void setOperator(OperatorDTO operator) {
        this.operator = operator;
    }

    public ProducerDTO getProducer() {
        return producer;
    }

    public void setProducer(ProducerDTO producer) {
        this.producer = producer;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getUseMulticurrency() {
        return useMulticurrency;
    }

    public void setUseMulticurrency(Boolean useMulticurrency) {
        this.useMulticurrency = useMulticurrency;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public MFAType getMfaType() {
        return mfaType;
    }

    public void setMfaType(MFAType mfaType) {this.mfaType = mfaType;}

    public VisibilityDTO getVisibility() {return visibility;}

    public void setVisibility(VisibilityDTO visibility) {this.visibility = visibility;}
}
