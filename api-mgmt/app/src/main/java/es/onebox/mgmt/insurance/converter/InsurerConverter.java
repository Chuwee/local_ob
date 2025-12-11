package es.onebox.mgmt.insurance.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurerBasic;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurers;
import es.onebox.mgmt.datasources.ms.insurance.dto.SearchInsurerFilter;
import es.onebox.mgmt.insurance.dto.InsurerBasicDTO;
import es.onebox.mgmt.insurance.dto.InsurerCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTORequest;
import es.onebox.mgmt.insurance.dto.InsurersDTO;
import es.onebox.mgmt.insurance.dto.SearchInsurerFilterDTO;

public class InsurerConverter {
    private InsurerConverter() {
    }

    public static InsurerDTO toDto(Insurer insurer) {
        if (insurer == null) {
            return null;
        }
        InsurerDTO target = new InsurerDTO();

        target.setId(insurer.getId());
        target.setOperator(new IdNameDTO(insurer.getOperatorId().longValue()));
        target.setName(insurer.getName());
        target.setTaxId(insurer.getTaxId());
        target.setTaxName(insurer.getTaxName());
        target.setContactEmail(insurer.getContactEmail());
        target.setAddress(insurer.getAddress());
        target.setDescription(insurer.getDescription());
        target.setZipCode(insurer.getZipCode());
        target.setPhone(insurer.getPhone());

        return target;
    }

    public static InsurerBasicDTO toBasicDto(InsurerBasic insurer) {
        if (insurer == null) {
            return null;
        }
        InsurerBasicDTO target = new InsurerBasicDTO();

        target.setId(insurer.getId());
        target.setOperator(new IdNameDTO(insurer.getOperatorId().longValue()));
        target.setName(insurer.getName());
        target.setTaxName(insurer.getTaxName());
        target.setContactEmail(insurer.getContactEmail());
        target.setPhone(insurer.getPhone());

        return target;
    }

    public static InsurersDTO toDtoList(Insurers insurers) {
        InsurersDTO response = new InsurersDTO();
        if (insurers.getData() != null) {
            response.setData(insurers.getData().stream()
                    .map(InsurerConverter::toBasicDto)
                    .toList());
        }
        response.setMetadata(insurers.getMetadata());
        return response;
    }

    public static Insurer toEntity(InsurerCreateDTO insurer) {
        if (insurer == null) {
            return null;
        }
        Insurer target = new Insurer();

        target.setOperatorId(insurer.getOperatorId());
        target.setName(insurer.getName());
        target.setTaxId(insurer.getTaxId());
        target.setTaxName(insurer.getTaxName());
        target.setContactEmail(insurer.getContactEmail());
        target.setAddress(insurer.getAddress());
        target.setZipCode(insurer.getZipCode());
        target.setPhone(insurer.getPhone());

        return target;
    }

    public static void toUpdatedInsurer(Insurer insurerRecord, InsurerDTORequest request) {

        if (request.getName() != null) {
            insurerRecord.setName(request.getName());
        }

        if (request.getTaxId() != null) {
            insurerRecord.setTaxId(request.getTaxId());
        }

        if (request.getTaxName() != null) {
            insurerRecord.setTaxName(request.getTaxName());
        }

        if (request.getContactEmail() != null) {
            insurerRecord.setContactEmail(request.getContactEmail());
        }

        if (request.getAddress() != null) {
            insurerRecord.setAddress(request.getAddress());
        }

        if (request.getDescription() != null) {
            insurerRecord.setDescription(request.getDescription());
        }

        if (request.getZipCode() != null) {
            insurerRecord.setZipCode(request.getZipCode());
        }

        if (request.getPhone() != null) {
            insurerRecord.setPhone(request.getPhone());
        }
    }

    public static SearchInsurerFilter convertFilter(SearchInsurerFilterDTO searchInsurerFilterDTO) {
        SearchInsurerFilter searchInsurerFilter = new SearchInsurerFilter();
        searchInsurerFilter.setOperatorId(searchInsurerFilterDTO.getOperatorId());
        searchInsurerFilter.setQ(searchInsurerFilterDTO.getQ());
        searchInsurerFilter.setSort(searchInsurerFilterDTO.getSort());
        searchInsurerFilter.setOffset(searchInsurerFilterDTO.getOffset());
        searchInsurerFilter.setLimit(searchInsurerFilterDTO.getLimit());

        return searchInsurerFilter;
    }
}
