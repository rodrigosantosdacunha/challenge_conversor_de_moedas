package com.conversormoedas;

import com.conversormoedas.api.ApiExchangeRateClient;
import com.conversormoedas.conversor.ConversorMoeda;

import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ConversorMoedasApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ApiExchangeRateClient apiClient = new ApiExchangeRateClient(); // Instância do cliente da API

        // Usar LinkedHashMap para manter a ordem de inserção
        Map<Integer, ConversorMoeda> conversores = new LinkedHashMap<>();

        // Inicializa os conversores
        conversores.put(1, new ConversorMoeda("USD", "BRL", apiClient));
        conversores.put(2, new ConversorMoeda("EUR", "BRL", apiClient));
        conversores.put(3, new ConversorMoeda("GBP", "BRL", apiClient));
        conversores.put(4, new ConversorMoeda("BRL", "USD", apiClient));
        conversores.put(5, new ConversorMoeda("BRL", "EUR", apiClient));
        conversores.put(6, new ConversorMoeda("BRL", "GBP", apiClient));

        int escolha;

        do {
            System.out.println("===============================");
            System.out.println("   $  CONVERSOR DE MOEDAS   $  ");
            System.out.println("===============================");
            System.out.println("Escolha uma opção de conversão:");
            System.out.println();

            // Imprime as opções dinamicamente do mapa
            conversores.forEach((key, conversor) ->
                    System.out.printf("%d. %s (%s) para %s (%s)%n", key,
                            obterNomeMoeda(conversor.getMoedaOrigem()), conversor.getMoedaOrigem(),
                            obterNomeMoeda(conversor.getMoedaDestino()), conversor.getMoedaDestino()));

            System.out.println("0. Sair");
            System.out.print("Digite sua escolha: ");

            try {
                escolha = scanner.nextInt();

                if (escolha == 0) {
                    System.out.println("Saindo do conversor de moedas. Obrigado!");
                } else if (conversores.containsKey(escolha)) {
                    ConversorMoeda conversorSelecionado = conversores.get(escolha);

                    if (conversorSelecionado.isTaxaDisponivel()) {
                        System.out.printf("Você escolheu: %s para %s%n",
                                obterNomeMoeda(conversorSelecionado.getMoedaOrigem()),
                                obterNomeMoeda(conversorSelecionado.getMoedaDestino()));
                        System.out.printf("Digite o valor em %s: ", conversorSelecionado.getMoedaOrigem());
                        double valorEntrada = scanner.nextDouble();

                        double valorConvertido = conversorSelecionado.converter(valorEntrada);
                        if (valorConvertido != -1.0) { // Verifica se a conversão foi bem-sucedida
                            System.out.printf("O valor em %s (%s) é: %.2f%n",
                                    obterNomeMoeda(conversorSelecionado.getMoedaDestino()),
                                    conversorSelecionado.getMoedaDestino(),
                                    valorConvertido);
                        }
                    } else {
                        System.out.println("Não foi possível realizar a conversão. Taxa de câmbio não disponível.");
                    }
                } else {
                    System.out.println("Opção inválida. Por favor, tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.next(); // Limpa a entrada inválida
                escolha = -1; // Mantém o loop
            } catch (Exception e) { // Captura outras exceções inesperadas
                System.err.println("Ocorreu um erro inesperado: " + e.getMessage());

                escolha = -1;
            }
            System.out.println();
        } while (escolha != 0);

        scanner.close();
    }

    // Método auxiliar para obter o nome completo da moeda (opcional, para melhor exibição)
    private static String obterNomeMoeda(String codigoMoeda) {
        return switch (codigoMoeda) {
            case "USD" -> "Dólar Americano";
            case "BRL" -> "Real Brasileiro";
            case "EUR" -> "Euro";
            case "GBP" -> "Libra Esterlina";
            default -> codigoMoeda;
        };
    }
}

