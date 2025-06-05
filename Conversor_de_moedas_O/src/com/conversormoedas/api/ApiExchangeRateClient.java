
package com.conversormoedas.api;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiExchangeRateClient {


    /**
     * Obtém as taxas de câmbio para uma moeda base específica usando a ExchangeRate-API.
     * @param baseCurrency O código da moeda base (ex: "USD").
     * @return Um JsonObject contendo as taxas de conversão ou null em caso de erro.
     * @throws IOException Se ocorrer um erro de rede ou comunicação.
     */
    public JsonObject getExchangeRates(String baseCurrency) throws IOException {
        String apiUrl = "https://v6.exchangerate-api.com/v6/0fb47ec364cffcfb37dd6f36/latest/" + baseCurrency;

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000); // Tempo limite de conexão de 5 segundos
            connection.setReadTimeout(5000);    // Tempo limite de leitura de 5 segundos

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                if ("success".equals(jsonResponse.get("result").getAsString())) {
                    return jsonResponse.getAsJsonObject("conversion_rates");
                } else {
                    String errorType = jsonResponse.has("error-type") ? jsonResponse.get("error-type").getAsString() : "Desconhecido";
                    System.err.println("Erro na resposta da API: " + errorType);
                    return null;
                }
            } else {
                System.err.println("Erro HTTP na requisição: " + responseCode + " - " + connection.getResponseMessage());
                // Tenta ler a mensagem de erro do stream de erro
                if (connection.getErrorStream() != null) {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorReader.close();
                    System.err.println("Mensagem de erro da API: " + errorResponse);
                }
                return null;
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}