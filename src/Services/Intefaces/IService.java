package Services.Intefaces;

import Models.Entidades.Produto;

import java.util.List;

public interface IService<T> {
    List<T> listarTodos();
    void adicionar(T entidade);
}
