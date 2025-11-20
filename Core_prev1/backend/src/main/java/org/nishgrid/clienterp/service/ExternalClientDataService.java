package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.ExternalClientData;
import java.util.Optional;

public interface ExternalClientDataService {

    Optional<ExternalClientData> fetchClientData(String licenseKey);
}