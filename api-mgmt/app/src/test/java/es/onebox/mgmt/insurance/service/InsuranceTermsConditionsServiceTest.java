package es.onebox.mgmt.insurance.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.exception.ApiMgmtInsuranceErrorCode;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsFileContentDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsuranceTermsConditionsServiceTest {

    @Test
    void validateFileContentAndName_WithValidData_ShouldNotThrowException() {
        UpdateInsuranceTermsConditionsFileContentDTO validDto = new UpdateInsuranceTermsConditionsFileContentDTO();
        validDto.setFileContent("Contenido del archivo");
        validDto.setFileName("documento.pdf");

        assertDoesNotThrow(() -> InsuranceTermsConditionsService.validateFileContentAndName(validDto));
    }

    @Test
    void validateFileContentAndName_WithInvalidContent_ShouldThrowException() {
        UpdateInsuranceTermsConditionsFileContentDTO dtoWithNullContent = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithNullContent.setFileContent(null);
        dtoWithNullContent.setFileName("documento.pdf");

        OneboxRestException exception1 = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithNullContent));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_CONTENT.getErrorCode(), exception1.getErrorCode());

        UpdateInsuranceTermsConditionsFileContentDTO dtoWithEmptyContent = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithEmptyContent.setFileContent("   ");
        dtoWithEmptyContent.setFileName("documento.pdf");

        OneboxRestException exception2 = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithEmptyContent));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_CONTENT.getErrorCode(), exception2.getErrorCode());
    }

    @Test
    void validateFileContentAndName_WithInvalidFileName_ShouldThrowException() {
        UpdateInsuranceTermsConditionsFileContentDTO dtoWithNullName = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithNullName.setFileContent("Contenido");
        dtoWithNullName.setFileName(null);

        OneboxRestException exception1 = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithNullName));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME.getErrorCode(), exception1.getErrorCode());

        UpdateInsuranceTermsConditionsFileContentDTO dtoWithEmptyName = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithEmptyName.setFileContent("Contenido");
        dtoWithEmptyName.setFileName(" ");

        OneboxRestException exception2 = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithEmptyName));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME.getErrorCode(), exception2.getErrorCode());
    }

    @Test
    void validateFileContentAndName_WithSpacesInName_ShouldThrowException() {
        UpdateInsuranceTermsConditionsFileContentDTO dtoWithFileNameSpaces = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithFileNameSpaces.setFileContent("Contenido del archivo");
        dtoWithFileNameSpaces.setFileName("documento con espacios.pdf");

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithFileNameSpaces));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_SPACES.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void validateFileContentAndName_WithInvalidCharacters_ShouldThrowException() {
        UpdateInsuranceTermsConditionsFileContentDTO dtoWithInvalidCharacters = new UpdateInsuranceTermsConditionsFileContentDTO();
        dtoWithInvalidCharacters.setFileContent("Contenido del archivo");
        dtoWithInvalidCharacters.setFileName("test!?.pdf");

        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> InsuranceTermsConditionsService.validateFileContentAndName(dtoWithInvalidCharacters));
        assertEquals(ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_CHARACTERS.getErrorCode(), exception.getErrorCode());
    }
}