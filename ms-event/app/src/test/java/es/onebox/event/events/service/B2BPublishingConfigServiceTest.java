package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.B2BPublishingConfigCouchDao;
import es.onebox.event.events.domain.B2BSeatPublishingConfig;
import es.onebox.event.events.dto.B2BSeatPublishingConfigDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class B2BPublishingConfigServiceTest {

    @Mock
    private B2BPublishingConfigCouchDao b2BPublishingConfigCouchDao;

    @InjectMocks
    private B2BPublishingConfigService b2BPublishingConfigService;

    @Test
    public void testGetConfig() {
        Long eventId = 1L;
        Long channelId = 1L;
        Long venueTemplateId = 1L;

        B2BSeatPublishingConfig mockConfig = new B2BSeatPublishingConfig();
        when(b2BPublishingConfigCouchDao.get(eventId.toString(), channelId.toString(), venueTemplateId.toString())).thenReturn(mockConfig);

        B2BSeatPublishingConfigDTO result = b2BPublishingConfigService.getConfig(eventId, channelId, venueTemplateId);

        assertNotNull(result);
    }

    @Test(expected = OneboxRestException.class)
    public void testGetConfigThrowsExceptionWhenNotFound() {
        Long eventId = 1L;
        Long channelId = 1L;
        Long venueTemplateId = 1L;

        when(b2BPublishingConfigCouchDao.get(eventId.toString(), channelId.toString(), venueTemplateId.toString())).thenReturn(null);

        b2BPublishingConfigService.getConfig(eventId, channelId, venueTemplateId);
    }

    @Test
    public void testUpdateConfig() {
        Long eventId = 1L;
        Long channelId = 1L;
        Long venueTemplateId = 1L;
        B2BSeatPublishingConfigDTO b2BSeatPublishingConfigDTO = new B2BSeatPublishingConfigDTO();

        b2BPublishingConfigService.updateConfig(eventId, channelId, venueTemplateId, b2BSeatPublishingConfigDTO);
        verify(b2BPublishingConfigCouchDao).upsert(anyString(),
                any(B2BSeatPublishingConfig.class));
    }
}