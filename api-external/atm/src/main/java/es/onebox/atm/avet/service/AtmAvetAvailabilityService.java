package es.onebox.atm.avet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.avetconfig.dto.ClubConfig;
import es.onebox.common.datasources.avetconfig.dto.Status;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.dispatcher.dto.CheckStatusResponse;
import es.onebox.common.datasources.dispatcher.repositoty.IntAvetDispatcherRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.exception.HttpErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtmAvetAvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmAvetAvailabilityService.class);

    private final IntAvetDispatcherRepository intAvetDispatcherRepository;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final ObjectMapper jacksonMapper;
    @Autowired
    public AtmAvetAvailabilityService(IntAvetDispatcherRepository intAvetDispatcherRepository, IntAvetConfigRepository intAvetConfigRepository,
                                      ObjectMapper jacksonMapper) {
        this.intAvetDispatcherRepository = intAvetDispatcherRepository;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.jacksonMapper = jacksonMapper;
    }


    public CheckStatusResponse getAvetAvailability() {
        Long entityId = getEntityId();
        ClubConfig clubConfig = intAvetConfigRepository.getClubByEntityId(entityId);
        if(clubConfig == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CLUB_CONFIG_NOT_FOUND);
        }
        String clubCode = clubConfig.getClubCode();
        if(clubCode == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CLUB_CONFIG_NOT_FOUND);
        }
        LOGGER.info("[AVET_AVAILABILITY] clubCode: {}", clubCode);
        try {
            CheckStatusResponse response = this.intAvetDispatcherRepository.checkStatus(clubCode);
            if(response == null) {
                LOGGER.error("[AVET_AVAILABILITY] AVET status response null, clubName: {}", clubCode);
                return new CheckStatusResponse(Status.KO, "AVET Null response");

            }
            return response;
        } catch (Exception e) {
            try {
                LOGGER.error("[AVET_AVAILABILITY] Error on checkStatus, clubName: {}", clubCode, e);
                HttpErrorException ex = (HttpErrorException) e;
                return jacksonMapper.readValue(ex.getResponseBody(), CheckStatusResponse.class);
            } catch(Exception e2) {
                LOGGER.error("[AVET_AVAILABILITY] Error on checkStatus, clubName: {}", clubCode, e2);
                throw e;
            }
        }
    }

    private Long getEntityId() {
        AuthenticationData authData = AuthenticationUtils.getAuthDataOrNull();
        if (authData == null || authData.getEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
        return AuthenticationUtils.getAuthDataOrNull().getEntityId();
    }

}
