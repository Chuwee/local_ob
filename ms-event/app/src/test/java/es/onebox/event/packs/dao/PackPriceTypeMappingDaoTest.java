package es.onebox.event.packs.dao;

import es.onebox.event.packs.dao.domain.ItemPackPriceInfoRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Comparator;
import java.util.List;

public class PackPriceTypeMappingDaoTest extends DaoImplTest {

    @InjectMocks
    private PackPriceTypeMappingDao packPriceTypeMappingDao;


    @Override
    protected String getDatabaseFile() {
        return "dao/PackPriceTypeMappingDao.sql";
    }

    @Test
    public void testPackWithMainSessionAndZonedMapped() {
        List<ItemPackPriceInfoRecord> actual = packPriceTypeMappingDao.getItemPackPriceInfoRecordsByUnmappedPackItemId(3493);
        Assertions.assertNotNull(actual);

        // Pack with a main session and one related session with and specific zone
        // The dao must return the main session prices and the related session unique price equivalent to the zone defined at default rate
        List<ItemPackPriceInfoRecord> expected = List.of(
                new ItemPackPriceInfoRecord(3314, 233247, 429844, "General A", 10.0),
                new ItemPackPriceInfoRecord(3314, 233247, 429845, "Premium A", 20.0),
                new ItemPackPriceInfoRecord(3314, 233248, 429844, "General A", 5.0),
                new ItemPackPriceInfoRecord(3314, 233248, 429845, "Premium A", 15.0),
                new ItemPackPriceInfoRecord(3315, 233247, 441398, "General C", 2.0)
        );

        Assertions.assertEquals(expected, sortAsExpected(actual));
    }

    @Test
    public void testPackWithMainSessionAndMainZoned() {
        List<ItemPackPriceInfoRecord> actual = packPriceTypeMappingDao.getItemPackPriceInfoRecordsByUnmappedPackItemId(3544);
        Assertions.assertNotNull(actual);

        // Pack with main session and one related session with the same venue config
        // The dao must return only main session prices (we don't want duplicates)
        List<ItemPackPriceInfoRecord> expected = List.of(
                new ItemPackPriceInfoRecord(3390, 233247, 429844, "General A", 10.0),
                new ItemPackPriceInfoRecord(3390, 233247, 429845, "Premium A", 20.0),
                new ItemPackPriceInfoRecord(3390, 233248, 429844, "General A", 5.0),
                new ItemPackPriceInfoRecord(3390, 233248, 429845, "Premium A", 15.0)
        );

        Assertions.assertEquals(expected, sortAsExpected(actual));
    }

    @Test
    public void testPackWithMainEventAndMappings() {
        List<ItemPackPriceInfoRecord> actual = packPriceTypeMappingDao.getItemPackPriceInfoRecordsByPackItemId(3459);
        Assertions.assertNotNull(actual);

        // Pack with main event and a related session and all prices mapped
        // The dao must return the main event prices and the related session mapped prices at default rate
        List<ItemPackPriceInfoRecord> expected = List.of(
                new ItemPackPriceInfoRecord(3214, 233247, 429844, "General A", 10.0),
                new ItemPackPriceInfoRecord(3214, 233247, 429845, "Premium A", 20.0),
                new ItemPackPriceInfoRecord(3214, 233248, 429844, "General A", 5.0),
                new ItemPackPriceInfoRecord(3214, 233248, 429845, "Premium A", 15.0),
                new ItemPackPriceInfoRecord(3223, 249973, 429844, "General B", 1.0),
                new ItemPackPriceInfoRecord(3223, 249973, 429845, "Premium B", 5.0)
        );

        Assertions.assertEquals(expected, sortAsExpected(actual));
    }

    @Test
    public void testPackWithMainSessionAndMappings() {
        List<ItemPackPriceInfoRecord> actual = packPriceTypeMappingDao.getItemPackPriceInfoRecordsByPackItemId(3455);
        Assertions.assertNotNull(actual);

        // Pack with main session and a related session and all prices mapped
        // The dao must return the main session prices and the related session mapped prices at default rate
        List<ItemPackPriceInfoRecord> expected = List.of(
                new ItemPackPriceInfoRecord(3210, 233247, 429844, "General A", 10.0),
                new ItemPackPriceInfoRecord(3210, 233247, 429845, "Premium A", 20.0),
                new ItemPackPriceInfoRecord(3210, 233248, 429844, "General A", 5.0),
                new ItemPackPriceInfoRecord(3210, 233248, 429845, "Premium A", 15.0),
                new ItemPackPriceInfoRecord(3217, 249973, 429844, "General B", 1.0),
                new ItemPackPriceInfoRecord(3217, 249973, 429845, "Premium B", 5.0)
        );
        Assertions.assertEquals(expected, sortAsExpected(actual));
    }

    private static List<ItemPackPriceInfoRecord> sortAsExpected(List<ItemPackPriceInfoRecord> unsortedActualList) {
        return unsortedActualList.stream()
                .sorted(Comparator
                        .comparing(ItemPackPriceInfoRecord::getPackItemId)
                        .thenComparing(ItemPackPriceInfoRecord::getItemRateId)
                        .thenComparing(ItemPackPriceInfoRecord::getItemPrice)
                )
                .toList();
    }

}
