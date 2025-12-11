package es.onebox.event.packs;


import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.elasticsearch.dao.test.BaseElasticsearchTest;
import es.onebox.event.catalog.elasticsearch.dto.ElasticSearchResults;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackFilter;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItem;
import es.onebox.event.catalog.elasticsearch.dao.PackElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.pack.PackData;
import es.onebox.event.packs.enums.PackItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PackElasticDaoTest extends BaseElasticsearchTest<PackElasticDao, PackData> {

    private static final long CHANNEL_ID = 10L;

    @InjectMocks
    private PackElasticDao packElasticDao;

    @Override
    protected String getIndexFile() {
        return "/dao/packdata_index.json";
    }

    @Override
    protected String getDataFile() {
        return "/dao/packdata.json";
    }

    @Override
    protected PackElasticDao elasticsearchDao() {
        return packElasticDao;
    }

    @Test
    void searchChannelPacksByChannel() {
        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, new ChannelPackFilter());

        Assertions.assertEquals(5L, results.getMetadata().getTotal());
    }

    @Test
    void searchChannelPacksByCustomCategoryCode() {
        Map<String, Long> codesAndTotalResults = Map.of(
                "TEST1", 1L,
                "TEST2", 2L,
                "TEST3", 0L,
                "TEST4", 0L);

        codesAndTotalResults.forEach((code, totalResults) -> {
            ChannelPackFilter filter = new ChannelPackFilter();
            filter.setCustomCategoryCode(code);

            ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);

            Assertions.assertEquals(totalResults, results.getMetadata().getTotal());
            results.getResults().forEach(packData ->
                    Assertions.assertEquals(code, packData.getChannelPack().getCustomCategoryCode()));
        });
    }

    @Test
    void searchChannelPacksByStartDate() {
        testResultsByStartDate("2025-01-01T00:00:00Z", "2025-01-03T00:00:00Z", List.of(1L, 2L));
        testResultsByStartDate("2025-01-01T00:00:00Z", "2025-01-05T00:00:00Z", List.of(1L, 2L, 3L));
        testResultsByStartDate("2025-01-03T00:00:00Z", "2025-01-05T00:00:00Z", List.of(3L));
        testResultsByStartDate("2025-01-04T00:00:00Z", "2025-01-05T00:00:00Z", List.of());
    }

    private void testResultsByStartDate(String startDateFrom, String startDateTo, List<Long> resultChannelPackIds) {
        ChannelPackFilter filter = new ChannelPackFilter();
        FilterWithOperator<ZonedDateTime> zdtGteFilter = new FilterWithOperator<>();
        zdtGteFilter.setOperator(Operator.GREATER_THAN);
        zdtGteFilter.setValue(ZonedDateTime.parse(startDateFrom, DateTimeFormatter.ISO_ZONED_DATE_TIME));
        FilterWithOperator<ZonedDateTime> zdtLteFilter = new FilterWithOperator<>();
        zdtLteFilter.setOperator(Operator.LESS_THAN);
        zdtLteFilter.setValue(ZonedDateTime.parse(startDateTo, DateTimeFormatter.ISO_ZONED_DATE_TIME));
        filter.setStartDate(List.of(zdtGteFilter, zdtLteFilter));

        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);

        Assertions.assertEquals(resultChannelPackIds.size(), results.getMetadata().getTotal());
        results.getResults().forEach(packData ->
                Assertions.assertTrue(resultChannelPackIds.contains(packData.getChannelPack().getId())));
    }

    @Test
    void searchChannelPacksByName() {
        Map<String, Long> namesAndTotalResults = Map.of(
                "Pack", 3L,
                "1", 1L,
                "Test", 0L);

        namesAndTotalResults.forEach((name, totalResults) -> {
            ChannelPackFilter filter = new ChannelPackFilter();
            filter.setQ(name);

            ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);

            Assertions.assertEquals(totalResults, results.getMetadata().getTotal());
            results.getResults().forEach(packData ->
                    Assertions.assertTrue(packData.getChannelPack().getName().contains(name)));
        });
    }

    @Test
    void searchPacksByEventIds() {
        Long eventId1 = 123L;
        Long eventId2 = 222L;
        ChannelPackFilter filter = new ChannelPackFilter();
        filter.setEventId(List.of(123L, 222L));
        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);
        Assertions.assertEquals(5, (long) results.getMetadata().getTotal());
        results.getResults().forEach(p -> {
            Assertions.assertTrue(p.getChannelPack().getItems().stream()
                    .anyMatch(i -> i.getItemId().equals(eventId1) ||
                            i.getItemId().equals(eventId2) &&
                                    PackItemType.EVENT.equals(i.getType())));
        });
        Assertions.assertTrue(results.getResults().stream().map(PackData::getId).toList()
                .containsAll(List.of("channelPack|10|3", "channelPack|10|4", "channelPack|10|2", "channelPack|10|1")));
    }

    @Test
    void searchPacksByEventIdAndMain() {
        Long eventId = 123L;
        ChannelPackFilter filter = new ChannelPackFilter();
        filter.setEventId(List.of(eventId));
        filter.setMain(true);
        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);
        Assertions.assertEquals(1, (long) results.getMetadata().getTotal());
        results.getResults().forEach(p -> {
            Assertions.assertTrue(p.getChannelPack().getItems().stream()
                    .anyMatch(i -> i.getItemId().equals(eventId) &&
                            Boolean.TRUE.equals(i.getMain() &&
                                    PackItemType.EVENT.equals(i.getType()))));
        });
        Assertions.assertTrue(results.getResults().stream().map(PackData::getId).toList().contains("channelPack|10|3"));
    }

    @Test
    void searchPacksBySessionIdsAndMain() {
        ChannelPackFilter filter = new ChannelPackFilter();
        Long sessionId = 111L;
        filter.setSessionId(List.of(sessionId));
        filter.setMain(true);
        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);
        Assertions.assertEquals(3, (long) results.getMetadata().getTotal());
        results.getResults().forEach(p -> {
            Assertions.assertTrue(p.getChannelPack().getItems().stream()
                    .anyMatch(i -> i.getItemId().equals(sessionId) &&
                            Boolean.TRUE.equals(i.getMain() &&
                                    PackItemType.SESSION.equals(i.getType()))));
        });
        Assertions.assertTrue(results.getResults().stream().map(PackData::getId).toList()
                .containsAll(List.of("channelPack|10|2", "channelPack|10|4")));
    }

    @Test
    void searchPacksByEventAndSessionIds() {
        Long eventId = 123L;
        Long sessionId = 111L;

        ChannelPackFilter filter = new ChannelPackFilter();
        filter.setSessionId(List.of(sessionId));
        filter.setEventId(List.of(eventId));
        filter.setMain(true);

        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);

        Assertions.assertEquals(4, (long) results.getMetadata().getTotal());
        List<ChannelPackItem> sessionPack = new ArrayList<>();
        List<ChannelPackItem> eventPack = new ArrayList<>();
        results.getResults().forEach(packData -> {
                    sessionPack.addAll(packData.getChannelPack().getItems().stream()
                            .filter(i -> i.getItemId().equals(sessionId) &&
                                    Boolean.TRUE.equals(i.getMain() &&
                                            PackItemType.SESSION.equals(i.getType()))).toList());
                    eventPack.addAll(packData.getChannelPack().getItems().stream()
                            .filter(i -> Boolean.TRUE.equals(i.getMain()) &&
                                    i.getItemId().equals(eventId) &&
                                    PackItemType.EVENT.equals(i.getType())).toList());
                }
        );
        Assertions.assertEquals(3, (long) sessionPack.size());
        Assertions.assertEquals(1, (long) eventPack.size());
        Assertions.assertTrue(results.getResults().stream().map(PackData::getId).toList()
                .containsAll(List.of("channelPack|10|3", "channelPack|10|4", "channelPack|10|2")));
    }
    @Test
    void searchSuggestedPacksInChannel() {
        ChannelPackFilter filter = new ChannelPackFilter();
        filter.setSuggested(true);

        ElasticSearchResults<PackData> results = packElasticDao.searchChannelPacks(CHANNEL_ID, filter);
        Assertions.assertEquals(1, (long) results.getMetadata().getTotal());
        results.getResults().forEach(packData -> {
                   Assertions.assertEquals(Boolean.TRUE, packData.getChannelPack().getSuggested());
            Assertions.assertEquals(105, packData.getChannelPack().getId());
                }
        );
        Assertions.assertTrue(results.getResults().stream().map(PackData::getId).toList().contains("channelPack|10|5"));
    }
}