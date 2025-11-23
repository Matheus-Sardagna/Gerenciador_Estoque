package Services.ServicesEntidades;

import Models.Entidades.Entrada;
import Repositorios.EntradaRepository;
import Services.BaseService;

import java.util.List;

public class EntradaService extends BaseService<Entrada, EntradaRepository> {

    public EntradaService() {
        super(new EntradaRepository());
    }

    @Override
    protected void carregarTodos() {
        repository.load(); // usa o repository da superclasse
    }

    public List<Entrada> buscarPorSku(int sku) {
        return repository.getAll().stream()
                .filter(e -> e.getSku() == sku)
                .toList();
    }

    @Override
    public List<Entrada> listarTodos() {
        return repository.getAll();
    }

    @Override
    public void adicionar(Entrada entrada) {
        repository.getAll().add(entrada);
        repository.save();
    }
}
