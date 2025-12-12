package es.onebox.atm.users.converter;

import es.onebox.atm.users.dto.ATMUserPromotion;
import es.onebox.atm.users.dto.ATMUserPromotionDTO;
import es.onebox.atm.users.dto.DiscountType;
import es.onebox.atm.users.dto.PromotionStatus;
import es.onebox.atm.users.dto.PromotionType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserPromotionConverter {

    private UserPromotionConverter() {
    }

    public static List<ATMUserPromotionDTO> convert(List<ATMUserPromotion> userPromotions) {
        if (userPromotions == null) {
            return Collections.emptyList();
        }

        return userPromotions
                .stream()
                .map(UserPromotionConverter::convert)
                .collect(Collectors.toList());
    }

    public static ATMUserPromotionDTO convert(ATMUserPromotion userPromotion) {
        ATMUserPromotionDTO userPromotionDTO = new ATMUserPromotionDTO();

        userPromotionDTO.setId(userPromotion.getPromotionId());
        userPromotionDTO.setValue(userPromotion.getValue());
        userPromotionDTO.setDiscountType(userPromotion.getDiscountType().equals("Numerico") ? DiscountType.FIXED : DiscountType.PERCENTAGE);
        userPromotionDTO.setName(userPromotion.getName());
        userPromotionDTO.setStatus(PromotionStatus.byName(userPromotion.getStatus()));
        userPromotionDTO.setPromotionType(userPromotion.getPromotionType().equals("Tarjeta monedero") ? PromotionType.WALLET : PromotionType.ONESHOT);
        userPromotionDTO.setStartDate(userPromotion.getInicioPromocion().toLocalDateTime());
        userPromotionDTO.setEndDate(userPromotion.getFinPromocion().toLocalDateTime());

        return userPromotionDTO;
    }

}
