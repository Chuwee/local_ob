package es.onebox.fever.repository;

import es.onebox.fever.dto.RegisterPhoneRequest;
import es.onebox.fever.dto.UserPhoneInfoDTO;
import es.onebox.fever.dto.VerifyPhoneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PhoneVerificationRepository {

    private final PhoneVerificationDatasource phoneVerificationDatasource;

    @Autowired
    public PhoneVerificationRepository(PhoneVerificationDatasource phoneVerificationDatasource) {
        this.phoneVerificationDatasource = phoneVerificationDatasource;
    }

    public UserPhoneInfoDTO getUserPhoneInfo(String userId, String token) {
        return phoneVerificationDatasource.getUserPhoneInfo(userId, token);
    }

    public void registerPhone(RegisterPhoneRequest request, String userId, String token) {
        phoneVerificationDatasource.registerPhone(request, userId, token);
    }

    public void requestPhoneVerificationCode(String clientId, String token) {
         phoneVerificationDatasource.requestPhoneVerificationCode(clientId, token);
    }

    public void verifyPhone(VerifyPhoneRequest request, String clientId, String token) {
        phoneVerificationDatasource.verifyPhone(request, clientId, token);
    }

}
