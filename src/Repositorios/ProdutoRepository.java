package Repositorios;

import Models.Entidades.Produto;
import Services.Utils.FileUtilsService;

import java.io.*;
import java.util.*;

public class ProdutoRepository extends BaseRepository<Produto> {
    private static  String FILE = "produtos.csv";

    public ProdutoRepository() {
        itens = new ArrayList<>();
        try {
            String caminho = "src/Dados/produtos.csv";
            FILE = String.valueOf(FileUtilsService.getOrCreateFile(caminho));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        itens.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                itens.add(new Produto(
                        Integer.parseInt(p[0]), p[1], p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4])
                ));
            }
        } catch (Exception e) {}
    }

    @Override
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Produto p : itens) {
                pw.println(p.getSku() + ";" + p.getNome() + ";" + p.getCategoria() + ";" +
                        p.getPrecoUnitario() + ";" + p.getQuantidade());
            }
        } catch (Exception e) {}
    }
}
