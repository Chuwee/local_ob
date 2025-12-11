package es.onebox.mgmt.datasources.integration.avetconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.dto.DatesFilter;
import es.onebox.mgmt.channels.dto.DatesFilterDTO;
import es.onebox.mgmt.channels.dto.NewMemberConfigDTO;
import es.onebox.mgmt.channels.members.dto.AvetEvent;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.CapacityDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.CompetitionDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.LiteralMapDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.Match;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.MatchPriceDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.VenueTemplateDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.ClubConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.LinkClubConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import es.onebox.mgmt.events.dto.UpdateCapacityExternalDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsAvetConfigDatasource {
    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "1.0";
    private static final String MAIN_BASE_PATH = "/avetconfig/" + API_VERSION;
    private static final String MEMBERS_BASE_PATH = "/avet-config/v1";
    private static final String CAPACITIES = "/capacities";
    private static final String CAPACITY = "/capacity";
    private static final String MATCHES = "/matches";
    private static final String COMPETITIONS = "/competitions";
    private static final String COMPETITION_ID = "/{competitionId}";
    private static final String MEMBER_CONFIG = "/member-config";
    private static final String ENTITIES = "/entities";
    private static final String VENUE_TEMPLATE = "/venue-template";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("COMPETITION_ID_MANDATORY", ApiMgmtErrorCode.COMPETITION_ID_MANDATORY);
        ERROR_CODES.put("INVALID_COMPETITION_ID", ApiMgmtErrorCode.COMPETITION_ID_INVALID);
        ERROR_CODES.put("COMPETITION_NOT_FOUND", ApiMgmtErrorCode.COMPETITION_NOT_FOUND);
        ERROR_CODES.put("MATCH_ID_MANDATORY", ApiMgmtErrorCode.MATCH_ID_MANDATORY);
        ERROR_CODES.put("INVALID_MATCH_ID", ApiMgmtErrorCode.MATCH_ID_INVALID);
        ERROR_CODES.put("MATCH_NOT_FOUND", ApiMgmtErrorCode.MATCH_NOT_FOUND);
        ERROR_CODES.put("CAPACITY_NOT_FOUND", ApiMgmtErrorCode.CAPACITY_NOT_FOUND);
        ERROR_CODES.put("LANGUAGE_BASE_LITERALS_NOT_FOUND", ApiMgmtErrorCode.LANGUAGE_BASE_LITERALS_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("INVALID_CLUB_CODE", ApiMgmtErrorCode.INVALID_CLUB_CODE);
        ERROR_CODES.put("MEMBER_CONFIG_NOT_FOUND", ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        ERROR_CODES.put("MEMBER_CONFIG_ALREADY_EXIST", ApiMgmtErrorCode.MEMBER_CONFIG_ALREADY_EXIST);
        ERROR_CODES.put("MEMBER_CONFIG_IS_LOCKED", ApiMgmtErrorCode.MEMBER_CONFIG_IS_LOCKED);
        ERROR_CODES.put("CLUB_CONFIG_NOT_FOUND", ApiMgmtErrorCode.CLUB_CONFIG_NOT_FOUND);
        ERROR_CODES.put("ENTITY_ALREADY_UNLINKED", ApiMgmtErrorCode.ENTITY_ALREADY_UNLINKED);
        ERROR_CODES.put("ENTITY_ALREADY_LINKED", ApiMgmtErrorCode.ENTITY_ALREADY_LINKED);
        ERROR_CODES.put("LOCKED_EXECUTION", ApiMgmtErrorCode.LOCKED_EXECUTION);
        ERROR_CODES.put("SUBSCRIPTION_MODES_ALLOWED_CHARACTERS", ApiMgmtErrorCode.SUBSCRIPTION_MODES_ALLOWED_CHARACTERS);
        ERROR_CODES.put("RESTRICTIONS_ALLOWED_CHARACTERS", ApiMgmtErrorCode.RESTRICTIONS_ALLOWED_CHARACTERS);
        ERROR_CODES.put("MEMBER_CONFIG_UNIQUE_RESTRICTION_SID", ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_RESTRICTION_SID);
        ERROR_CODES.put("AVET_EVENTS_NOT_FOUND", ApiMgmtErrorCode.AVET_EVENTS_NOT_FOUND);
        ERROR_CODES.put("PARTNER_PIN_REGEX_NOT_VALID", ApiMgmtErrorCode.PARTNER_PIN_REGEX_NOT_VALID);
        ERROR_CODES.put("MEMBER_CONFIG_SUBSCRIPTION_MODE_SID_EXISTS", ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_SID_EXISTS);
    }

    @Autowired
    public MsAvetConfigDatasource(@Value("${clients.services.int-avet-config}") String baseUrl,
                                  ObjectMapper jacksonMapper,
                                  TracingInterceptor tracingInterceptor) {


        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public LiteralMapDTO getBaseLiteral(String language) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBERS_BASE_PATH + "/member-literal-base/{language}")
                .pathParams(language)
                .execute(LiteralMapDTO.class);
    }

    public List<CompetitionDTO> getCompetitions(Long avetCapacityId) {
        return httpClient.buildRequest(HttpMethod.GET, MAIN_BASE_PATH + CAPACITIES + "/{capacityId}/competitions")
                .pathParams(avetCapacityId)
                .execute(ListType.of(CompetitionDTO.class));
    }

    public Match getMatch(Long competitionId, Long matchId) {
        return httpClient.buildRequest(HttpMethod.GET, MAIN_BASE_PATH + COMPETITIONS + COMPETITION_ID + MATCHES + "/{matchId}")
                .pathParams(competitionId, matchId)
                .execute(Match.class);
    }

    public List<Match> getMatches(Long competitionId) {
        return httpClient.buildRequest(HttpMethod.GET, MAIN_BASE_PATH + COMPETITIONS + COMPETITION_ID + MATCHES)
                .pathParams(competitionId)
                .execute(ListType.of(Match.class));
    }

    public List<MatchPriceDTO> getMatchPrices(Long competitionId, Long matchId) {
        return httpClient.buildRequest(HttpMethod.GET,
                        MAIN_BASE_PATH + COMPETITIONS + COMPETITION_ID + MATCHES + "/{matchId}/prices")
                .pathParams(competitionId, matchId)
                .execute(ListType.of(MatchPriceDTO.class));
    }

    public MemberConfigDTO getMemberConfig(String memberClubName) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBERS_BASE_PATH + MEMBER_CONFIG + "/{memberClubName}")
                .pathParams(memberClubName)
                .execute(MemberConfigDTO.class);
    }

    public MemberConfigDTO getMemberConfigByChannel(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBERS_BASE_PATH + MEMBER_CONFIG + "/channel/{channelId}")
                .pathParams(channelId)
                .execute(MemberConfigDTO.class);
    }

    public void deleteMemberConfigByChannel(Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, MEMBERS_BASE_PATH + MEMBER_CONFIG + "/channel/{channelId}")
                .pathParams(channelId)
                .execute();
    }

    public ClubConfig getClubConfigByEntity(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, MAIN_BASE_PATH + "/entities/{entityId}")
                .pathParams(entityId)
                .execute(ClubConfig.class);
    }

    public List<String> getAvailableClubCodes() {
        return httpClient.buildRequest(HttpMethod.GET, MAIN_BASE_PATH + "/clubs/codes/available")
                .execute(ListType.of(String.class));
    }

    public MemberConfigDTO updateMemberConfigByChannel(Long channelId, MemberConfigDTO memberConfigDTO) {
        return httpClient.buildRequest(HttpMethod.PUT, MEMBERS_BASE_PATH + MEMBER_CONFIG + "/channel/{channelId}")
                .pathParams(channelId)
                .body(new ClientRequestBody(memberConfigDTO))
                .execute(MemberConfigDTO.class);
    }

    public void updateDatesFilter(String memberClubName, DatesFilter datesFilter) {
        httpClient.buildRequest(HttpMethod.PUT, MEMBERS_BASE_PATH + "/member-dates-filter/{memberClubName}")
                .pathParams(memberClubName)
                .body(new ClientRequestBody(datesFilter))
                .execute();
    }

    public DatesFilterDTO getDatesFilter(String memberClubName, MemberPeriodType type) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBERS_BASE_PATH + "/member-dates-filter/{memberClubName}")
                .pathParams(memberClubName)
                .params(new QueryParameters.Builder().addQueryParameter("period", type).build())
                .execute(DatesFilterDTO.class);
    }

    public void updateClubConfigByEntity(Long entityId, ClubConfig clubConfig) {
        httpClient.buildRequest(HttpMethod.PUT, MAIN_BASE_PATH + "/entities/{entityId}")
                .pathParams(entityId)
                .body(new ClientRequestBody(clubConfig))
                .execute();
    }

    public void linkClubConfig(Long entityId, LinkClubConfigDTO linkClubConfigDTO) {
        httpClient.buildRequest(HttpMethod.POST, MAIN_BASE_PATH + "/entities/{entityId}/link")
                .pathParams(entityId)
                .body(new ClientRequestBody(linkClubConfigDTO))
                .execute();
    }

    public void unlinkClubConfig(Long entityId) {
        httpClient.buildRequest(HttpMethod.POST, MAIN_BASE_PATH + "/entities/{entityId}/unlink")
                .pathParams(entityId)
                .execute();
    }

    public MemberConfigDTO createMemberConfig(NewMemberConfigDTO newMemberConfigDTO) {
        return httpClient.buildRequest(HttpMethod.POST, MEMBERS_BASE_PATH + "/member-config/{memberClubName}")
                .pathParams(newMemberConfigDTO.getUrl())
                .body(new ClientRequestBody(newMemberConfigDTO))
                .execute(MemberConfigDTO.class);
    }

    public List<CapacityDTO> getCapacities(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES)
                .pathParams(entityId)
                .execute(ListType.of(CapacityDTO.class));
    }

    public void putCapacity(Long entityId, Integer capacityId, UpdateCapacityExternalDTO updateCapacityExternalDTO) {
        httpClient.buildRequest(HttpMethod.PUT,MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/{capacityId}")
                .pathParams(entityId, capacityId)
                .body(new ClientRequestBody(updateCapacityExternalDTO))
                .execute();
    }

    public CapacityDTO getCapacity(Integer venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET,
                        MAIN_BASE_PATH + VENUE_TEMPLATE + "/{venueTemplateId}" + CAPACITY)
                .pathParams(venueTemplateId)
                .execute(CapacityDTO.class);
    }

    public void loadCapacity(Long entityId, Integer capacityId) {
        httpClient.buildRequest(HttpMethod.POST,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/{capacityId}")
                .pathParams(entityId, capacityId)
                .execute();
    }

    public void deleteCapacity(Long entityId, Integer capacityId) {
        httpClient.buildRequest(HttpMethod.DELETE,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/{capacityId}")
                .pathParams(entityId, capacityId)
                .execute();
    }

    public List<AvetEvent> getEventsInformation(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET,
                        MEMBERS_BASE_PATH + "/entity/{entityId}/events")
                .pathParams(entityId)
                .execute(ListType.of(AvetEvent.class));
    }

    public void updateEvents(Long entityId) {
        httpClient.buildRequest(HttpMethod.POST,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/refresh")
                .pathParams(entityId)
                .execute();
    }

    public void updateEvents(Long entityId, Integer capacityId) {
        httpClient.buildRequest(HttpMethod.POST,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/{capacityId}/refresh")
                .pathParams(entityId, capacityId)
                .execute();
    }

    public List<VenueTemplateDTO> getVenueTemplateIdsByCapacityId(Long entityId, Integer capacityId) {
        return httpClient.buildRequest(HttpMethod.GET,
                        MEMBERS_BASE_PATH + ENTITIES + "/{entityId}" + CAPACITIES + "/{capacityId}/relatedVenueTemplates")
                .pathParams(entityId, capacityId)
                .execute(ListType.of(VenueTemplateDTO.class));
    }
}
