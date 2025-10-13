package org.example.service;

import com.google.gson.*;
import org.example.exception.ApiException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.model.POSITION;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";
    private static final Gson GSON = new Gson();
    public static List<Employee> fetchEmployeesFromAPI() throws ApiException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException("Błąd HTTP: " + response.statusCode() + " - " + response.body());
            }

            return parseJson(response.body());

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Błąd komunikacji z API: " + API_URL, e);
        }
    }

    private static List<Employee> parseJson(String json) throws ApiException {
        List<Employee> employees = new ArrayList<>();
        POSITION basePos = POSITION.PROGRAMISTA;

        try {
            JsonElement root = JsonParser.parseString(json);

            if (!root.isJsonArray()) {
                throw new ApiException("Oczekiwana jest tablica użytkowników");

            }
            JsonArray usersArray = root.getAsJsonArray();

            for (JsonElement userElement : usersArray) {
                if (!userElement.isJsonObject()) {
                    continue;
                }
                JsonObject userObject = userElement.getAsJsonObject();

                String fullName = userObject.get("name").getAsString();
                String email = userObject.get("email").getAsString();

                String companyName = null;
                if (userObject.has("company") && userObject.get("company").isJsonObject()) {
                    JsonObject companyObject = userObject.getAsJsonObject("company");
                    if (companyObject.has("name")) {
                        companyName = companyObject.get("name").getAsString();
                    }
                }

                if (fullName == null || email == null || companyName == null) {
                    System.err.println("Ostrzeżenie: pominięto użytkownika z brakującymi danymi.");
                    continue;
                }

                String[] nameParts = fullName.trim().split("\\s+", 2);
                String firstName = nameParts.length > 0 ? nameParts[0] : "N/A";
                String lastName = nameParts.length > 1 ? nameParts[1] : "N/A";

                Employee employee = new Employee(
                        firstName,
                        lastName,
                        email,
                        companyName,
                        basePos
                );
                employees.add(employee);
            }

        } catch (Exception e) {
            throw new ApiException("Błąd parsowania JSON lub mapowania na Employee", e);
        }
        return employees;
    }
}
