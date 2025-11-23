package com.mycompany.trabalhofinal;

public interface Movimentacao {
    String getData();
    int getSku();
    int getQuantidade();
    double getValorUnitario();
    boolean isEntrada();
}

class Produto {
    private int sku;
    private String nome;
    private String categoria;
    private double precoUnitario;
    private int quantidade;

    public Produto(int sku, String nome, String categoria, double precoUnitario, int quantidade) {
        this.sku = sku;
        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public int getSku() {
        return sku; 
    }
    public String getNome() { 
        return nome; 
    }
    public String getCategoria() { 
        return categoria; 
    }
    public double getPrecoUnitario() { 
        return precoUnitario; 
    }
    public int getQuantidade() { 
        return quantidade; 
    }

    public void addEstoque(int q) { 
        quantidade += q; 
    }
    public void removeEstoque(int q) { 
        quantidade -= q; 
    }
}

class Entrada implements Movimentacao {
    private String data;
    private int sku;
    private int quantidade;
    private double valorUnitario;

    public Entrada(String data, int sku, int quantidade, double valorUnitario) {
        this.data = data;
        this.sku = sku;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public String getData() { 
        return data; 
    }
    public int getSku() { 
        return sku; 
    }
    public int getQuantidade() { 
        return quantidade; 
    }
    public double getValorUnitario() { 
        return valorUnitario; 
    }
    public boolean isEntrada() { 
        return true; 
    }
}

class Saida implements Movimentacao {
    private String data;
    private int sku;
    private int quantidade;

    public Saida(String data, int sku, int quantidade) {
        this.data = data;
        this.sku = sku;
        this.quantidade = quantidade;
    }

    public String getData() { 
        return data; 
    }
    public int getSku() { 
        return sku; 
    }
    public int getQuantidade() { 
        return quantidade; 
    }
    public double getValorUnitario() { 
        return 0; 
    }
    public boolean isEntrada() { 
        return false; 
    }
}
