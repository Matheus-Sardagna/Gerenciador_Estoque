package Models.Entidades;

import Models.EntidadeBase;
import Models.Interfaces.Movimentacao;

import java.util.Date;

public class Saida extends EntidadeBase implements Movimentacao {
    public Saida(Date data, int sku, int quantidade) {
        super(data, sku, quantidade);  // Corrigido
    }

    @Override
    public double getValorUnitario() {
        // Opcional: buscar valorUnitario do Produto
        return 0;
    }

    @Override
    public boolean isEntrada() {
        return false;
    }
}
