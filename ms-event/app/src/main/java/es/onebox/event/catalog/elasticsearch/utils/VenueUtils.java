package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.event.venues.domain.VenueRecord;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VenueUtils {

    private VenueUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static Boolean areMultiLocation(List<VenueRecord> venues) {
        Set<String> locations = venues.stream()
                .map(VenueRecord::getMunicipality)
                .map(VenueUtils::simplifyCountrySubdivisionName)
                .collect(Collectors.toSet());
        return locations.size() > 1;
    }

    private static String simplifyCountrySubdivisionName(String countrySubdivisionName) {
        // Descomposición canónica
        String normalized = Normalizer.normalize(countrySubdivisionName, Normalizer.Form.NFD);
        // Nos quedamos únicamente con los caracteres ASCII
        Pattern pattern = Pattern.compile("\\P{ASCII}+");
        String result = pattern.matcher(normalized).replaceAll("");
        Pattern pattern2 = Pattern.compile("\\'|·|\\-");
        return pattern2.matcher(result).replaceAll("").toUpperCase().trim();
    }
}
