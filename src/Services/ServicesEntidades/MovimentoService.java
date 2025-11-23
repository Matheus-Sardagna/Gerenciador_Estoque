package Services.ServicesEntidades;

import Models.Interfaces.Movimentacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovimentoService {

    private final EntradaService entradaService;
    private final SaidaService saidaService;

    public MovimentoService(EntradaService entradaService, SaidaService saidaService) {
        this.entradaService = entradaService;
        this.saidaService = saidaService;
    }

    public List<Movimentacao> listarMovimentosOrdenados() {
        List<Movimentacao> movimentos = new ArrayList<>();

        movimentos.addAll(entradaService.listarTodos());
        movimentos.addAll(saidaService.listarTodos());

        movimentos.sort(Comparator.comparing(Movimentacao::getData));

        return movimentos;
    }
}
