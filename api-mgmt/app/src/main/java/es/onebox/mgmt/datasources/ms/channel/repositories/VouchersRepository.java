package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.SendEmailVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVoucherBalance;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVouchersBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Voucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupGiftCard;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroups;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Vouchers;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.vouchers.dto.VoucherExportFileField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VouchersRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public VouchersRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public VoucherGroups searchVoucherGroups(Long operatorId, VoucherGroupFilter filter, SortOperator<String> sort) {
        return msChannelDatasource.getVoucherGroups(operatorId, filter, sort);
    }

    public VoucherGroup getVoucherGroup(Long voucherGroupId) {
        return msChannelDatasource.getVoucherGroup(voucherGroupId);
    }

    public IdDTO createVoucherGroup(CreateVoucherGroup request) {
        return msChannelDatasource.createVoucherGroup(request);
    }

    public void updateVoucherGroup(VoucherGroup request) {
        msChannelDatasource.updateVoucherGroup(request);
    }

    public void deleteVoucherGroup(Long voucherGroupId) {
        msChannelDatasource.deleteVoucherGroup(voucherGroupId);
    }

    public Vouchers searchVouchers(Long voucherGroupId, VoucherFilter filter) {
        return msChannelDatasource.getVouchers(voucherGroupId, filter);
    }

    public Voucher getVoucher(Long voucherGroupId, String code) {
        return msChannelDatasource.getVoucher(voucherGroupId, code);
    }

    public String createVoucher(Long voucherGroupId, CreateVoucher request) {
        return msChannelDatasource.createVoucher(voucherGroupId, request);
    }

    public List<String> createVouchers(Long voucherGroupId, List<CreateVoucher> request) {
        return msChannelDatasource.createVouchers(voucherGroupId, request);
    }

    public void updateVoucher(Voucher request) {
        msChannelDatasource.updateVoucher(request);
    }

    public void updateVoucherBalance(Long voucherGroupId, String code, UpdateVoucherBalance request) {
        msChannelDatasource.updateVoucherBalance(voucherGroupId, code, request);
    }

    public void updateVouchers(Long voucherGroupId, UpdateVouchersBulk request) {
        msChannelDatasource.updateVouchers(voucherGroupId, request);
    }

    public void deleteVoucher(Long voucherGroupId, String code) {
        msChannelDatasource.deleteVoucher(voucherGroupId, code);
    }

    public List<BaseCommunicationElement> getVoucherCommunicationElements(Long voucherGroupId, CommunicationElementFilter comElementsFilter) {
        return msChannelDatasource.getVoucherGroupComElements(voucherGroupId, comElementsFilter);
    }

    public void updateVoucherGroupCommunicationElements(Long voucherGroupId, List<BaseCommunicationElement> elements) {
        msChannelDatasource.updateVoucherGroupComElements(voucherGroupId, elements);
    }

    public VoucherGroupGiftCard getVoucherGroupGiftCard(Long voucherGroupId) {
        return msChannelDatasource.getVoucherGroupGiftCard(voucherGroupId);
    }

    public void updateVoucherGroupGiftCard(Long voucherGroupId, VoucherGroupGiftCard request) {
        msChannelDatasource.updateVoucherGroupGiftCard(voucherGroupId, request);
    }

    public ExportProcess generateVouchersReport(Long voucherGroupId, ExportFilter<VoucherExportFileField> filter) {
        return msChannelDatasource.generateVouchersReport(voucherGroupId, filter);
    }

    public ExportProcess getVouchersReportStatus(Long voucherGroupId, String exportId, Long id) {
        return msChannelDatasource.getVouchersReportStatus(voucherGroupId, exportId, id);
    }

    public void sendEmailVoucher(Long voucherGroupId, String code, SendEmailVoucher body) {
        msChannelDatasource.sendEmailVoucher(voucherGroupId, code, body);
    }
}