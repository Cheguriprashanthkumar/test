package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.service.GeoNamesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/geonames")
@RequiredArgsConstructor
public class GeoNamesController {

    private final GeoNamesService geoNamesService;

    @GetMapping("/countries")
    public Map<String, Object> getCountries() {
        return geoNamesService.getCountryInfo();
    }

    @GetMapping("/states")
    public Map<String, Object> getStates(@RequestParam("countryGeonameId") final int countryGeonameId) {
        return geoNamesService.getStates(countryGeonameId);
    }

    @GetMapping("/cities")
    public Map<String, Object> getCities(@RequestParam("stateGeonameId") final int stateGeonameId) {
        return geoNamesService.getCities(stateGeonameId);
    }

    @GetMapping("/postalcode")
    public Map<String, Object> getPostalCodeDetails(
            @RequestParam("postalCode") final String postalCode,
            @RequestParam("countryCode") final String countryCode) {
        return geoNamesService.getPostalCodeDetails(postalCode, countryCode);
    }
}
