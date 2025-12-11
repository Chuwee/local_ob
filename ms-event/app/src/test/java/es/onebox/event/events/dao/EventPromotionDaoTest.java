package es.onebox.event.events.dao;

import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PromotionCommElemRecord;
import es.onebox.event.promotions.dao.PromotionTemplateDao;
import es.onebox.jooq.dao.test.RealDaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventPromotionDaoTest extends RealDaoImplTest {

    private final Integer EVENT_ID = 68;
    private final Integer INVALID_EVENT_ID = 666;
    private final Integer EVENT_PROMOTION_TEMPLATE_ID = 60;

    private final Integer EXPECTED_PROMOTION_TEMPLATE_ID = 61;
    private final String EXPECTED_NAME = "Auto -1€";
    private final Integer EXPECTED_STATUS = 1;
    private final Boolean EXPECTED_ACTIVE = Boolean.TRUE;
    private final Integer EXPECTED_SUBTYPE = 1;
    private final Integer EXPECTED_VALIDATION_PERIOD_TYPE = 0;
    private final Date EXPECTED_DATE_FROM = initializeDate("2012-05-31 22:00:00.000");
    private final Date EXPECTED_DATE_TO = initializeDate("2012-07-31 22:00:00.000");
    private final Integer EXPECTED_DISCOUNT_TYPE = 0;
    private final Double EXPECTED_FIXED_DISCOUNT_VALUE = 1.0;
    private final Double EXPECTED_PERCENTUAL_DISCOUNT_VALUE = 10.0;
    private final Boolean EXPECTED_SPECIFIC_CHANNEL_RECHARGE = Boolean.FALSE;
    private final Boolean EXPECTED_SPECIFIC_PROMOTER_RECHARGE = Boolean.FALSE;
    private final Boolean EXPECTED_EXCLUSIVE_SALE = Boolean.FALSE;
    private final Boolean EXPECTED_SELF_MANAGED_SALE = Boolean.TRUE;
    private final Integer EXPECTED_COLLECTIVE_ID = 53;
    private final Integer EXPECTED_COLLECTIVE_TYPE = 4;
    private final Integer EXPECTED_COLLECTIVE_SUBTYPE = 1;

    private final String EXPECTED_CHANNELS = "93||94";
    private final String EXPECTED_PRICEZONES = "11||12";
    private final String EXPECTED_RATES = "21||22";

    private final String EXPECTED_RANGES = "0::0::5||5::10.5::25";
    private final Boolean EXPECTED_BLOCK_SECONDARY_MARKET = Boolean.FALSE;

    @InjectMocks
    private EventPromotionTemplateDao eventPromotionTemplateDao;

    @InjectMocks
    private PromotionTemplateDao promotionTemplateDao;

    @BeforeEach
    public void setUp() {
        super.setUp();

        ReflectionTestUtils.setField(promotionTemplateDao, "dsl", dsl);
    }

    @Override
    protected String getDatabaseFile() {
        return "dao/EventPromotionsDao.sql";
    }

    @Test
    public void getPromotionByEventIdTest() {
        List<EventPromotionRecord> promos = eventPromotionTemplateDao.getPromotionsByEventId(EVENT_ID);
        assertNotNull(promos);
        assertEquals(9, promos.size(), "Expected 9 promos to be returned.");

        EventPromotionRecord eventPromotionRecord = findEventPromotionByEventPromotionTemplateId(EVENT_PROMOTION_TEMPLATE_ID, promos);

        List<Integer> eventPromotionIds = promos.stream()
                .map(EventPromotionRecord::getEventPromotionTemplateId).collect(Collectors.toList());
        List<Integer> promotionTemplateId = promos.stream()
                .map(EventPromotionRecord::getPromotionTemplateId).collect(Collectors.toList());

        assertEquals(9, eventPromotionIds.size());
        assertEquals(9, promotionTemplateId.size());

        Map<Integer, List<Long>> sessionsPromoted = this.eventPromotionTemplateDao.getPromotedSessionsByPromotionEventIds(eventPromotionIds);
        Map<Integer, List<PromotionCommElemRecord>> commElements = this.promotionTemplateDao.getCommunicationElementsByPromotionTemplateIds(promotionTemplateId);

        validate(sessionsPromoted);
        validateCommunicationElements(commElements);
        validate(EXPECTED_PROMOTION_TEMPLATE_ID.equals(eventPromotionRecord.getPromotionTemplateId()), "Template promotion ID doesn't match");
        validate(EXPECTED_NAME.equals(eventPromotionRecord.getName()), "Promotion name doesn't match");
        validate(EXPECTED_STATUS.equals(eventPromotionRecord.getStatus()), "Promotion status doesn't match");
        validate(EXPECTED_ACTIVE.equals(eventPromotionRecord.getActive()), "Promotion active doesn't match");
        validate(EXPECTED_SUBTYPE.equals(eventPromotionRecord.getSubtype()), "Promotion subtype doesn't match");
        validate(EXPECTED_VALIDATION_PERIOD_TYPE.equals(eventPromotionRecord.getValidationPeriodType()), "Promotion validation period type doesn't match");
        validate(EXPECTED_DATE_FROM.equals(eventPromotionRecord.getDateFrom()), "Promotion from date doesn't match");
        validate(EXPECTED_DATE_TO.equals(eventPromotionRecord.getDateTo()), "Promotion to date doesn't match");
        validate(EXPECTED_DISCOUNT_TYPE.equals(eventPromotionRecord.getDiscountType()), "Promotion discount type doesn't match");
        validate(EXPECTED_FIXED_DISCOUNT_VALUE.equals(eventPromotionRecord.getFixedDiscountValue()), "Promotion discount fixed value doesn't match");
        validate(EXPECTED_PERCENTUAL_DISCOUNT_VALUE.equals(eventPromotionRecord.getPercentualDiscountValue()), "Promotion discount percentual value doesn't match");
        validate(EXPECTED_SPECIFIC_CHANNEL_RECHARGE.equals(eventPromotionRecord.getApplyChannelSpecificCharges()), "Promotion specific channel charges doesn't match");
        validate(EXPECTED_SPECIFIC_PROMOTER_RECHARGE.equals(eventPromotionRecord.getApplyPromoterSpecificCharges()), "Promotion specific promoter charges doesn't match");
        validate(EXPECTED_EXCLUSIVE_SALE.equals(eventPromotionRecord.getExclusiveSale()), "Exclusive sale doesn't match.");
        validate(EXPECTED_SELF_MANAGED_SALE.equals(eventPromotionRecord.getSelfManaged()), "Self managed doesn't match.");
        validate(EXPECTED_COLLECTIVE_ID.equals(eventPromotionRecord.getCollectiveId()), "Promotion collective ID doesn't match.");
        validate(EXPECTED_COLLECTIVE_TYPE.equals(eventPromotionRecord.getCollectiveTypeId()), "Promotion collective type doesn't match.");
        validate(EXPECTED_COLLECTIVE_SUBTYPE.equals(eventPromotionRecord.getCollectiveSubtypeId()), "Promotion collective subtype doesn't match.");
        validate(EXPECTED_CHANNELS.equals(eventPromotionRecord.getChannels()), "Channels not found.");
        validate(EXPECTED_PRICEZONES.equals(eventPromotionRecord.getPriceZones()), "Price zones not found.");
        validate(EXPECTED_RATES.equals(eventPromotionRecord.getRates()), "Rates not found.");
        validate(EXPECTED_RANGES.equals(eventPromotionRecord.getRanges()), "Ranges not found.");
        validate(EXPECTED_BLOCK_SECONDARY_MARKET.equals(eventPromotionRecord.getBlockSecondaryMarketSale()), "BlockSecondaryMarketSales not found.");

        promos = eventPromotionTemplateDao.getPromotionsByEventId(INVALID_EVENT_ID);
        assertNotNull(promos);
        assertEquals(0, promos.size(), "Expected zero promos to be returned.");
    }

    @Test
    public void testSessionsPromotedByEventPromotionIdTest() {
        assertTrue(CollectionUtils.isEmpty(this.eventPromotionTemplateDao.getPromotedSessionsByPromotionEventIds(Collections.emptyList())));
        assertTrue(CollectionUtils.isEmpty(this.eventPromotionTemplateDao.getPromotedSessionsByPromotionEventIds(Collections.singletonList(45555))));
    }

    @Test
    public void testCommunicationElementsByPromotionTemplateTest() {
        assertTrue(CollectionUtils.isEmpty(this.promotionTemplateDao.getCommunicationElementsByPromotionTemplateIds(Collections.emptyList())));
        assertTrue(CollectionUtils.isEmpty(this.promotionTemplateDao.getCommunicationElementsByPromotionTemplateIds(Collections.singletonList(45555))));
    }

    @Test
    public void testCommunicationElementsWithoutDescription() {
        List<String> languages = Arrays.asList("es_ES", "ca_ES", "en_US");
        Integer promoTemplateId = 73;
        Map<Integer, List<PromotionCommElemRecord>> result = this.promotionTemplateDao.getCommunicationElementsByPromotionTemplateIds(Collections.singletonList(promoTemplateId));
        assertNotNull(result);
        assertEquals(1, result.size());
        List<PromotionCommElemRecord> commElements = result.get(promoTemplateId);
        assertNotNull(commElements);
        assertEquals(3, commElements.size());
        assertTrue(commElements.stream().allMatch(it -> languages.contains(it.getLanguageCode())));
        commElements.forEach(commElement -> {
            switch (commElement.getLanguageCode()) {
                case "es_ES":
                    assertEquals("Promo sin descripcion", commElement.getName());
                    break;
                case "ca_ES":
                    assertEquals("Promo sense descripcio", commElement.getName());
                    break;
                case "en_US":
                    assertEquals("Promo without description", commElement.getName());
                    break;
            }
            assertNull(commElement.getDescription());
        });
    }

    private void validateCommunicationElements(Map<Integer, List<PromotionCommElemRecord>> commElements) {
        assertNotNull(commElements);
        Assertions.assertEquals(9, commElements.size());
        List<PromotionCommElemRecord> result = commElements.get(EVENT_PROMOTION_TEMPLATE_ID);
        Assertions.assertEquals(3, result.size());
    }


    private void validate(Map<Integer, List<Long>> sessionsPromoted) {
        assertNotNull(sessionsPromoted);
        assertNotNull(sessionsPromoted.get(EVENT_PROMOTION_TEMPLATE_ID));
        List<Long> result = sessionsPromoted.get(EVENT_PROMOTION_TEMPLATE_ID);
        Assertions.assertEquals(4, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(4L));
        assertTrue(result.contains(5L));
    }

    private void validate(Boolean condition, String message) {
        assertTrue(condition, message);
    }

    private EventPromotionRecord findEventPromotionByEventPromotionTemplateId(Integer eventPromotionTemmplateId, List<EventPromotionRecord> promos) {
        return promos.stream()
                .filter(p -> eventPromotionTemmplateId.equals(p.getEventPromotionTemplateId()))
                .findFirst()
                .orElse(null);
    }

    private static Date initializeDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
