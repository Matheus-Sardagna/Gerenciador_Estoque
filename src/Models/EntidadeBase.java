package Models;

import java.util.Date;

public abstract class EntidadeBase {
    protected Date data;
    protected int sku;
    protected int quantidade;

    protected EntidadeBase(Date data, int sku, int quantidade) {
        this.data = data;
        this.sku = sku;
        this.quantidade = quantidade;
    }

    protected EntidadeBase(int sku, int quantidade) {
        this.data = new Date();
        this.sku = sku;
        this.quantidade = quantidade;
    }

    public Date getData() { return data; }
    public int getSku() { return sku; }
    public int getQuantidade() { return quantidade; }

    public void setData(Date data) { this.data = data; }
    public void setSku(int sku) { this.sku = sku; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
