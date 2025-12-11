package es.onebox.mgmt.datasources.integration.avetconfig.repository;

import es.onebox.mgmt.channels.dto.DatesFilter;
import es.onebox.mgmt.channels.dto.DatesFilterDTO;
import es.onebox.mgmt.channels.dto.NewMemberConfigDTO;
import es.onebox.mgmt.channels.members.dto.AvetEvent;
import es.onebox.mgmt.datasources.integration.avetconfig.MsAvetConfigDatasource;
import es.onebox.mgmt.datasources.integration.avetconfig.VenueConfigConverterDatasource;
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
import es.onebox.mgmt.sessions.dto.IntegrationEventEntityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AvetConfigRepository {
    MsAvetConfigDatasource datasource;
    VenueConfigConverterDatasource venueConfigConverterDatasource;

    @Autowired
    public AvetConfigRepository(MsAvetConfigDatasource datasource, VenueConfigConverterDatasource venueConfigConverterDatasource) {
        this.datasource = datasource;
        this.venueConfigConverterDatasource = venueConfigConverterDatasource;
    }

    public LiteralMapDTO getBaseLiteral(String language) {
        return datasource.getBaseLiteral(language);
    }

    public List<CompetitionDTO> getCompetitions(Long avetCapacityId) {
        return datasource.getCompetitions(avetCapacityId);
    }

    public Match getMatch(Long competitionId, Long matchId) {
        return datasource.getMatch(competitionId, matchId);
    }

    public List<Match> getMatches(Long competitionId) {
        return datasource.getMatches(competitionId);
    }

    public IntegrationEventEntityDTO getIntegrationEvents (Long sessionId){
        return venueConfigConverterDatasource.getIntegrationEvents(sessionId);
    }

    public List<MatchPriceDTO> getMatchPrices(Long competitionId, Long matchId) {
        return datasource.getMatchPrices(competitionId, matchId);
    }

    public List<CapacityDTO> getCapacities(Long entityId) {
        return datasource.getCapacities(entityId);
    }

    public List<VenueTemplateDTO> getVenueTemplateIdsByCapacityId(Long entityId, Integer capacityId) {
        return datasource.getVenueTemplateIdsByCapacityId(entityId, capacityId);
    }

    public CapacityDTO getCapacity(Integer venueTemplateId) {
        return datasource.getCapacity(venueTemplateId);
    }

    public MemberConfigDTO getMemberConfig(String memberClubName) {
        return datasource.getMemberConfig(memberClubName);
    }
    public MemberConfigDTO getMemberConfigByChannel(Long channelId) {
        return datasource.getMemberConfigByChannel(channelId);
    }

    public void deleteMemberConfigByChannel(Long channelId) {
        datasource.deleteMemberConfigByChannel(channelId);
    }

    public ClubConfig getClubConfigByEntity(Long entityId) {
        return datasource.getClubConfigByEntity(entityId);
    }

    public void updateClubConfigByEntity(Long entityId, ClubConfig clubConfig) {
        datasource.updateClubConfigByEntity(entityId, clubConfig);
    }

    public void linkClubConfig(Long entityId, LinkClubConfigDTO linkClubConfigDTO) {
        datasource.linkClubConfig(entityId, linkClubConfigDTO);
    }

    public void unlinkClubConfig(Long entityId) {
        datasource.unlinkClubConfig(entityId);
    }

    public List<String> getAvailableClubCodes() {
        return datasource.getAvailableClubCodes();
    }

    public MemberConfigDTO updateMemberConfigByChannel(Long channelId, MemberConfigDTO memberConfigDTO) {
        return datasource.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void updateDatesFilter(String memberClubName, DatesFilter datesFilter) {
        datasource.updateDatesFilter(memberClubName, datesFilter);
    }

    public DatesFilterDTO getDatesFilter(String memberClubName, MemberPeriodType type) {
        return datasource.getDatesFilter(memberClubName, type);
    }

    public void createTemplateMappings(Long venueTemplateId) {
        venueConfigConverterDatasource.createTemplateMappings(venueTemplateId);
    }

    public void createSessionTicketsMappings (Long sessionId, Long venueTemplateId) {
        venueConfigConverterDatasource.createSessionTicketsMappings(sessionId, venueTemplateId);
    }

    public void createSessionMappingFull (Long sessionId, Long venueTemplateId) {
        venueConfigConverterDatasource.createSessionMappingFull(sessionId, venueTemplateId);
    }

    public void createSessionsTicketsMappings (Long entityId, Integer capacityId) {
        venueConfigConverterDatasource.createSessionsTicketsMappings(entityId, capacityId);
    }

    public void createSessionsMappingsFull (Long entityId, Integer capacityId) {
        venueConfigConverterDatasource.createSessionMappingsFull(entityId, capacityId);
    }

    public MemberConfigDTO createMemberConfig(NewMemberConfigDTO newMemberConfigDTO) {
        return datasource.createMemberConfig(newMemberConfigDTO);
    }

    public List<AvetEvent> getEventsInformation(Long entityId) {
        return datasource.getEventsInformation(entityId);
    }

    public void loadCapacity(Long entityId, Integer capacityId) {
        datasource.loadCapacity(entityId, capacityId);
    }

    public void updateEvents(Long entityId) {
        datasource.updateEvents(entityId);
    }

    public void updateEvents(Long entityId, Integer capacityId) {
        datasource.updateEvents(entityId, capacityId);
    }

    public void deleteCapacity(Long entityId, Integer capacityId) {
        datasource.deleteCapacity(entityId, capacityId);
    }

    public void putCapacity(Long entityId, Integer capacityId, UpdateCapacityExternalDTO updateCapacityExternalDTO) {
        datasource.putCapacity(entityId, capacityId, updateCapacityExternalDTO);
    }
}
