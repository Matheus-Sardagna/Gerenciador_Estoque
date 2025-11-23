package Models.Entidades;

import Models.EntidadeBase;

import java.util.Date;

public class Produto extends EntidadeBase {
    private String nome;
    private String categoria;
    private double precoUnitario;

    public Produto(int sku, String nome, String categoria, double precoUnitario, int quantidade) {
        super(sku, quantidade);
        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
    }

    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoUnitario() { return precoUnitario; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
}
