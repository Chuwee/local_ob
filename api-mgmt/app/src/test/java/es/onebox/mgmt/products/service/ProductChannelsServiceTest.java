package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannels;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.events.dto.channel.ChannelEntityDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ProductChannelsServiceTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private ProductsRepository productsRepository;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private UsersRepository usersRepository;
    @InjectMocks
    private ProductChannelsService productChannelsService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void requestChannelApproval() {
        Long channelId = 1L;
        Long productId = 2L;

        ProductChannels productChannels = new ProductChannels();
        when(validationService.getAndCheckProduct(anyLong())).thenReturn(null);
        when(productsRepository.getProductChannels(anyLong())).thenReturn(productChannels);

        User user = new User();
        user.setId(99L);
        when(usersRepository.getUser(any(), any(), any()))
                .thenReturn(user);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getUsername).thenReturn("test-user");
            securityMock.when(SecurityUtils::getUserOperatorId).thenReturn(123L);
            securityMock.when(SecurityUtils::getApiKey).thenReturn("api-key-test");

            //Fail when there is no matching product channel
            Assertions.assertThrows(OneboxRestException.class, () ->
                    productChannelsService.requestChannelApproval(productId, channelId));

            ProductChannel productChannel = new ProductChannel();
            productChannel.setProduct(new IdNameDTO(productId, null));
            productChannels.add(productChannel);

            ProductChannelInfo channelInfo = new ProductChannelInfo();
            channelInfo.setId(channelId);

            productChannel.setChannel(channelInfo);

            productChannelsService.requestChannelApproval(productId, channelId);
        }
    }
}