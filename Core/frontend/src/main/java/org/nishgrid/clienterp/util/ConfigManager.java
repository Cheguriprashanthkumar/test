package org.nishgrid.clienterp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.nishgrid.clienterp.model.LicenseResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    private static final String CONFIG_PATH = "config.json";
    private static ConfigManager instance;

    private ConfigData config = new ConfigData();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        load();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void load() {
        try {
            Path path = Paths.get(CONFIG_PATH);
            if (Files.exists(path)) {
                String json = new String(Files.readAllBytes(path));
                ConfigData loaded = gson.fromJson(json, ConfigData.class);
                if (loaded != null) config = loaded;
            } else {
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSetupCompleted() {
        return config.setupCompleted;
    }

    public boolean isClientDetailsCompleted() {
        return config.clientDetailsCompleted;
    }

    public String getSystemId() {
        return config.systemId;
    }

    public String getLicenseKey() {
        return (config.licenseKey != null && !config.licenseKey.isBlank()) ? config.licenseKey : null;
    }

    // --- ADDED THIS METHOD ---
    public String getUniqueId() {
        return (config.uniqueId != null && !config.uniqueId.isBlank()) ? config.uniqueId : null;
    }
    // -------------------------

    public void saveClientDetails(boolean completed) {
        config.clientDetailsCompleted = completed;
        save();
    }

    public void markSetupCompleted(boolean completed) {
        config.setupCompleted = completed;
        save();
    }

    public void saveLicenseDetails(LicenseResponse response, String systemId, boolean setupCompleted) {
        saveLicenseDetails(response, systemId, setupCompleted, response.isClientDetailsCompleted());
    }

    public void saveLicenseDetails(LicenseResponse response, String systemId, boolean setupCompleted, boolean clientDetailsCompleted) {
        if (response == null) return;

        config.uniqueId = response.getUniqueId();
        config.licenseKey = response.getLicenseKey();
        config.fullName = response.getFullName();
        config.companyName = response.getCompanyName();
        config.emailAddress = response.getEmailAddress();
        config.startDate = response.getStartDate();
        config.endDate = response.getEndDate();
        config.systemId = systemId;
        config.setupCompleted = setupCompleted;
        config.clientDetailsCompleted = clientDetailsCompleted;

        save();
    }

    public LicenseResponse getSavedLicenseDetails() {
        LicenseResponse response = new LicenseResponse();
        response.setUniqueId(config.uniqueId);
        response.setLicenseKey(config.licenseKey);
        response.setFullName(config.fullName);
        response.setCompanyName(config.companyName);
        response.setEmailAddress(config.emailAddress);
        response.setStartDate(config.startDate);
        response.setEndDate(config.endDate);
        response.setSystemId(config.systemId);
        response.setSetupCompleted(config.setupCompleted);
        response.setClientDetailsCompleted(config.clientDetailsCompleted);
        return response;
    }


//    private static class ConfigData {
//        private String uniqueId;
//        private String licenseKey;
//        private String fullName;
//        private String companyName;
//        private String emailAddress;
//        private String startDate;
//        private String endDate;
//        private String systemId;
//        private boolean setupCompleted;
//        private boolean clientDetailsCompleted;
//    }
}