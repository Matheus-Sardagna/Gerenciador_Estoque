package Services;

import Repositorios.BaseRepository;
import Services.Intefaces.IService;

import java.util.List;

public abstract class BaseService<T, R extends BaseRepository<T>> implements IService<T> {
    protected R repository;

    public BaseService(R repository) {
        this.repository = repository;
        carregarTodos();
    }

    protected abstract void carregarTodos();

    public void salvar() {
        repository.save(); // assim você pode salvar sem repetir nos Services
    }

    public List<T> listarTodos() {
        return repository.getAll();
    }

    public void adicionar(T entidade) {
        repository.getAll().add(entidade);
        salvar();
    }
}
