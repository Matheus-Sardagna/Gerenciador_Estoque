package Models.Entidades;

import Models.EntidadeBase;
import Models.Interfaces.Movimentacao;

import java.util.Date;

public class Entrada extends EntidadeBase implements Movimentacao {
        private final double valorUnitario;

        public Entrada(Date data, int sku, int quantidade, double valorUnitario) {
            super(data, sku, quantidade);  // delega inicialização para EntidadeBase
            this.valorUnitario = valorUnitario;
        }

        public double getValorUnitario() { return valorUnitario; }

        @Override
        public boolean isEntrada() { return true; }
}

