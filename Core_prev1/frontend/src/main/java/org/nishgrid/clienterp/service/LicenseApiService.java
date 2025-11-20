package org.nishgrid.clienterp.service;

import com.google.gson.*;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.util.SystemInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class LicenseApiService {

    private static final String API_URL = ApiService.getLicenseUrl() + "/licenses/validate";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                try { return LocalDate.parse(json.getAsString()); } catch (Exception e) { return null; }
            })
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .setPrettyPrinting()
            .create();

    public LicenseResponse validateLicense(String licenseKey) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject req = new JsonObject();
            req.addProperty("licenseKey", licenseKey);
            req.addProperty("systemId", SystemInfo.getSystemId());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(req.toString().getBytes(StandardCharsets.UTF_8));
            }

            InputStream input = (conn.getResponseCode() == 200) ? conn.getInputStream() : conn.getErrorStream();

            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                return gson.fromJson(reader, LicenseResponse.class);
            }

        } catch (Exception e) {
            LicenseResponse err = new LicenseResponse();
            err.setMessage("Network error. Please try again.");
            return err;
        }
    }
}
