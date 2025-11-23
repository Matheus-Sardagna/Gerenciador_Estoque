package Models.Interfaces;

import java.util.Date;

public interface Movimentacao {
    Date getData();
    int getSku();
    int getQuantidade();
    double getValorUnitario();
    boolean isEntrada();
}

