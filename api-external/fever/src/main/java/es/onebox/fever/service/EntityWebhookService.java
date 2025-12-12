package es.onebox.fever.service;

import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.webhook.dto.fever.EntityDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.FeverAddressDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.dto.city.FeverCityData;
import es.onebox.fever.repository.FeverRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityWebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityWebhookService.class);

    private final EntitiesRepository entitiesRepository;
    private final MasterDataRepository masterDataRepository;
    private final FeverRepository feverRepository;

    public EntityWebhookService(EntitiesRepository entitiesRepository, MasterDataRepository masterDataRepository,
                                FeverRepository feverRepository) {
        this.entitiesRepository = entitiesRepository;
        this.masterDataRepository = masterDataRepository;
        this.feverRepository = feverRepository;
    }

    public WebhookFeverDTO sendEntityFvZoneData(WebhookFeverDTO webhookFever) {

        Long entityId = Long.valueOf(webhookFever.getNotificationMessage().getId());

        EntityDTO entity  = entitiesRepository.getById(entityId);

        validateIfEntityNeedsNotification(entity, webhookFever);

        if (Boolean.FALSE.equals(webhookFever.getAllowSend())){
            return webhookFever;
        }

        List<CountryDTO> countries = this.masterDataRepository.countries();

        FeverAddressDTO address = getFeverAddress(countries, entity.getCountryId(), entity.getAddress(), entity.getCity());
        FeverAddressDTO invoiceAddress = getFeverAddress(countries, entity.getInvoiceCountryId(), entity.getInvoiceAddress(), entity.getInvoiceCity());

        EntityDetailDTO entityDetail = new EntityDetailDTO();
        entityDetail.setPhone(entity.getPhone());
        entityDetail.setBusinessName(entity.getSocialReason());
        entityDetail.setNif(entity.getNif());
        entityDetail.setAddress(address);
        entityDetail.setInvoiceAddress(invoiceAddress);
        entityDetail.setFvId(entity.getExternalReference() != null ? Integer.valueOf(entity.getExternalReference()) : null);
        entityDetail.setName(entity.getName());
        entityDetail.setEmail(entity.getEmail());

        webhookFever.getFeverMessage().setId(entityId.toString());
        webhookFever.getFeverMessage().setEntityDetail(entityDetail);

        LOGGER.info("[FEVER WEBHOOK] Entity with id: {} was received for sync with fever.", entityId);

        return webhookFever;
    }

    public void validateIfEntityNeedsNotification(EntityDTO entity, WebhookFeverDTO webhookFever) {
        if (StringUtils.isEmpty(webhookFever.getHeaders().getHeader("ob-action"))) {
            webhookFever.setAllowSend(Boolean.FALSE);
            return;
        }

        boolean ret = Boolean.TRUE;

        if (entity == null || entity.getId() == null || entity.getName() == null || entity.getEmail() == null ||
                entity.getNif() == null || entity.getCountryId() == null || entity.getSocialReason() == null) {
            ret = Boolean.FALSE;
        }

        if (Boolean.TRUE.equals(ret) && "CREATE".equals(webhookFever.getHeaders().getHeader("ob-action"))) {
            ret = StringUtils.isEmpty(entity.getExternalReference());
        }

        webhookFever.setAllowSend(ret);
    }

    private FeverAddressDTO getFeverAddress(List<CountryDTO> countries, Integer countryId, String address, String city) {
        CountryDTO country = countries.stream().filter(e ->
                e.getId().equals(countryId)).findFirst().orElse(null);
        FeverAddressDTO feverAddress = new FeverAddressDTO();
        feverAddress.setAddress(address);

        if (country != null) {
            feverAddress.setCountryCode(country.getCode());
            FeverCityData cityData = feverRepository.getCity(city, country.getCode());
            if (CollectionUtils.isNotEmpty(cityData.getData().getCities())) {
                feverAddress.setCityCriteriaId(cityData.getData().getCities().get(0).getId());
            }
        }
        return feverAddress;
    }

}
