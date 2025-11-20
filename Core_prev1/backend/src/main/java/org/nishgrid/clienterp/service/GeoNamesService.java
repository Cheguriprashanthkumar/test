package org.nishgrid.clienterp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class GeoNamesService {

    private static final String GEONAMES_USERNAME = "prashanht";
    private static final String BASE_URL = "http://api.geonames.org";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getCountryInfo() {
        String url = BASE_URL + "/countryInfoJSON?username=" + GEONAMES_USERNAME;
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getStates(int countryGeonameId) {
        String url = BASE_URL + "/childrenJSON?geonameId=" + countryGeonameId + "&username=" + GEONAMES_USERNAME;
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getCities(int stateGeonameId) {
        String url = BASE_URL + "/childrenJSON?geonameId=" + stateGeonameId + "&username=" + GEONAMES_USERNAME;
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getPostalCodeDetails(String postalCode, String countryCode) {
        String url = BASE_URL + "/postalCodeLookupJSON?postalcode=" + postalCode +
                "&country=" + countryCode + "&username=" + GEONAMES_USERNAME;
        return restTemplate.getForObject(url, Map.class);
    }
}
