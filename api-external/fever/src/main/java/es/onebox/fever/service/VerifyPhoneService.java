package es.onebox.fever.service;

import es.onebox.fever.dto.CustomerPhoneVerificationRequestDTO;
import es.onebox.fever.dto.CustomerVerifyRequestDTO;
import es.onebox.fever.dto.RegisterPhoneRequest;
import es.onebox.fever.dto.UserPhoneInfoDTO;
import es.onebox.fever.dto.VerifyPhoneRequest;
import es.onebox.fever.repository.PhoneVerificationRepository;
import org.springframework.stereotype.Service;

@Service
public class VerifyPhoneService {

  private final PhoneVerificationRepository phoneVerificationRepository;

    public VerifyPhoneService(PhoneVerificationRepository phoneVerificationRepository) {
        this.phoneVerificationRepository = phoneVerificationRepository;
    }

    public void requestPhoneVerificationCode(CustomerPhoneVerificationRequestDTO request) {
        String token = request.getAccessToken();
        UserPhoneInfoDTO userPhoneInfo = phoneVerificationRepository.getUserPhoneInfo(request.getClientId(), token);
        if (userPhoneInfo == null || userPhoneInfo.getData() == null || userPhoneInfo.getError() != null) {
            RegisterPhoneRequest registerPhoneRequest = new RegisterPhoneRequest();
            registerPhoneRequest.setPhone(request.getPrefix() + request.getPhone());
            phoneVerificationRepository.registerPhone(registerPhoneRequest, request.getClientId(), token);
        }

        phoneVerificationRepository.requestPhoneVerificationCode(request.getClientId(), token);
    }

  public void verifyPhone(CustomerVerifyRequestDTO request) {
    String token = request.getAccessToken();
    VerifyPhoneRequest verifyPhoneRequest = new VerifyPhoneRequest();
    verifyPhoneRequest.setOtp(request.getCode());
    phoneVerificationRepository.verifyPhone(verifyPhoneRequest, request.getClientId(), token);
  }

}
