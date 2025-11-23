package Services.ServicesEntidades;

import Models.Entidades.Entrada;
import Models.Entidades.Produto;
import Models.Entidades.Saida;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class EstoqueService {
    private final List<Entrada> entradas;
    private final List<Saida> saidas;
    private final ProdutoService produtoService;

    public EstoqueService(EntradaService entradaService, SaidaService saidaService, ProdutoService produtoService) {
        this.entradas = entradaService.listarTodos();
        this.saidas = saidaService.listarTodos();
        this.produtoService = produtoService;
    }

    public double calcularSaldo(LocalDate inicio, LocalDate fim) {
        double saldo = 0;

        for (Entrada e : entradas) {
            LocalDate data = e.getData().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                saldo -= e.getQuantidade() * e.getValorUnitario();
            }
        }

        for (Saida s : saidas) {
            LocalDate data = s.getData().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                Optional<Produto> produceOpt = produtoService.listarTodos().stream()
                        .filter(p -> p.getSku() == s.getSku())
                        .findFirst();
                if (produceOpt.isPresent()) {
                    Produto p = produceOpt.get();
                    saldo += s.getQuantidade() * p.getPrecoUnitario();
                }
            }
        }

        return saldo;
    }
}
