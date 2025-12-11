package es.onebox.event.events.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import es.onebox.event.catalog.dao.couch.CatalogPrice;
import es.onebox.event.catalog.dao.couch.CatalogPriceSimulation;
import es.onebox.event.catalog.dao.couch.CatalogPriceTaxes;
import es.onebox.event.catalog.dao.couch.CatalogPriceType;
import es.onebox.event.catalog.dao.couch.CatalogRate;
import es.onebox.event.catalog.dao.couch.CatalogVenueConfigPricesSimulation;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.events.domain.ExternalRateType;
import es.onebox.event.pricesengine.dto.PromotionDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalRateTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;

class EventRateConverterTest {

	@Test
	void fromChannelSessionPrices() {
		int amountOfSimulations = 2;
		ChannelSessionPricesDocument channelSessionPricesDocument = createDoc(amountOfSimulations);
		long totalAmountOfRates = channelSessionPricesDocument.getSimulation().getRates().stream()
				.map(CatalogRate::getPriceTypes).mapToLong(
						Collection::size)
				.sum();
		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(channelSessionPricesDocument.getSimulation().getRates().get(0).getId().intValue());
		sessionRates.add(taxDb);
		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);
		SectorDTO sector = new SectorDTO();
		sector.setId(1L);
		sector.setName("sector");
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType externalRateType = EventRateConverter.fromChannelSessionPrices(channelSessionPricesDocument,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(externalRateType);
		assertEquals(totalAmountOfRates - amountOfSimulations,
				externalRateType.getRateTypes().stream().filter(rate -> rate.getExternalCode().startsWith("I"))
						.count());
		assertEquals(amountOfSimulations,
				externalRateType.getRateTypes().stream().filter(rate -> rate.getExternalCode().startsWith("R"))
						.count());
	}

	private ChannelSessionPricesDocument createDoc(int amountOfSimulations) {

		ChannelSessionPricesDocument channelSessionPricesDocument = new ChannelSessionPricesDocument();
		channelSessionPricesDocument.setChannelId(1L);
		channelSessionPricesDocument.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		CatalogRate rate1 = createRate(3, false);
		CatalogRate rate2 = createRate(4, true, IntStream.of(0, amountOfSimulations).toArray());
		CatalogRate rate3 = createRate(2, false);
		simulation.setRates(List.of(rate1, rate2, rate3));
		channelSessionPricesDocument.setSimulation(simulation);
		return channelSessionPricesDocument;
	}

	private CatalogRate createRate(int numberOfPriceTypes, boolean withSimulations, int... positions) {
		CatalogRate catalogRate = new CatalogRate();
		catalogRate.setId(1L);
		catalogRate.setName("sector");
		List<CatalogPriceType> catalogPriceTypes = new ArrayList<>();
		for (int i = 0; i < numberOfPriceTypes; i++) {
			CatalogPriceType catalogPriceType = new CatalogPriceType();
			catalogPriceType.setName(i + "priceType");
			catalogPriceType.setId((long) i);
			CatalogPrice catalogPrice = createCatalogPrice(false);
			catalogPriceType.setPrice(catalogPrice);
			if (withSimulations && positions.length >= 1) {
				List<CatalogPriceSimulation> simulations = new ArrayList<>();
				for (int position : positions) {
					if (i == position) {
						CatalogPriceSimulation simul = new CatalogPriceSimulation();
						simul.setPrice(createCatalogPrice(true));
						simul.setPromotions(createPromotion());
						simulations.add(simul);
					}
				}
				catalogPriceType.setSimulations(simulations);
			}
			catalogPriceTypes.add(catalogPriceType);
		}
		catalogRate.setPriceTypes(catalogPriceTypes);
		return catalogRate;
	}

	private List<PromotionDTO> createPromotion() {
		List<PromotionDTO> promotions = new ArrayList<>();
		PromotionDTO promotion = new PromotionDTO();
		promotion.setId(1L);
		promotion.setName("promotion");
		promotions.add(promotion);
		return promotions;
	}

	private CatalogPrice createCatalogPrice(boolean discount) {
		CatalogPrice catalogPrice = new CatalogPrice();
		catalogPrice.setBase(1.0);
		catalogPrice.setNet(2.0);
		catalogPrice.setOriginal(3.0);
		if (discount) {
			catalogPrice.setTotal(4.0);
		} else {
			catalogPrice.setTotal(5.0);
		}
		CatalogPriceTaxes taxes = new CatalogPriceTaxes();
		taxes.setTotal(1.1);
		catalogPrice.setTaxes(taxes);
		return catalogPrice;
	}

	@Test
	void fromChannelSessionPrices_withNullSimulation_returnsEmptyRateTypes() {
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		doc.setSimulation(null);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertEquals(2L, result.getSessionId());
		assertTrue(result.getRateTypes().isEmpty());
	}

	@Test
	void fromChannelSessionPrices_withEmptyRates_returnsEmptyRateTypes() {
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		simulation.setRates(new ArrayList<>());
		doc.setSimulation(simulation);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().isEmpty());
	}

	@Test
	void fromChannelSessionPrices_withNoMatchingSessionRate_returnsEmptyRateTypes() {
		ChannelSessionPricesDocument doc = createDoc(0);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(9999);
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().isEmpty());
	}

	@Test
	void fromChannelSessionPrices_withMCExternalCode_addsMCRateType() {
		ChannelSessionPricesDocument doc = createDoc(0);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(doc.getSimulation().getRates().get(0).getId().intValue());
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("MC");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().stream()
				.anyMatch(rate -> "MC".equals(rate.getExternalCode())));
	}

	@Test
	void fromChannelSessionPrices_withTCExternalCode_addsTCRateType() {
		ChannelSessionPricesDocument doc = createDoc(0);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(doc.getSimulation().getRates().get(0).getId().intValue());
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("TC");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().stream()
				.anyMatch(rate -> "TC".equals(rate.getExternalCode())));
	}

	@Test
	void fromChannelSessionPrices_withMultipleRatesSameCode_incrementsInteroCode() {
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		CatalogRate rate1 = createRate(2, false);
		rate1.setId(1L);
		CatalogRate rate2 = createRate(2, false);
		rate2.setId(2L);
		simulation.setRates(List.of(rate1, rate2));
		doc.setSimulation(simulation);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb1 = new CpanelTarifaRecord();
		taxDb1.setExternalratetypeid(1);
		taxDb1.setIdtarifa(1);
		sessionRates.add(taxDb1);
		CpanelTarifaRecord taxDb2 = new CpanelTarifaRecord();
		taxDb2.setExternalratetypeid(1);
		taxDb2.setIdtarifa(2);
		sessionRates.add(taxDb2);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().stream()
				.anyMatch(rate -> "I1".equals(rate.getExternalCode())));
		assertTrue(result.getRateTypes().stream()
				.anyMatch(rate -> "I2".equals(rate.getExternalCode())));
	}

	@Test
	void fromChannelSessionPrices_withNullExternalRateTypeId_skipsRate() {
		ChannelSessionPricesDocument doc = createDoc(0);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(null);
		taxDb.setIdtarifa(doc.getSimulation().getRates().get(0).getId().intValue());
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().isEmpty());
	}

	@Test
	void fromChannelSessionPrices_withMultiplePromotions_incrementsRidottoCode() {
		int amountOfSimulations = 3;
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		CatalogRate rate = createRate(4, true, 0, 1, 2);
		simulation.setRates(List.of(rate));
		doc.setSimulation(simulation);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(rate.getId().intValue());
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertEquals(amountOfSimulations, result.getRateTypes().stream()
				.filter(rate1 -> rate1.getExternalCode().startsWith("R"))
				.count());
		assertTrue(result.getRateTypes().stream()
				.anyMatch(r -> "R1".equals(r.getExternalCode())));
		assertTrue(result.getRateTypes().stream()
				.anyMatch(r -> "R2".equals(r.getExternalCode())));
		assertTrue(result.getRateTypes().stream()
				.anyMatch(r -> "R3".equals(r.getExternalCode())));
	}

	@Test
	void fromChannelSessionPrices_withNoMatchingExternalRateType_skipsAddingRateType() {
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		CatalogRate rate = createRate(3, false);
		simulation.setRates(List.of(rate));
		doc.setSimulation(simulation);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb = new CpanelTarifaRecord();
		taxDb.setExternalratetypeid(1);
		taxDb.setIdtarifa(rate.getId().intValue());
		sessionRates.add(taxDb);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(999);
		externalRateDb.setCode("I");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		assertTrue(result.getRateTypes().isEmpty());
	}

	@Test
	void fromChannelSessionPrices_withMCCodeCalledTwice_addsMultipleMCRateTypes() {
		ChannelSessionPricesDocument doc = new ChannelSessionPricesDocument();
		doc.setChannelId(1L);
		doc.setSessionId(2L);
		CatalogVenueConfigPricesSimulation simulation = new CatalogVenueConfigPricesSimulation();
		CatalogRate rate1 = createRate(2, false);
		rate1.setId(1L);
		CatalogRate rate2 = createRate(2, false);
		rate2.setId(2L);
		simulation.setRates(List.of(rate1, rate2));
		doc.setSimulation(simulation);

		List<CpanelTarifaRecord> sessionRates = new ArrayList<>();
		CpanelTarifaRecord taxDb1 = new CpanelTarifaRecord();
		taxDb1.setExternalratetypeid(1);
		taxDb1.setIdtarifa(1);
		sessionRates.add(taxDb1);
		CpanelTarifaRecord taxDb2 = new CpanelTarifaRecord();
		taxDb2.setExternalratetypeid(1);
		taxDb2.setIdtarifa(2);
		sessionRates.add(taxDb2);

		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = new ArrayList<>();
		CpanelExternalRateTypeRecord externalRateDb = new CpanelExternalRateTypeRecord();
		externalRateDb.setId(1);
		externalRateDb.setCode("MC");
		externalRateTypeRecords.add(externalRateDb);

		SectorDTO sector = new SectorDTO();
		sector.setCode("SE");
		List<SectorDTO> seatSections = List.of(sector);

		ExternalRateType result = EventRateConverter.fromChannelSessionPrices(doc,
				sessionRates, externalRateTypeRecords, seatSections);

		assertNotNull(result);
		long mcCount = result.getRateTypes().stream()
				.filter(rate -> "MC".equals(rate.getExternalCode()))
				.count();
		assertEquals(4, mcCount);
	}
}