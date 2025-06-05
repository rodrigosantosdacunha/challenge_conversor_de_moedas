package com.conversormoedas.conversor;


public abstract class Conversor {
    protected String moedaOrigem;
    protected String moedaDestino;

    public Conversor(String moedaOrigem, String moedaDestino) {
        this.moedaOrigem = moedaOrigem;
        this.moedaDestino = moedaDestino;
    }

    public abstract double converter(double valor);

    public String getMoedaOrigem() {
        return moedaOrigem;
    }

    public String getMoedaDestino() {
        return moedaDestino;
    }
}