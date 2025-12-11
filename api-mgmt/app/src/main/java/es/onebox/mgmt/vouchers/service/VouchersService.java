package es.onebox.mgmt.vouchers.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVoucherBalance;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Voucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Vouchers;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.vouchers.converter.VouchersConverter;
import es.onebox.mgmt.vouchers.dto.CreateVoucherBulkRequestDTO;
import es.onebox.mgmt.vouchers.dto.CreateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.SendEmailVoucherDTO;
import es.onebox.mgmt.vouchers.dto.SendEmailVoucherType;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherBalanceDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVouchersBulkDTO;
import es.onebox.mgmt.vouchers.dto.VoucherDTO;
import es.onebox.mgmt.vouchers.dto.VoucherSearchFilter;
import es.onebox.mgmt.vouchers.dto.VouchersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VouchersService {

    private SecurityManager securityManager;
    private VouchersRepository vouchersRepository;
    private EntitiesRepository entitiesRepository;
    private VoucherGroupsService voucherGroupsService;

    @Autowired
    public VouchersService(SecurityManager securityManager,
                           VouchersRepository vouchersRepository,
                           EntitiesRepository entitiesRepository,
                           VoucherGroupsService voucherGroupsService) {
        this.securityManager = securityManager;
        this.vouchersRepository = vouchersRepository;
        this.entitiesRepository = entitiesRepository;
        this.voucherGroupsService = voucherGroupsService;
    }

    public VouchersDTO searchVouchers(Long voucherGroupId, VoucherSearchFilter request) {
        securityManager.checkEntityAccessible(request);

        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        Vouchers vouchers = vouchersRepository.searchVouchers(voucherGroupId, VouchersConverter.convertFilter(request));
        Operator operator = entitiesRepository.getCachedOperator(voucherGroup.getEntityId());

        return VouchersConverter.fromMsChannel(vouchers, operator.getTimezone().getValue());
    }

    public VoucherDTO getVoucher(Long voucherGroupId, String code) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        Voucher voucher = vouchersRepository.getVoucher(voucherGroupId, code);
        Operator operator = entitiesRepository.getCachedOperator(voucherGroup.getEntityId());

        return VouchersConverter.fromMsChannel(voucher, operator.getTimezone().getValue());
    }

    public CodeDTO createVoucher(Long voucherGroupId, CreateVoucherRequestDTO request) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        if (!es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType.MANUAL.equals(voucherGroup.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_CREATE_INVALID_TYPE);
        }

        return new CodeDTO(vouchersRepository.createVoucher(voucherGroupId, VouchersConverter.convertCreate(request)));
    }

    public List<String> createBulkVoucher(Long voucherGroupId, CreateVoucherBulkRequestDTO request) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        if (!es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType.MANUAL.equals(voucherGroup.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_CREATE_INVALID_TYPE);
        }
        return vouchersRepository.createVouchers(voucherGroupId, VouchersConverter.convertCreate(request));
    }

    public void updateVoucher(Long voucherGroupId, String code, UpdateVoucherRequestDTO request) {
        validateUpdate(request);
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        Voucher voucher = VouchersConverter.toMsChannel(request);
        voucher.setVoucherGroupId(voucherGroupId);
        voucher.setCode(code);

        vouchersRepository.updateVoucher(voucher);
    }

    public void updateVoucherBalance(Long voucherGroupId, String code, UpdateVoucherBalanceDTO request) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        UpdateVoucherBalance voucher = new UpdateVoucherBalance();
        voucher.setBalance(request.getBalance());
        voucher.setUserId(SecurityUtils.getUserId());

        vouchersRepository.updateVoucherBalance(voucherGroupId, code, voucher);
    }

    public void updateVouchers(Long voucherGroupId, UpdateVouchersBulkDTO request) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        vouchersRepository.updateVouchers(voucherGroupId, VouchersConverter.toMsChannel(request));
    }

    public void deleteVoucher(Long voucherGroupId, String code) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        vouchersRepository.deleteVoucher(voucherGroupId, code);
    }

    public void sendEmail(Long voucherGroupId, String code, SendEmailVoucherDTO body) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);
        validateSendEmail(body, voucherGroup);
        vouchersRepository.sendEmailVoucher(voucherGroupId, code, VouchersConverter.toMs(body));
    }

    private void validateSendEmail(SendEmailVoucherDTO body, VoucherGroup voucherGroup) {
        switch (voucherGroup.getType()) {
            case MANUAL -> {
                if (SendEmailVoucherType.REFUND.equals(body.type())) {
                    throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).build();
                }
            }
            case GIFT_CARD, EXTERNAL -> throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).build();
        }
        if (SendEmailVoucherType.BASIC.equals(body.type()) &&
                (body.body() == null || body.language() == null || body.subject() == null)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.REQUIRED_PARAMS).build();
        } else if (SendEmailVoucherType.REFUND.equals(body.type()) &&
                (body.body() != null || body.language() != null || body.subject() != null)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).build();
        }
    }

    private void validateUpdate(UpdateVoucherRequestDTO requestDTO) {
        //Check changing expiration but filling date
        if (requestDTO.getExpiration() != null &&
                requestDTO.getExpiration().getDate() != null &&
                Boolean.FALSE.equals(requestDTO.getExpiration().getEnable())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_EXPIRATION_INCONSISTENT_VALUES);
        }
    }

}
