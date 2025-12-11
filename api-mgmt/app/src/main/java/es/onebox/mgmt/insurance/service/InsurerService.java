package es.onebox.mgmt.insurance.service;

import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurerBasic;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurers;
import es.onebox.mgmt.datasources.ms.insurance.dto.ResponseEntity;
import es.onebox.mgmt.datasources.ms.insurance.dto.SearchInsurerFilter;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurerRepository;
import es.onebox.mgmt.insurance.converter.InsurerConverter;
import es.onebox.mgmt.insurance.dto.InsurerBasicDTO;
import es.onebox.mgmt.insurance.dto.InsurerCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTORequest;
import es.onebox.mgmt.insurance.dto.InsurersDTO;
import es.onebox.mgmt.insurance.dto.SearchInsurerFilterDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InsurerService {
    private final InsurerRepository insurerRepository;
    private final ValidationService validationService;
    private final EntitiesRepository entitiesRepository;

    private static final List<String> OPERATOR_NAME_FIELD = List.of("name");

    public InsurerService(InsurerRepository insurerRepository, ValidationService validationService, EntitiesRepository entitiesRepository) {
        this.insurerRepository = insurerRepository;
        this.validationService = validationService;
        this.entitiesRepository = entitiesRepository;
    }

    public InsurerDTO getInsurer(Integer insurerId) {
        InsurerDTO insurerDTO = InsurerConverter.toDto(validationService.getAndCheckInsurer(insurerId));

        String operatorName = getOperatorName(insurerDTO.getOperator().getId().intValue());
        insurerDTO.getOperator().setName(operatorName);

        return insurerDTO;
    }

    public InsurersDTO searchInsurers(SearchInsurerFilterDTO searchInsurerFilterDTO) {
        SearchInsurerFilter filter = InsurerConverter.convertFilter(searchInsurerFilterDTO);
        Insurers insurers = insurerRepository.searchInsurers(filter);
        InsurersDTO insurerBasicDTOs = InsurerConverter.toDtoList(insurers);

        if (!insurerBasicDTOs.getData().isEmpty()) {
            List<InsurerBasicDTO> insurerDTOs = insurerBasicDTOs.getData();
            setOperatorNames(insurerDTOs, extractOperatorIds(insurers.getData()));
        }
        return insurerBasicDTOs;
    }

    public InsurerDTO createInsurer(InsurerCreateDTO request) {
        Insurer insurerToSave = InsurerConverter.toEntity(request);
        InsurerDTO insurerDTO = InsurerConverter.toDto(insurerRepository.createInsurer(insurerToSave));
        String operatorName = getOperatorName(insurerDTO.getOperator().getId().intValue());
        insurerDTO.getOperator().setName(operatorName);

        return insurerDTO;
    }

    public InsurerDTO updateInsurer(Integer insurerId, InsurerDTORequest request) {
        Insurer insurer = validationService.getAndCheckInsurer(insurerId);

        InsurerConverter.toUpdatedInsurer(insurer, request);
        InsurerDTO insurerDTO = InsurerConverter.toDto(insurerRepository.updateInsurer(insurer));
        String operatorName = getOperatorName(insurerDTO.getOperator().getId().intValue());
        insurerDTO.getOperator().setName(operatorName);

        return insurerDTO;
    }


    private List<Integer> extractOperatorIds(List<InsurerBasic> insurers) {
        return insurers.stream()
                .map(InsurerBasic::getOperatorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void setOperatorNames(List<InsurerBasicDTO> insurerDTOs, List<Integer> operatorIds) {
        if (operatorIds.isEmpty()) return;

        EntitySearchFilter entityFilter = new EntitySearchFilter();
        entityFilter.setIds(operatorIds);
        entityFilter.setFields(OPERATOR_NAME_FIELD);

        List<ResponseEntity> operatorEntities = entitiesRepository.getListOfEntities(entityFilter).getData();
        Map<Integer, String> operatorIdToName = operatorEntities.stream()
                .collect(Collectors.toMap(ResponseEntity::getId, ResponseEntity::getName));

        for (InsurerBasicDTO insurer : insurerDTOs) {
            if (insurer.getOperator() != null) {
                String name = operatorIdToName.get(insurer.getOperator().getId().intValue());
                insurer.getOperator().setName(name);
            }
        }
    }

    private String getOperatorName(Integer operatorId) {
        if (operatorId == null) {
            return null;
        }

        EntitySearchFilter entityFilter = new EntitySearchFilter();
        entityFilter.setIds(List.of(operatorId));
        entityFilter.setFields(OPERATOR_NAME_FIELD);

        List<ResponseEntity> result = entitiesRepository.getListOfEntities(entityFilter).getData();
        return result.isEmpty() ? null : result.get(0).getName();
    }


}
