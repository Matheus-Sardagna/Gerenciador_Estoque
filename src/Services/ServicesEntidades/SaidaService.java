package Services.ServicesEntidades;

import Models.Entidades.Saida;
import Repositorios.SaidaRepository;
import Services.BaseService;

import java.util.List;

public class SaidaService extends BaseService<Saida, SaidaRepository> {

    public SaidaService() {
        super(new SaidaRepository());
    }

    @Override
    protected void carregarTodos() {
        repository.load(); // usa o repository da superclasse
    }

    public List<Saida> buscarPorSku(int sku) {
        return repository.getAll().stream()
                .filter(e -> e.getSku() == sku)
                .toList();
    }

    @Override
    public List<Saida> listarTodos() {
        return repository.getAll();
    }

    @Override
    public void adicionar(Saida saida) {
        repository.getAll().add(saida);
        repository.save();
    }
}
