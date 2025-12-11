package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.exception.ApiErrorDTO;
import es.onebox.mgmt.sessions.enums.UpdateSessionStatus;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSessionResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3542512991763395824L;

    private Long id;
    private UpdateSessionStatus status;
    private ApiErrorDTO detail;

    public UpdateSessionResponseDTO() {

    }

    public UpdateSessionResponseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UpdateSessionStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateSessionStatus status) {
        this.status = status;
    }

    public ApiErrorDTO getDetail() {
        return detail;
    }

    public void setDetail(ApiErrorDTO detail) {
        this.detail = detail;
    }
}
