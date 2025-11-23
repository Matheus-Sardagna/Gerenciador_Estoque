package Repositorios;

import java.util.List;

public abstract class BaseRepository<T> {
    protected List<T> itens;

    public List<T> getAll() {
        return itens;
    }

    public abstract void load();
    public abstract void save();
}