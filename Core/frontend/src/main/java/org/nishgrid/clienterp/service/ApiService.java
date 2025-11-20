package org.nishgrid.clienterp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.nishgrid.clienterp.dto.*;
import org.nishgrid.clienterp.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String BASE_URL = "https://client-nishgrid.co.in/ngst/api";
    private static final String LICENSE_URL = "https://client-nishgrid.co.in/lv/api";

//    https://client-nishgrid.co.in/ngst/api"
    //    https://client-nishgrid.co.in/api
    public static String getBaseUrl() {
        return BASE_URL;
    }
    public static String getLicenseUrl(){return LICENSE_URL;}

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }


    private <T> CompletableFuture<T> sendAsync(HttpRequest request, TypeReference<T> typeReference) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String responseBody = response.body();
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        if (response.statusCode() == 204 || responseBody == null || responseBody.isBlank()) {
                            return null;
                        }
                        try {
                            return objectMapper.readValue(responseBody, typeReference);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to parse JSON response for " + request.uri(), e);
                        }
                    } else {
                        throw new RuntimeException("HTTP request to " + request.uri() + " failed with status: " + response.statusCode() + " Body: " + responseBody);
                    }
                });
    }

    private HttpRequest.BodyPublisher ofMultipartData(Map<Object, Object> data, String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        String separator = "--" + boundary + "\r\nContent-Disposition: form-data; name=";

        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator.getBytes(StandardCharsets.UTF_8));

            if (entry.getValue() instanceof java.nio.file.Path path) {
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n"
                        + "Content-Type: application/json\r\n\r\n"
                        + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    // --- VENDOR API METHODS ---

    public CompletableFuture<List<Vendor>> getVendors() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/vendors")).GET().build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<Vendor> createVendor(Vendor vendor) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/vendors"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(vendor))).build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<Vendor> updateVendor(long id, Vendor vendor) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/vendors/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(vendor))).build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<Void> deleteVendor(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/vendors/" + id)).DELETE().build();
        return sendAsync(request, new TypeReference<>() {});
    }

    // --- PRODUCT API METHODS ---

    public CompletableFuture<List<ProductCatalog>> getProducts() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/products")).GET().build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<ProductCatalog> createProduct(ProductCatalog product) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(product))).build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<ProductCatalog> updateProduct(long id, ProductCatalog product) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/products/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(product))).build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<Void> deleteProduct(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/products/" + id)).DELETE().build();
        return sendAsync(request, new TypeReference<>() {});
    }


    // --- GENERAL DATA METHODS ---

    public CompletableFuture<List<Customer>> getCustomers() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/customers")).GET().build();
        return sendAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<List<SalesInvoiceResponse>> getAllSalesInvoices() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/invoices")).GET().build();
        return sendAsync(request, new TypeReference<>() {});
    }


    public CompletableFuture<HttpResponse<String>> importSqlBackup(File file) {
        try {
            String boundary = "---" + UUID.randomUUID().toString();
            Map<Object, Object> data = Map.of("file", file.toPath());

            HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/backup/import/sql"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMultipartData(data, boundary))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<BackupLog>> getBackupLogs() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/backup-logs")).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String responseBody = response.body();
                    if (responseBody == null || responseBody.isBlank()) {
                        return Collections.emptyList();
                    }
                    try {
                        return objectMapper.readValue(responseBody,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, BackupLog.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                });
    }
    public CompletableFuture<List<BankDetailsResponse>> getAllBankDetails() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/bank-details")).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        return objectMapper.readValue(response.body(), new TypeReference<>() {});
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                });
    }

    public CompletableFuture<BankDetailsResponse> createBankDetails(BankDetailsRequest request, File file) {
        try {
            String boundary = "---" + UUID.randomUUID().toString();
            Map<Object, Object> data = new java.util.HashMap<>();
            data.put("details", objectMapper.writeValueAsString(request));
            if (file != null) {
                data.put("qrCode", file.toPath());
            }
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(BASE_URL + "/bank-details"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMultipartData(data, boundary))
                    .build();
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            return objectMapper.readValue(response.body(), BankDetailsResponse.class);
                        } catch (Exception e) { throw new RuntimeException(e); }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<BankDetailsResponse> updateBankDetails(long id, BankDetailsRequest request, File file) {
        try {
            String boundary = "---" + UUID.randomUUID().toString();
            Map<Object, Object> data = new java.util.HashMap<>();
            data.put("details", objectMapper.writeValueAsString(request));
            if (file != null) {
                data.put("qrCode", file.toPath());
            }
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(BASE_URL + "/bank-details/" + id))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .PUT(ofMultipartData(data, boundary))
                    .build();
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            return objectMapper.readValue(response.body(), BankDetailsResponse.class);
                        } catch (Exception e) { throw new RuntimeException(e); }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> deleteBankDetails(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/bank-details/" + id))
                .DELETE()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {});
    }

    // --- COMPANY DETAILS API METHODS ---

    public CompletableFuture<List<CompanyDetails>> getAllCompanyDetails() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/company-details")).GET().build();
        return sendAsync(request, new TypeReference<List<CompanyDetails>>() {});
    }

    public CompletableFuture<CompanyDetails> createCompanyDetails(CompanyDetails details) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/company-details"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(details))).build();
        return sendAsync(request, new TypeReference<CompanyDetails>() {});
    }

    public CompletableFuture<CompanyDetails> updateCompanyDetails(long id, CompanyDetails details) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/company-details/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(details))).build();
        return sendAsync(request, new TypeReference<CompanyDetails>() {});
    }

    public CompletableFuture<Void> deleteCompanyDetails(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/company-details/" + id)).DELETE().build();
        return sendAsync(request, new TypeReference<Void>() {});
    }

}