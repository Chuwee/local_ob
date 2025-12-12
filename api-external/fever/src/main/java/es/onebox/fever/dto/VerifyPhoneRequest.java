package es.onebox.fever.dto;

import java.io.Serial;
import java.io.Serializable;

public class VerifyPhoneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String otp;

    public String getOtp() { return otp; }

    public void setOtp(String otp) { this.otp = otp; }
}
