package Repositorios;

import Models.Entidades.Saida;
import Services.Utils.FileUtilsService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaidaRepository extends BaseRepository<Saida> {
    private static String FILE = "saidas.csv";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public SaidaRepository() {
        itens = new ArrayList<>();
        try {
            String caminho = "src/Dados/saidas.csv";
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
                itens.add(new Saida(data, sku, quantidade));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Saida s : itens) {
                pw.println(sdf.format(s.getData()) + ";" + s.getSku() + ";" + s.getQuantidade());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
