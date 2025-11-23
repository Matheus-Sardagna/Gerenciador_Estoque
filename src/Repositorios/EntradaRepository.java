package Repositorios;

import Models.Entidades.Entrada;
import Services.Utils.FileUtilsService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EntradaRepository extends BaseRepository<Entrada> {
    private static String FILE = "entradas.csv";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public EntradaRepository() {
        itens = new ArrayList<>();
        try {
            String caminho = "src/Dados/entrada.csv";
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
                Date data = sdf.parse(p[0]);
                int sku = Integer.parseInt(p[1]);
                int quantidade = Integer.parseInt(p[2]);
                double valorUnitario = Double.parseDouble(p[3]);
                itens.add(new Entrada(data, sku, quantidade, valorUnitario));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Entrada e : itens) {
                pw.println(sdf.format(e.getData()) + ";" + e.getSku() + ";" + e.getQuantidade() + ";" + e.getValorUnitario());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
