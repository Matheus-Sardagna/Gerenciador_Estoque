package Services.ServicesEntidades;

import Models.Entidades.Produto;
import Repositorios.ProdutoRepository;
import Services.BaseService;

import java.util.List;

public class ProdutoService extends BaseService<Produto, ProdutoRepository> {

    public ProdutoService() {
        super(new ProdutoRepository());
    }

    @Override
    protected void carregarTodos() {
        repository.load();
    }

    @Override
    public List<Produto> listarTodos() {
        return repository.getAll();
    }

    public int gerarProximoSku() {
        List<Produto> produtos = repository.getAll(); // pega sempre a lista atual
        return produtos.stream()
                .mapToInt(Produto::getSku)
                .max()
                .orElse(0) + 1; // se lista vazia, começa do 1
    }

    @Override
    public void adicionar(Produto produto) {
        if (produto.getSku() <= 0) {
            int proximoSku = repository.getAll().stream()
                    .mapToInt(Produto::getSku)
                    .max()
                    .orElse(0) + 1;
            produto.setSku(proximoSku);
        }

        boolean existe = repository.getAll().stream()
                .anyMatch(p -> p.getSku() == produto.getSku());
        if (existe) {
            throw new IllegalArgumentException("SKU já existe: " + produto.getSku());
        }

        repository.getAll().add(produto);
        repository.save();
    }

    public Produto getProdutoBySku(int sku) {
        for (Produto produto : repository.getAll()) {
            if (produto.getSku() == sku) {
                return produto;
            }
        }
        return null;
    }
}
