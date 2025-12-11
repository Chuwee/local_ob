package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.packs.dto.CreatePackItemDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypeMappingRequestDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("PacksValidationService - validateCreatePackItems Tests for AUTOMATIC Packs")
class PacksValidationServiceTest {

    @Mock
    private ChannelsRepository channelsRepository;
    
    @Mock
    private ProductsRepository productsRepository;
    
    @Mock
    private SessionsRepository sessionsRepository;
    
    @Mock
    private PacksRepository packsRepository;
    
    @Mock
    private ChannelsHelper channelsHelper;
    
    @Mock
    private ValidationService validationService;
    
    @Mock
    private SecurityManager securityManager;
    
    @Mock
    private PacksHelper packsHelper;

    @InjectMocks
    private PacksValidationService packsValidationService;

    private CreatePackItemsDTO createPackItemsDTO;
    private List<PackItem> existingItems;
    private PackItem mainItem;
    private Session mockSession;
    private Session mockMainItemSession;
    private PriceTypes mockPriceTypes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createPackItemsDTO = new CreatePackItemsDTO();
        existingItems = new ArrayList<>();
        mainItem = createMockPackItem(1L, PackItemType.SESSION, true);
        mockSession = createMockSession(100L, 200L);
        mockMainItemSession = createMockSession(100L, 1L); // Session for main item
        mockPriceTypes = createMockPriceTypes();
    }

    @Nested
    @DisplayName("Basic Validations")
    class BasicValidationsTest {

        @Test
        @DisplayName("Should throw exception when trying to create EVENT type item")
        void shouldThrowExceptionWhenCreatingEventTypeItem() {
            // Given
            CreatePackItemDTO eventItem = createMockCreatePackItemDTO(1L, PackItemType.EVENT);
            createPackItemsDTO.add(eventItem);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_CANNOT_BE_EVENT_TYPE.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when trying to create duplicate item")
        void shouldThrowExceptionWhenCreatingDuplicateItem() {
            // Given
            PackItem existingItem = createMockPackItem(1L, PackItemType.SESSION, false);
            existingItems.add(existingItem);
            
            CreatePackItemDTO duplicateItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            createPackItemsDTO.add(duplicateItem);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEMS_ALREADY_IN_PACK.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should not throw exception when no existing items")
        void shouldNotThrowExceptionWhenNoExistingItems() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(1); // Add valid price type ID
            createPackItemsDTO.add(sessionItem);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(Collections.emptyList())).thenReturn(null);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, null)).thenReturn(false);
            when(sessionsRepository.getPriceTypes(mockSession.getEventId(), mockSession.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, Collections.emptyList(), createPackItemsDTO));
        }
    }

    @Nested
    @DisplayName("AUTOMATIC Pack Session Item Validations")
    class AutomaticPackValidationsTest {

        @Test
        @DisplayName("Should throw exception when session with main template has priceTypeId")
        void shouldThrowExceptionWhenSessionWithMainTemplateHasPriceTypeId() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(10);
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(true);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_WITH_MAIN_TEMPLATE_CANNOT_HAVE_PRICE_TYPE_ID.getErrorCode(), 
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when session without main template has neither priceTypeId nor priceTypeMapping")
        void shouldThrowExceptionWhenSessionWithoutMainTemplateHasNeitherPriceTypeIdNorMapping() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(null);
            sessionItem.setPriceTypeMapping(null);
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_PRICE_TYPE_ID_CANNOT_BE_NULL.getErrorCode(), 
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when session without main template has both priceTypeId and priceTypeMapping")
        void shouldThrowExceptionWhenSessionWithoutMainTemplateHasBothPriceTypeIdAndMapping() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(10);
            sessionItem.setPriceTypeMapping(Collections.singletonList(createMockPriceTypeMapping(1, Arrays.asList(2, 3))));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_PRICE_TYPE_ID_OR_MAPPING.getErrorCode(), 
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when priceTypeId does not exist in session")
        void shouldThrowExceptionWhenPriceTypeIdNotInSession() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(999); // ID that doesn't exist in session
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            // Mock for priceTypeId validation (uses current session)
            when(sessionsRepository.getPriceTypes(mockSession.getEventId(), mockSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should not throw exception when priceTypeId exists in session")
        void shouldNotThrowExceptionWhenPriceTypeIdExistsInSession() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(1); // ID that exists in mockPriceTypes
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            // Mock for priceTypeId validation (uses current session)
            when(sessionsRepository.getPriceTypes(mockSession.getEventId(), mockSession.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }

        @Test
        @DisplayName("Should not throw exception when session has main template and no priceTypeId")
        void shouldNotThrowExceptionWhenSessionHasMainTemplateAndNoPriceTypeId() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(null);
            sessionItem.setPriceTypeMapping(null);
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(true);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }
    }

    @Nested
    @DisplayName("Price Type Mapping Validations")
    class PriceTypeMappingValidationsTest {

        @Test
        @DisplayName("Should throw exception when priceTypeMapping count does not match total price types")
        void shouldThrowExceptionWhenPriceTypeMappingCountDoesNotMatchTotal() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            // Only mapping 2 price types when there are 3 total
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10, 11)),
                    createMockPriceTypeMapping(2, Arrays.asList(12, 13))
                    // Missing mapping for price type 3
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when sourcePriceTypeId in mapping does not exist in session")
        void shouldThrowExceptionWhenSourcePriceTypeIdNotInSession() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(999, Arrays.asList(10, 11)), // 999 doesn't exist
                    createMockPriceTypeMapping(2, Arrays.asList(12, 13)),
                    createMockPriceTypeMapping(3, Arrays.asList(14, 15))
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should not throw exception when priceTypeMapping is valid")
        void shouldNotThrowExceptionWhenPriceTypeMappingIsValid() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10)),
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }

        @Test
        @DisplayName("Should correctly validate mapping with one-to-one target price types")
        void shouldValidateMappingWithDifferentTargetPriceTypeCounts() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10)), // One-to-one
                    createMockPriceTypeMapping(2, Arrays.asList(12)), // One-to-one
                    createMockPriceTypeMapping(3, Arrays.asList(15)) // One-to-one
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }

        @Test
        @DisplayName("Should validate that all source price type IDs are covered")
        void shouldValidateThatAllSourcePriceTypeIdsAreCovered() {
            // Given - Missing one source price type ID
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10)),
                    createMockPriceTypeMapping(3, Arrays.asList(12))
                    // Missing price type 2
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when target price type list is empty")
        void shouldThrowExceptionWhenTargetPriceTypeListIsEmpty() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Collections.emptyList()), // Empty target list - should fail
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then - Should throw exception for empty target list
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.INVALID_TARGET_PRICE_TYPE_ID.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when target price type list has more than one element")
        void shouldThrowExceptionWhenTargetPriceTypeListHasMultipleElements() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10, 11)), // Multiple elements - should fail
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            createPackItemsDTO.add(sessionItem);

            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then - Should throw exception for multiple elements in target list
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.INVALID_TARGET_PRICE_TYPE_ID.getErrorCode(), exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle empty existing items list correctly")
        void shouldHandleEmptyExistingItemsList() {
            // Given
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(1); // Add valid price type ID
            createPackItemsDTO.add(sessionItem);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getMainItem(Collections.emptyList())).thenReturn(null);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, null)).thenReturn(false);
            when(sessionsRepository.getPriceTypes(mockSession.getEventId(), mockSession.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, Collections.emptyList(), createPackItemsDTO));
        }

        @Test
        @DisplayName("Should handle no session pack items to create")
        void shouldHandleNoSessionPackItemsToCreate() {
            // Given - Only products, no sessions
            CreatePackItemDTO productItem = createMockCreatePackItemDTO(1L, PackItemType.PRODUCT);
            createPackItemsDTO.add(productItem);
            
            // Configure product item for AUTOMATIC pack
            productItem.setSharedBarcode(true);
            productItem.setDeliveryPointId(1);
            
            // Mock the product validation
            Product mockProduct = new Product();
            mockProduct.setProductType(es.onebox.mgmt.products.enums.ProductType.SIMPLE);
            
            DeliveryPoint mockDeliveryPoint = new DeliveryPoint();
            IdNameDTO mockEntity = new IdNameDTO();
            mockEntity.setId(100L);
            mockDeliveryPoint.setEntity(mockEntity);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(productItem));
            when(validationService.getAndCheckProduct(productItem.getItemId()))
                    .thenReturn(mockProduct);
            when(productsRepository.getDeliveryPoint(1L)).thenReturn(mockDeliveryPoint);

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }

        @Test
        @DisplayName("Should handle no product pack items to create")
        void shouldHandleNoProductPackItemsToCreate() {
            // Given - Only sessions, no products
            CreatePackItemDTO sessionItem = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            sessionItem.setPriceTypeId(1); // Add valid price type ID
            createPackItemsDTO.add(sessionItem);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Collections.singletonList(sessionItem));
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Collections.singletonList(mockSession));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem, Collections.singletonList(mockSession)))
                    .thenReturn(mockSession);
            when(packsHelper.sessionHasMainTemplate(mockSession, mainItem)).thenReturn(false);
            // Mock for priceTypeId validation (uses current session)
            when(sessionsRepository.getPriceTypes(mockSession.getEventId(), mockSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));
        }

        @Test
        @DisplayName("Should process multiple session items correctly")
        void shouldProcessMultipleSessionItemsCorrectly() {
            // Given
            CreatePackItemDTO sessionItem1 = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            CreatePackItemDTO sessionItem2 = createMockCreatePackItemDTO(2L, PackItemType.SESSION);
            sessionItem1.setPriceTypeId(1); // Add valid price type IDs
            sessionItem2.setPriceTypeId(2);
            createPackItemsDTO.addAll(Arrays.asList(sessionItem1, sessionItem2));
            
            Session session1 = createMockSession(100L, 1L);
            Session session2 = createMockSession(100L, 2L);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Arrays.asList(sessionItem1, sessionItem2));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Arrays.asList(session1, session2));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem1, Arrays.asList(session1, session2)))
                    .thenReturn(session1);
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem2, Arrays.asList(session1, session2)))
                    .thenReturn(session2);
            when(packsHelper.sessionHasMainTemplate(session1, mainItem)).thenReturn(false);
            when(packsHelper.sessionHasMainTemplate(session2, mainItem)).thenReturn(false);
            when(sessionsRepository.getPriceTypes(session1.getEventId(), session1.getId()))
                    .thenReturn(mockPriceTypes);
            when(sessionsRepository.getPriceTypes(session2.getEventId(), session2.getId()))
                    .thenReturn(mockPriceTypes);
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            verify(packsHelper, times(1)).validateSessionStatus(session1);
            verify(packsHelper, times(1)).validateSessionStatus(session2);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should process complete AUTOMATIC scenario with valid priceTypeMapping")
        void shouldProcessCompleteAutomaticScenarioWithValidPriceTypeMapping() {
            // Given - Complete scenario with session items with price type mapping
            CreatePackItemDTO sessionItem1 = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            CreatePackItemDTO sessionItem2 = createMockCreatePackItemDTO(2L, PackItemType.SESSION);
            
            // Configure valid price type mapping (one-to-one)
            sessionItem1.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10)),
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            
            sessionItem2.setPriceTypeId(2); // Another item with direct priceTypeId
            
            createPackItemsDTO.addAll(Arrays.asList(sessionItem1, sessionItem2));
            
            Session session1 = createMockSession(100L, 1L);
            Session session2 = createMockSession(100L, 2L);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Arrays.asList(sessionItem1, sessionItem2));
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Arrays.asList(session1, session2));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem1, Arrays.asList(session1, session2)))
                    .thenReturn(session1);
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem2, Arrays.asList(session1, session2)))
                    .thenReturn(session2);
            when(packsHelper.sessionHasMainTemplate(session1, mainItem)).thenReturn(false);
            when(packsHelper.sessionHasMainTemplate(session2, mainItem)).thenReturn(false);
            // Mock main item session for priceTypeMapping validation
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);
            // Mock session price types for individual priceTypeId validation
            when(sessionsRepository.getPriceTypes(session2.getEventId(), session2.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            // Verify that both sessions were validated
            verify(packsHelper, times(1)).validateSessionStatus(session1);
            verify(packsHelper, times(1)).validateSessionStatus(session2);
            verify(sessionsRepository, times(2)).getPriceTypes(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Should fail correctly in complex scenario with invalid mapping")
        void shouldFailCorrectlyInComplexScenarioWithInvalidMapping() {
            // Given - Scenario with partially invalid mapping
            CreatePackItemDTO sessionItem1 = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            CreatePackItemDTO sessionItem2 = createMockCreatePackItemDTO(2L, PackItemType.SESSION);
            
            // sessionItem1 has valid mapping (one-to-one)
            sessionItem1.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(1, Arrays.asList(10)),
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            
            // sessionItem2 has mapping with non-existent source price type
            sessionItem2.setPriceTypeMapping(Arrays.asList(
                    createMockPriceTypeMapping(999, Arrays.asList(10)), // 999 doesn't exist
                    createMockPriceTypeMapping(2, Arrays.asList(12)),
                    createMockPriceTypeMapping(3, Arrays.asList(14))
            ));
            
            createPackItemsDTO.addAll(Arrays.asList(sessionItem1, sessionItem2));
            
            Session session1 = createMockSession(100L, 1L);
            Session session2 = createMockSession(100L, 2L);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Arrays.asList(sessionItem1, sessionItem2));
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Arrays.asList(session1, session2));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem1, Arrays.asList(session1, session2)))
                    .thenReturn(session1);
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionItem2, Arrays.asList(session1, session2)))
                    .thenReturn(session2);
            when(packsHelper.sessionHasMainTemplate(session1, mainItem)).thenReturn(false);
            when(packsHelper.sessionHasMainTemplate(session2, mainItem)).thenReturn(false);
            // Mock main item session for priceTypeMapping validation (both items use priceTypeMapping)
            when(sessionsRepository.getSession(mainItem.getItemId())).thenReturn(mockMainItemSession);
            when(sessionsRepository.getPriceTypes(mockMainItemSession.getEventId(), mockMainItemSession.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then - Should fail on the second item
            OneboxRestException exception = assertThrows(OneboxRestException.class,
                    () -> packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            assertEquals(ApiMgmtSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(), exception.getErrorCode());
        }

        @Test
        @DisplayName("Should handle mixed session items with main template and without")
        void shouldHandleMixedSessionItemsWithMainTemplateAndWithout() {
            // Given - Mix of sessions: some with main template, some without
            CreatePackItemDTO sessionWithMainTemplate = createMockCreatePackItemDTO(1L, PackItemType.SESSION);
            CreatePackItemDTO sessionWithoutMainTemplate = createMockCreatePackItemDTO(2L, PackItemType.SESSION);
            
            sessionWithoutMainTemplate.setPriceTypeId(2); // This one needs price configuration
            
            createPackItemsDTO.addAll(Arrays.asList(sessionWithMainTemplate, sessionWithoutMainTemplate));
            
            Session session1 = createMockSession(100L, 1L);
            Session session2 = createMockSession(100L, 2L);
            
            when(packsHelper.getCreateSessionPackItems(createPackItemsDTO))
                    .thenReturn(Arrays.asList(sessionWithMainTemplate, sessionWithoutMainTemplate));
            when(packsHelper.getCreateProductPackItems(createPackItemsDTO))
                    .thenReturn(Collections.emptyList());
            when(packsHelper.getMainItem(existingItems)).thenReturn(mainItem);
            when(packsHelper.getSessionsFromCreateSessionPackItems(anyList()))
                    .thenReturn(Arrays.asList(session1, session2));
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionWithMainTemplate, Arrays.asList(session1, session2)))
                    .thenReturn(session1);
            when(packsHelper.getSessionFromCreateSessionPackItem(sessionWithoutMainTemplate, Arrays.asList(session1, session2)))
                    .thenReturn(session2);
            when(packsHelper.sessionHasMainTemplate(session1, mainItem)).thenReturn(true);  // Has main template
            when(packsHelper.sessionHasMainTemplate(session2, mainItem)).thenReturn(false); // No main template
            when(sessionsRepository.getPriceTypes(session2.getEventId(), session2.getId()))
                    .thenReturn(mockPriceTypes);

            // When & Then
            assertDoesNotThrow(() -> 
                packsValidationService.validateCreatePackItems(PackType.AUTOMATIC, existingItems, createPackItemsDTO));

            // Verify price type validation only called for session without main template
            verify(sessionsRepository, times(1)).getPriceTypes(session2.getEventId(), session2.getId());
            verify(sessionsRepository, never()).getPriceTypes(session1.getEventId(), session1.getId());
        }
    }

    // Helper methods to create mock objects
    private CreatePackItemDTO createMockCreatePackItemDTO(Long itemId, PackItemType type) {
        CreatePackItemDTO dto = new CreatePackItemDTO();
        dto.setItemId(itemId);
        dto.setType(type);
        dto.setDisplayItemInChannels(true);
        return dto;
    }

    private PackItem createMockPackItem(Long itemId, PackItemType type, boolean isMain) {
        PackItem item = new PackItem();
        item.setItemId(itemId);
        item.setType(type);
        item.setMain(isMain);
        return item;
    }

    private Session createMockSession(Long eventId, Long sessionId) {
        Session session = new Session();
        session.setEventId(eventId);
        session.setId(sessionId);
        return session;
    }

    private PriceTypes createMockPriceTypes() {
        PriceTypes priceTypes = new PriceTypes();
        
        PriceType priceType1 = new PriceType();
        priceType1.setId(1L);
        
        PriceType priceType2 = new PriceType();
        priceType2.setId(2L);
        
        PriceType priceType3 = new PriceType();
        priceType3.setId(3L);
        
        priceTypes.setData(Arrays.asList(priceType1, priceType2, priceType3));
        
        // Mock metadata
        Metadata metadata = new Metadata();
        metadata.setTotal(3L);
        priceTypes.setMetadata(metadata);
        
        return priceTypes;
    }

    private PackItemPriceTypeMappingRequestDTO createMockPriceTypeMapping(Integer sourcePriceTypeId, List<Integer> targetPriceTypeIds) {
        PackItemPriceTypeMappingRequestDTO mapping = new PackItemPriceTypeMappingRequestDTO();
        mapping.setSourcePriceTypeId(sourcePriceTypeId);
        mapping.setTargetPriceTypeId(targetPriceTypeIds);
        return mapping;
    }
}
