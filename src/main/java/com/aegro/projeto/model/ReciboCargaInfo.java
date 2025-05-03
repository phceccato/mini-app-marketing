package com.aegro.projeto.model;

public class ReciboCargaInfo {
    private String data;
    private String nomeProdutor;
    private String tipoCultura;
    private String pesoBruto;
    private String umidade;
    private String impureza;
    private String pesoLiquido;
    
    // Construtor padrão
    public ReciboCargaInfo() {
    }

    // Construtor com todos os campos
    public ReciboCargaInfo(String data, String nomeProdutor, String tipoCultura,
                           String pesoBruto, String umidade, String impureza, String pesoLiquido) {
        this.data = data;
        this.nomeProdutor = nomeProdutor;
        this.tipoCultura = tipoCultura;
        this.pesoBruto = pesoBruto;
        this.umidade = umidade;
        this.impureza = impureza;
        this.pesoLiquido = pesoLiquido;
    }

    // Getters e Setters
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNomeProdutor() {
        return nomeProdutor;
    }

    public void setNomeProdutor(String nomeProdutor) {
        this.nomeProdutor = nomeProdutor;
    }

    public String getTipoCultura() {
        return tipoCultura;
    }

    public void setTipoCultura(String tipoCultura) {
        this.tipoCultura = tipoCultura;
    }

    public String getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(String pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public String getUmidade() {
        return umidade;
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getImpureza() {
        return impureza;
    }

    public void setImpureza(String impureza) {
        this.impureza = impureza;
    }

    public String getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(String pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }
}

