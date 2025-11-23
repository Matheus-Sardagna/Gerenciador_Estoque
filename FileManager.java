package com.mycompany.trabalhofinal;

import java.io.*;
import java.util.*;

public class FileManager {

    private static final String PROD_FILE = "produtos.csv";
    private static final String ENT_FILE = "entradas.csv";
    private static final String SAI_FILE = "saidas.csv";

    private List<Produto> produtos = new ArrayList<>();
    private List<Entrada> entradas = new ArrayList<>();
    private List<Saida> saidas = new ArrayList<>();

    public void loadAll() {
        loadProdutos();
        loadEntradas();
        loadSaidas();
    }

    private void loadProdutos() {
        produtos.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(PROD_FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                produtos.add(new Produto(
                        Integer.parseInt(p[0]), p[1], p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4])));
            }
        } catch (Exception e) {}
    }

    private void loadEntradas() {
        entradas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ENT_FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                entradas.add(new Entrada(p[0], Integer.parseInt(p[1]),
                        Integer.parseInt(p[2]), Double.parseDouble(p[3])));
            }
        } catch (Exception e) {}
    }

    private void loadSaidas() {
        saidas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(SAI_FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                saidas.add(new Saida(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    public void saveAll() {
        saveProdutos();
        saveEntradas();
        saveSaidas();
    }

    private void saveProdutos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PROD_FILE))) {
            for (Produto p : produtos) {
                pw.println(p.getSku() + ";" + p.getNome() + ";" + p.getCategoria() + ";"
                        + p.getPrecoUnitario() + ";" + p.getQuantidade());
            }
        } catch (Exception e) {}
    }

    private void saveEntradas() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ENT_FILE))) {
            for (Entrada e : entradas) {
                pw.println(e.getData() + ";" + e.getSku() + ";" +
                        e.getQuantidade() + ";" + e.getValorUnitario());
            }
        } catch (Exception e) {}
    }
    

    private void saveSaidas() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAI_FILE))) {
            for (Saida s : saidas) {
                pw.println(s.getData() + ";" + s.getSku() + ";" + s.getQuantidade());
            }
        } catch (Exception e) {}
    }

    public List<Produto> getProdutos() { return produtos; }
    public List<Entrada> getEntradas() { return entradas; }
    public List<Saida> getSaidas() { return saidas; }

    public void addProduto(Produto p) {
        produtos.add(p);
        saveProdutos();
    }

    public void addEntrada(Entrada e) {
        entradas.add(e);
        Produto p = getProdutoBySku(e.getSku());
        if (p != null) p.addEstoque(e.getQuantidade());
        saveAll();
    }
    

    public void addSaida(Saida s) {
        saidas.add(s);
        Produto p = getProdutoBySku(s.getSku());
        if (p != null) p.removeEstoque(s.getQuantidade());
        saveAll();
    }

    public Produto getProdutoBySku(int sku) {
        return produtos.stream().filter(x -> x.getSku() == sku).findFirst().orElse(null);
    }

    // LISTA MOVIMENTOS ORDENADOS
    public List<Movimentacao> getMovimentosOrdenados() {
        List<Movimentacao> movs = new ArrayList<>();
        movs.addAll(entradas);
        movs.addAll(saidas);

        movs.sort(Comparator.comparing(Movimentacao::getData));
        return movs;
    }
}
