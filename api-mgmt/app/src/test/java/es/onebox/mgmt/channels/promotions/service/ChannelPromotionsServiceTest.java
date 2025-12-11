package es.onebox.mgmt.channels.promotions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionValidityType;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPeriod;
import es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.ChannelPromotionDetailDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDiscountDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.channels.promotions.dto.CreateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionDiscountType;
import es.onebox.mgmt.common.AmountCurrencyDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceType;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.CreateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.repository.ChannelPromotionsRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;

class ChannelPromotionsServiceTest {

	private static final long ENTITY_ID = 1L;
	private static final long CHANNEL_ID = 1L;
	private static final long PROMOTION_ID = 100L;
	@Mock
	private ChannelsHelper channelsHelper;
	@Mock
	private ChannelPromotionsRepository channelPromotionsRepository;
	@Mock
	private EntitiesRepository entitiesRepository;
	@Mock
	private MasterdataService masterdataService;
	@InjectMocks
	private ChannelPromotionsService channelPromotionsService;
	private ChannelResponse channelResponse;
	private Operator operator;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		channelResponse = new ChannelResponse();
		channelResponse.setEntityId(ENTITY_ID);
		channelResponse.setType(ChannelType.OB_PORTAL);
		channelResponse.setSubtype(ChannelSubtype.PORTAL_WEB);

		operator = new Operator();
		operator.setUseMultiCurrency(true);
	}

	@Test
	void getChannelPromotions() {
		ChannelPromotions channelPromotions = new ChannelPromotions();
		ChannelPromotion promotion = new ChannelPromotion();
		channelPromotions.setData(List.of(promotion));
		channelPromotions.setMetadata(new Metadata());
		promotion.setId(PROMOTION_ID);
		promotion.setStatus(PromotionStatus.ACTIVE);
		promotion.setType(ChannelPromotionType.AUTOMATIC);
		ChannelPromotionPeriod validityPeriod = new ChannelPromotionPeriod();
		validityPeriod.setType(ChannelPromotionValidityType.CHANNEL);
		promotion.setValidityPeriod(validityPeriod);
		when(channelPromotionsRepository.getChannelPromotions(any(), any())).thenReturn(channelPromotions);

		ChannelPromotionsDTO result = channelPromotionsService.getChannelPromotions(1L, new ChannelPromotionsFilter());
		assertNotNull(result);
	}

	@Test
	void getChannelPromotion() {

		when(channelsHelper.getAndCheckChannel(any())).thenReturn(channelResponse);
		IdValueDTO idValueDTO = new IdValueDTO();
		idValueDTO.setId(1);
		idValueDTO.setValue("1");
		operator.setCurrency(idValueDTO);
		when(entitiesRepository.getCachedOperator(any())).thenReturn(operator);
		Currency currency = new Currency();
		when(masterdataService.getCurrencies()).thenReturn(List.of(currency));
		ChannelPromotionDetail channelPromotionDetail = new ChannelPromotionDetail();
		channelPromotionDetail.setId(PROMOTION_ID);
		channelPromotionDetail.setStatus(PromotionStatus.ACTIVE);
		channelPromotionDetail.setType(ChannelPromotionType.AUTOMATIC);
		ChannelPromotionPeriod validityPeriod = new ChannelPromotionPeriod();
		validityPeriod.setType(ChannelPromotionValidityType.CHANNEL);
		channelPromotionDetail.setValidityPeriod(validityPeriod);
		when(channelPromotionsRepository.getChannelPromotion(any(), any())).thenReturn(channelPromotionDetail);

		ChannelPromotionDetailDTO result = channelPromotionsService.getChannelPromotion(1L, 1L);
		assertNotNull(result);
	}

	@Test
	void createChannelPromotion_noProviders_shouldWork() {
		CreateChannelPromotionDTO request = new CreateChannelPromotionDTO();
		request.setName("name");
		request.setType(es.onebox.mgmt.channels.promotions.enums.ChannelPromotionType.AUTOMATIC);
		when(channelsHelper.getAndCheckChannel(any())).thenReturn(channelResponse);
		Entity cachedEntity = new Entity();
		cachedEntity.setInventoryProviders(new ArrayList<>());
		when(entitiesRepository.getCachedEntity(any())).thenReturn(cachedEntity);

		IdDTO idDto = new IdDTO();
		idDto.setId(1L);
		when(channelPromotionsRepository.createChannelPromotion(any(), any())).thenReturn(idDto);

		IdDTO result = channelPromotionsService.createChannelPromotion(CHANNEL_ID, request);
		assertNotNull(result);
	}

	@Test
	void createChannelPromotion_providersFound_assertException() {

		CreateChannelPromotionDTO request = new CreateChannelPromotionDTO();
		request.setName("name");
		request.setType(es.onebox.mgmt.channels.promotions.enums.ChannelPromotionType.AUTOMATIC);
		when(channelsHelper.getAndCheckChannel(any())).thenReturn(channelResponse);
		Entity cachedEntity = new Entity();
		cachedEntity.setInventoryProviders(List.of(InventoryProviderEnum.ITALIAN_COMPLIANCE));
		when(entitiesRepository.getCachedEntity(any())).thenReturn(cachedEntity);

		OneboxRestException e = assertThrows(OneboxRestException.class,
				() -> channelPromotionsService.createChannelPromotion(1L, request));
		assertEquals(ApiMgmtErrorCode.PROMOTION_CHANNEL_TYPE_NOT_SUPPORTED.getErrorCode(), e.getErrorCode());
	}

	@Test
	void updateChannelPromotion() {
		UpdateChannelPromotionDTO request = new UpdateChannelPromotionDTO();
		ChannelPromotionDiscountDTO discount = new ChannelPromotionDiscountDTO();
		discount.setType(ChannelPromotionDiscountType.FIXED);

		AmountCurrencyDTO amount1 = new AmountCurrencyDTO();
		amount1.setAmount(10.0);
		amount1.setCurrencyCode("EUR");

		AmountCurrencyDTO amount2 = new AmountCurrencyDTO();
		amount2.setAmount(null);
		amount2.setCurrencyCode("USD");

		discount.setFixedValues(List.of(amount1, amount2));
		request.setDiscount(discount);

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

		OneboxRestException exception = assertThrows(OneboxRestException.class,
				() -> channelPromotionsService.updateChannelPromotion(CHANNEL_ID, PROMOTION_ID, request));

		assertEquals(ApiMgmtPromotionErrorCode.CHANNEL_PROMOTION_AMOUNT_MANDATORY.getErrorCode(),
				exception.getErrorCode());
		verify(channelPromotionsRepository, never()).updateChannelPromotion(any(), any(), any());
	}

	@Test
	void deleteChannelPromotion() {
		ChannelPromotionDetail promotionDetail = new ChannelPromotionDetail();

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
		when(channelPromotionsRepository.getChannelPromotion(CHANNEL_ID, PROMOTION_ID))
				.thenReturn(promotionDetail);

		channelPromotionsService.deleteChannelPromotion(CHANNEL_ID, PROMOTION_ID);

		verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
		verify(channelPromotionsRepository).getChannelPromotion(CHANNEL_ID, PROMOTION_ID);
		verify(channelPromotionsRepository).deleteChannelPromotion(CHANNEL_ID, PROMOTION_ID);
	}

	@Test
	void getAndCheckPromotion() {
		when(channelPromotionsRepository.getChannelPromotion(CHANNEL_ID, PROMOTION_ID))
				.thenReturn(null);

		OneboxRestException exception = assertThrows(OneboxRestException.class,
				() -> channelPromotionsService.getAndCheckPromotion(CHANNEL_ID, PROMOTION_ID));

		assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_PROMOTION_NOT_FOUND.getErrorCode(), exception.getErrorCode());
	}

	@Test
	void getChannelPromotionEvents() {
		ChannelPromotionEvents events = new ChannelPromotionEvents();

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
		when(channelPromotionsRepository.getChannelPromotionEvents(CHANNEL_ID, PROMOTION_ID))
				.thenReturn(events);

		ChannelPromotionEventsDTO result = channelPromotionsService.getChannelPromotionEvents(
				CHANNEL_ID, PROMOTION_ID);

		assertNotNull(result);
		verify(channelPromotionsRepository).getChannelPromotionEvents(CHANNEL_ID, PROMOTION_ID);
	}

	@Test
	void updateChannelPromotionEvents() {
		UpdateChannelPromotionEventsDTO request = new UpdateChannelPromotionEventsDTO();

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

		channelPromotionsService.updateChannelPromotionEvents(CHANNEL_ID, PROMOTION_ID, request);

		verify(channelPromotionsRepository).updateChannelPromotionEvents(
				eq(CHANNEL_ID), eq(PROMOTION_ID), any(UpdateChannelPromotionEvents.class));
	}

	@Test
	void getChannelPromotionSessions() {
		ChannelPromotionSessions sessions = new ChannelPromotionSessions();

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
		when(channelPromotionsRepository.getChannelPromotionSessions(CHANNEL_ID, PROMOTION_ID))
				.thenReturn(sessions);

		ChannelPromotionSessionsDTO result = channelPromotionsService.getChannelPromotionSessions(
				CHANNEL_ID, PROMOTION_ID);

		assertNotNull(result);
		verify(channelPromotionsRepository).getChannelPromotionSessions(CHANNEL_ID, PROMOTION_ID);
	}

	@Test
	void updateChannelPromotionSessions() {
		UpdateChannelPromotionSessionsDTO request = new UpdateChannelPromotionSessionsDTO();

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

		channelPromotionsService.updateChannelPromotionSessions(CHANNEL_ID, PROMOTION_ID, request);

		verify(channelPromotionsRepository).updateChannelPromotionSessions(
				eq(CHANNEL_ID), eq(PROMOTION_ID), any(UpdateChannelPromotionSessions.class));
	}

	@Test
	void cloneChannelPromotion() {
		IdDTO expectedId = new IdDTO();
		expectedId.setId(200L);

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
		when(channelPromotionsRepository.cloneChannelPromotion(eq(CHANNEL_ID), any(CreateChannelPromotion.class)))
				.thenReturn(expectedId);

		IdDTO result = channelPromotionsService.cloneChannelPromotion(CHANNEL_ID, PROMOTION_ID);

		assertNotNull(result);
		assertEquals(expectedId.getId(), result.getId());
		verify(channelPromotionsRepository).cloneChannelPromotion(eq(CHANNEL_ID), any(CreateChannelPromotion.class));
	}

	@Test
	void getChannelPromotionPriceTypes() {
		ChannelPromotionPriceTypes priceTypes = new ChannelPromotionPriceTypes();
		priceTypes.setType(PromotionTargetType.ALL);
		ChannelPromotionPriceType channelPromotionPriceType = new ChannelPromotionPriceType();
		channelPromotionPriceType.setId(1L);
		ChannelPromotionPriceType channelPromotionPriceType1 = new ChannelPromotionPriceType();
		channelPromotionPriceType1.setId(2L);
		priceTypes.setPriceTypes(List.of(channelPromotionPriceType, channelPromotionPriceType1));

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
		when(channelPromotionsRepository.getChannelPromotionPriceTypes(CHANNEL_ID, PROMOTION_ID))
				.thenReturn(priceTypes);

		ChannelPromotionPriceTypesDTO result = channelPromotionsService.getChannelPromotionPriceTypes(
				CHANNEL_ID, PROMOTION_ID);

		assertNotNull(result);
		assertEquals(PromotionTargetType.ALL, result.getType());
		verify(channelPromotionsRepository).getChannelPromotionPriceTypes(CHANNEL_ID, PROMOTION_ID);
	}

	@Test
	void updateChannelPromotionPriceTypes() {
		UpdateChannelPromotionPriceTypesDTO body = new UpdateChannelPromotionPriceTypesDTO();
		body.setType(PromotionTargetType.ALL);

		when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

		channelPromotionsService.updateChannelPromotionPriceTypes(CHANNEL_ID, PROMOTION_ID, body);

		verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
		verify(channelPromotionsRepository).updateChannelPromotionPriceTypes(
				eq(CHANNEL_ID),
				eq(PROMOTION_ID),
				any());
	}
}