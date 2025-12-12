package es.onebox.atm.barcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.accesscontrol.dto.Barcode;
import es.onebox.common.datasources.ms.accesscontrol.dto.ImportExternalBarcode;
import es.onebox.common.datasources.ms.accesscontrol.repository.ExternalBarcodeRepository;
import es.onebox.common.datasources.ms.ticket.dto.ExternalMode;
import es.onebox.common.datasources.ms.ticket.dto.OrderItemPrint;
import es.onebox.common.datasources.ms.ticket.dto.TicketItemPrintDTO;
import es.onebox.common.datasources.ms.ticket.repository.MsTicketRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ATMExternalBarcodeService {

    private static final String PASSBOOK_DATA_FILE_NAME = "pass.json";
    private static final String BARCODE_LEADING_ZEROS = "0000000000000000";

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMExternalBarcodeService.class);

    private final MsTicketRepository msTicketDatasource;
    private final ExternalBarcodeRepository externalBarcodeRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ATMExternalBarcodeService(MsTicketRepository msTicketDatasource, ExternalBarcodeRepository externalBarcodeRepository) {
        this.msTicketDatasource = msTicketDatasource;
        this.externalBarcodeRepository = externalBarcodeRepository;
    }

    public void addExternalBarcode(String orderCode, Long itemId, Long eventId, Long sessionId, String row, String seat,
                                   String sectorName, Map<String, String> attendantData) {


        OrderItemPrint oip = msTicketDatasource.getItemPassbook(orderCode, itemId, ExternalMode.PASSBOOK_FILE);
        String itemOrderData = String.format("item: %s, event: %s, session: %s", itemId, eventId,
                sessionId);

        if (oip == null || CollectionUtils.isEmpty(oip.getTickets())) {
            String msg = String.format("passbook not ready for %s", itemOrderData);
            LOGGER.error("[ATM EXTERNAL BARCODE] [{}] {}", orderCode, msg);
            throw new RuntimeException(msg);
        }
        TicketItemPrintDTO tip = oip.getTickets().get(0);
        if (tip == null || StringUtils.isEmpty(tip.getDownloadLink())) {
            String msg = String.format("passbook download link not available for %s", itemOrderData);
            LOGGER.error("[ATM EXTERNAL BARCODE] [{}] {}", orderCode, msg);
            throw new RuntimeException(msg);
        }
        byte[] passJsonContent = getPassJsonDataFromPassbook(tip.getDownloadLink());
        if (passJsonContent == null) {
            String msg = String.format("could not obtain %s for %s", PASSBOOK_DATA_FILE_NAME, itemOrderData);
            LOGGER.error("[ATM EXTERNAL BARCODE] [{}] {}", orderCode, msg);
            throw new RuntimeException(msg);
        }
        String avetBarcode = getAvetBarcodeFromJsonDataFile(passJsonContent);
        if (StringUtils.isEmpty(avetBarcode)) {
            String msg = String.format("could not obtain avet barcode for %s", itemOrderData);
            LOGGER.error("[ATM EXTERNAL BARCODE] [{}] {}", orderCode, msg);
            throw new RuntimeException(msg);
        }
        avetBarcode = avetBarcode.replaceAll("\r", "").replaceAll(" ", "");

        //Two calls to import barcodes are needed, for ticket pdf with zeros and for wallet without
        importBarcode(avetBarcode, eventId, sessionId, orderCode, row, seat, sectorName, attendantData);

        if (avetBarcode.startsWith(BARCODE_LEADING_ZEROS)) {
            String walletBarcode = avetBarcode.replaceFirst("^0{16}", "");
            importBarcode(walletBarcode, eventId, sessionId, orderCode, row, seat, sectorName, attendantData);
        }
    }

    private byte[] getPassJsonDataFromPassbook(String downloadLink) {
        byte[] passJsonContent = null;
        InputStream in = null;
        GZIPInputStream gzis = null;
        ZipInputStream zis = null;

        try {
            in = new URL(downloadLink).openStream();
            gzis = new GZIPInputStream(in);
            zis = new ZipInputStream(gzis);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (PASSBOOK_DATA_FILE_NAME.equals(entry.getName())) {
                    passJsonContent = zis.readAllBytes();
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("[ATM EXTERNAL BARCODE] error while downloading/reading passbook file from: {}", downloadLink, e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(gzis);
        }
        return passJsonContent;
    }

    private String getAvetBarcodeFromJsonDataFile(byte[] passJsonContent) {
        String avetBarcode = null;
        try {
            Map<String, Object> passbookData = mapper.readValue(passJsonContent, HashMap.class);
            if (passbookData.containsKey("barcode")) {
                Map<String, String> barcode = (Map<String, String>) passbookData.get("barcode");
                avetBarcode = barcode.get("message");
            }
        } catch (Exception e) {
            LOGGER.error("[ATM EXTERNAL BARCODE] error while recovering avet barcode", e);
        }
        return avetBarcode;
    }

    private void importBarcode(String avetBarcode, Long eventId, Long sessionId, String orderCode, String row,
                               String seat, String sectorName, Map<java.lang.String, java.lang.String> attendantData) {
        ImportExternalBarcode externalBarcode = new ImportExternalBarcode();
        externalBarcode.setEventId(eventId);
        externalBarcode.setSessionId(sessionId);

        Barcode barcode = new Barcode();
        barcode.setBarcode(avetBarcode);
        barcode.setLocator(orderCode);
        barcode.setRow(row);
        barcode.setSeat(seat);
        barcode.setSectorName(sectorName);
        barcode.setAttendantData(attendantData);

        externalBarcode.setBarcodes(List.of(barcode));
        externalBarcodeRepository.importExternalBarcodes(externalBarcode);
    }
}
