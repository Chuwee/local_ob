package es.onebox.event.events.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import es.onebox.event.catalog.dao.couch.CatalogPriceSimulation;
import es.onebox.event.catalog.dao.couch.CatalogPriceType;
import es.onebox.event.catalog.dao.couch.CatalogRate;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.events.domain.ExternalRateType;
import es.onebox.event.events.domain.ExternalRateTypeDetail;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalRateTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;

public class EventRateConverter {

	private static final String MUSEUM = "MC";
	private static final String THEATRE = "TC";
	private static final Character RIDOTTO = 'R';
    private static final Character INTERO = 'I';
	private static final String I1 = INTERO + "1";

	private EventRateConverter() {
		throw new UnsupportedOperationException("Cannot instantiate utilities class");
	}

	public static ExternalRateType fromChannelSessionPrices(
			ChannelSessionPricesDocument channelSessionPricesDocument,
			List<CpanelTarifaRecord> sessionRates,
			List<CpanelExternalRateTypeRecord> externalRateTypeRecords, List<SectorDTO> seatSections) {
		ExternalRateType externalRateType = new ExternalRateType();
		externalRateType.setSessionId(channelSessionPricesDocument.getSessionId());
		externalRateType.setRateTypes(new ArrayList<>());
		externalRateType.setSeatSections(seatSections.stream().map(SectorDTO::getCode).toList());

		Map<String, Map<String, Double>> tittoloPriceCode = new HashMap<>();
		if (channelSessionPricesDocument.getSimulation() != null
				&& CollectionUtils.isNotEmpty(channelSessionPricesDocument.getSimulation().getRates())) {
			for (CatalogRate catalogRate : channelSessionPricesDocument.getSimulation().getRates()) {
				Optional<CpanelTarifaRecord> optionalRate = sessionRates.stream()
						.filter(se -> se.getIdtarifa().equals(catalogRate.getId().intValue())
								&& se.getExternalratetypeid() != null)
						.findFirst();
				optionalRate.ifPresent(rate -> {
					for (CatalogPriceType catalogPriceType : catalogRate.getPriceTypes()) {
						calculate(externalRateTypeRecords, catalogRate,
								catalogPriceType, rate, tittoloPriceCode,
								externalRateType);
					}
				});
			}
		}
		return externalRateType;
	}

	private static void calculate(
			List<CpanelExternalRateTypeRecord> externalRateTypeRecords, CatalogRate catalogRate,
			CatalogPriceType catalogPriceType, CpanelTarifaRecord rate,
			Map<String, Map<String, Double>> tittoloPriceCode, ExternalRateType externalRateType) {
		if (!CollectionUtils.isEmpty(catalogPriceType.getSimulations())) {
			for (CatalogPriceSimulation catalogPriceSimulation : catalogPriceType.getSimulations()) {
				PriceData priceData = toPriceData(catalogPriceSimulation, catalogPriceType);
				processRate(externalRateTypeRecords, catalogRate, rate, tittoloPriceCode,
						externalRateType, priceData);
			}
		} else {
			PriceData priceData = toPriceData(catalogPriceType);
			processRate(externalRateTypeRecords, catalogRate, rate, tittoloPriceCode,
					externalRateType, priceData);
		}
	}

	private static PriceData toPriceData(CatalogPriceType catalogPriceType) {
		return new PriceData(
				catalogPriceType.getPrice().getTotal(),
				false, catalogPriceType.getId(), null);
	}

	private static PriceData toPriceData(CatalogPriceSimulation catalogPriceSimulation,
			CatalogPriceType catalogPriceType) {
		Double totalWithDiscountApplied = catalogPriceSimulation.getPrice().getTotal();
		Double totalWithoutDiscount = catalogPriceType.getPrice().getTotal();
		return new PriceData(
				totalWithDiscountApplied,
				CollectionUtils.isNotEmpty(catalogPriceSimulation.getPromotions()), catalogPriceType.getId(),
				totalWithoutDiscount - totalWithDiscountApplied);
	}

	private static void processRate(
			List<CpanelExternalRateTypeRecord> externalRateTypeRecords, CatalogRate catalogRate,
			CpanelTarifaRecord rate, Map<String, Map<String, Double>> tittoloPriceCode,
			ExternalRateType externalRateType, PriceData priceData) {
		ExternalRateTypeDetail externalRateTypeDetail = new ExternalRateTypeDetail();
		externalRateTypeDetail.setPriceZoneId(priceData.priceZoneId());
		externalRateTypeDetail.setRateId(catalogRate.getId());
		externalRateTypeDetail.setBasePrice(priceData.total());

		if (priceData.hasPromotions()) {
			Map<String, Double> ridottoMap = tittoloPriceCode.get(RIDOTTO.toString());
			Map<String, Double> priceCodeMap = ridottoMap != null ? ridottoMap : new HashMap<>();
			priceWithDiscount(priceData, priceCodeMap, externalRateTypeDetail, externalRateType);
			tittoloPriceCode.put(RIDOTTO.toString(), priceCodeMap);
		} else {
			Optional<CpanelExternalRateTypeRecord> optionalExternalRateType = externalRateTypeRecords
					.stream()
					.filter(rt -> rt.getId().equals(rate.getExternalratetypeid()))
					.findFirst();

			optionalExternalRateType
					.ifPresent(cpanelExternalRateTypeRecord -> addExternalRateType(catalogRate, priceData,
							cpanelExternalRateTypeRecord,
							tittoloPriceCode, externalRateTypeDetail, externalRateType));
		}

	}

	private static void addExternalRateType(
			CatalogRate catalogRate, PriceData priceData,
			CpanelExternalRateTypeRecord cpanelExternalRateTypeRecord,
			Map<String, Map<String, Double>> tittoloPriceCode,
			ExternalRateTypeDetail externalRateTypeDetail,
			ExternalRateType externalRateType) {
		if (cpanelExternalRateTypeRecord.getCode() != null
				&& (cpanelExternalRateTypeRecord.getCode().equals(MUSEUM)
						|| cpanelExternalRateTypeRecord.getCode().equals(THEATRE))) {
			if (!tittoloPriceCode.containsKey(cpanelExternalRateTypeRecord.getCode())) {
				tittoloPriceCode.put(cpanelExternalRateTypeRecord.getCode(), new HashMap<>());
			}
			externalRateTypeDetail.setExternalCode(cpanelExternalRateTypeRecord.getCode());
			externalRateType.getRateTypes().add(externalRateTypeDetail);
		} else {
			if (tittoloPriceCode.containsKey(cpanelExternalRateTypeRecord.getCode())) {
				Map<String, Double> priceCodeMap = tittoloPriceCode
						.get(cpanelExternalRateTypeRecord.getCode());
				if (findMax(priceCodeMap, cpanelExternalRateTypeRecord.getCode().charAt(0)) > 0) {
					priceVat(catalogRate, priceData.total(), priceCodeMap, externalRateTypeDetail, externalRateType);
				}
			} else {
				fillRate(catalogRate, priceData, cpanelExternalRateTypeRecord, tittoloPriceCode,
						externalRateTypeDetail, externalRateType);
			}
		}
	}

	private static void fillRate(
			CatalogRate catalogRate, PriceData priceData,
			CpanelExternalRateTypeRecord cpanelExternalRateTypeRecord,
			Map<String, Map<String, Double>> tittoloPriceCode,
			ExternalRateTypeDetail externalRateTypeDetail,
			ExternalRateType externalRateType) {
		tittoloPriceCode.put(cpanelExternalRateTypeRecord.getCode(), new HashMap<>());
		tittoloPriceCode.get(cpanelExternalRateTypeRecord.getCode()).put(I1, priceData.total());
		externalRateTypeDetail.setRateId(catalogRate.getId());
		externalRateTypeDetail.setBasePrice(priceData.total());
		externalRateTypeDetail.setExternalCode(I1);
		externalRateType.getRateTypes().add(externalRateTypeDetail);
	}

	private static void priceWithDiscount(
			PriceData priceData, Map<String, Double> priceCodeMap,
			ExternalRateTypeDetail externalRateTypeDetail, ExternalRateType externalRateType) {
		Integer maxR = findMax(priceCodeMap, RIDOTTO);
		String nextRCode = RIDOTTO.toString() + (maxR + 1);
		priceCodeMap.put(nextRCode, priceData.total());
		externalRateTypeDetail.setExternalCode(nextRCode);
		externalRateTypeDetail.setDiscount(priceData.discount());
		externalRateType.getRateTypes().add(externalRateTypeDetail);
	}

	private static void priceVat(
			CatalogRate catalogRate, Double total, Map<String, Double> priceCodeMap,
			ExternalRateTypeDetail externalRateTypeDetail, ExternalRateType externalRateType) {
		externalRateTypeDetail.setRateId(catalogRate.getId());
		externalRateTypeDetail.setBasePrice(total);
		Integer maxI = findMax(priceCodeMap, INTERO);
		String nextICode = INTERO.toString() + (maxI + 1);
		priceCodeMap.put(nextICode, total);
		externalRateTypeDetail.setExternalCode(nextICode);
		externalRateType.getRateTypes().add(externalRateTypeDetail);
	}

	private static Integer findMax(Map<String, Double> priceCodeMap, char character) {
		int max = 0;
		for (String value : priceCodeMap.keySet()) {
			if (value.charAt(0) == character) {
				int num = Integer.parseInt(value.substring(1));
				if (num > max) {
					max = num;
				}
			}
		}
		return max;
	}

	private record PriceData(Double total, boolean hasPromotions, Long priceZoneId, Double discount) {
	}
}