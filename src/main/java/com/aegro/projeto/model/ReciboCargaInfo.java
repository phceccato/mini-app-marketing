package com.aegro.projeto.model;

public class ReciboCargaInfo {
    private String peso;
    private String umidade;
    private String tipoCarga;
    private String folhagem;
    private String nomeMotorista;

    // constructor
    public ReciboCargaInfo() {}

    public ReciboCargaInfo(String peso, String umidade, String tipoCarga, String folhagem, String nomeMotorista) {
    this.peso = peso;
    this.umidade = umidade;
    this.tipoCarga = tipoCarga;
    this.folhagem = folhagem;
    this.nomeMotorista = nomeMotorista;
    }

    // Getters and Setters
    public String getPeso() { return peso; }
    public void setPeso(String peso) { this.peso = peso; }

    public String getUmidade() { return umidade; }
    public void setUmidade(String umidade) { this.umidade = umidade; }

    public String getTipoCarga() { return tipoCarga; }
    public void setTipoCarga(String tipoCarga) { this.tipoCarga = tipoCarga; }

    public String getFolhagem() { return folhagem; }
    public void setFolhagem(String folhagem) { this.folhagem = folhagem; }

    public String getNomeMotorista() { return nomeMotorista; }
    public void setNomeMotorista(String nomeMotorista) { this.nomeMotorista = nomeMotorista; }
}
