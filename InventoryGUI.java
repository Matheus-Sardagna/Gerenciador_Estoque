/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.trabalhofinal;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.Locale;
import java.time.format.DateTimeParseException;
import java.util.Map;

import java.time.format.DateTimeFormatter;


/**
 *
 * @author Usuario
 */
public class InventoryGUI extends javax.swing.JFrame {
    
    /**
     * Creates new form NewJFrame
     */
    public InventoryGUI() {
        initComponents();
        fm.loadAll();
        atualizarTabelaProdutos();
        atualizarTabelaEntradas();
        atualizarTabelaSaidas();
    }
    
    
    private LocalDate parseDataFlex(String dataStr) {
        if (dataStr == null) {
            throw new IllegalArgumentException("Data vazia (null)");
        }

    // Normaliza: trim e remove caracteres invisíveis (NBSP, BOM, tabs)
        dataStr = dataStr.trim()
                         .replace("\uFEFF", "")   // BOM
                         .replace("\u00A0", " ")  // NBSP -> espaço
                         .replace("\t", " ")
                         .replaceAll("\\s+", " ");

        if (dataStr.isEmpty()) {
            throw new IllegalArgumentException("Data vazia (string vazia)");
        }

        String original = dataStr; // para debug
        dataStr = dataStr.toLowerCase(Locale.ROOT);

    // Substitui separadores variados por "/"
        dataStr = dataStr.replace("-", "/").replace(".", "/").replace("\\", "/");

    // Mapeia nomes de mês (pt e en) para número
        Map<String, String> meses = Map.ofEntries(
            Map.entry("jan", "01"),
            Map.entry("fev", "02"),
            Map.entry("mar", "03"),
            Map.entry("abr", "04"),
            Map.entry("mai", "05"),
            Map.entry("jun", "06"),
            Map.entry("jul", "07"),
            Map.entry("ago", "08"),
            Map.entry("set", "09"),
            Map.entry("out", "10"),
            Map.entry("nov", "11"),
            Map.entry("dez", "12")
         );

    // substitui nomes de mês por número (ex: "22 nov 2025" -> "22/11/2025")
    for (Map.Entry<String, String> me : meses.entrySet()) {
        String key = me.getKey();
        String val = me.getValue();
        // usa regex para pegar casos como " 22nov2025", "22 nov 2025", "22-nov-2025"
        dataStr = dataStr.replaceAll("(?i)\\b" + key + "\\b", val);
    }

    // Se ainda houver letras, remover espaços extras
    dataStr = dataStr.trim().replaceAll("/+", "/");

    // Lista de padrões a tentar, em ordem
    DateTimeFormatter[] formatos = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yy"),
        DateTimeFormatter.ofPattern("d/M/yy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyy/M/d"),
        DateTimeFormatter.ofPattern("ddMMyyyy"),
        DateTimeFormatter.ofPattern("dMMyyyy"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    // Tenta com os padrões já com "/" já substituído
    for (DateTimeFormatter fmt : formatos) {
        try {
            return LocalDate.parse(dataStr, fmt);
        } catch (DateTimeParseException ex) {
            // tenta próximo
        }
    }

    // tentativa extra: separar por "/" e rearranjar se veio em ordem ISO 'yyyy/MM/dd'
    String[] parts = dataStr.split("/");
    if (parts.length == 3) {
        // tenta detectar se está em yyyy/MM/dd sem sucesso anterior
        if (parts[0].length() == 4) {
            try {
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int d = Integer.parseInt(parts[2]);
                return LocalDate.of(y, m, d);
            } catch (Exception ex) {}
        }
        // tenta dd/MM/yyyy novamente com parse por inteiros
        try {
            int d = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2].length() == 2 ? ("20" + parts[2]) : parts[2]);
            return LocalDate.of(y, m, d);
        } catch (Exception ex) {}
    }

    // tentativa final: extrai apenas dígitos
    String digits = dataStr.replaceAll("\\D", "");
    if (digits.length() == 8) { // ddMMyyyy
        try {
            int d = Integer.parseInt(digits.substring(0,2));
            int m = Integer.parseInt(digits.substring(2,4));
            int y = Integer.parseInt(digits.substring(4,8));
            return LocalDate.of(y, m, d);
        } catch (Exception ex) {}
    } else if (digits.length() == 6) { // ddMMyy
        try {
            int d = Integer.parseInt(digits.substring(0,2));
            int m = Integer.parseInt(digits.substring(2,4));
            int y = 2000 + Integer.parseInt(digits.substring(4,6));
            return LocalDate.of(y, m, d);
        } catch (Exception ex) {}
    }

    System.out.println("parseDataFlex failed para: '" + original + "' -> normalized: '" + dataStr + "'");

    throw new IllegalArgumentException("Formato de data inválido: " + original);
}




    
    private void atualizarTabelaEntradas() {
        DefaultTableModel model = (DefaultTableModel) tabAdicao.getModel();
        model.setRowCount(0);

    for (Entrada e : fm.getEntradas()) {
        model.addRow(new Object[]{
            e.getData(),
            e.getSku(),
            e.getQuantidade(),
            e.getValorUnitario()
        });
    }
}


    private void atualizarTabelaProdutos() {
        DefaultTableModel model = (DefaultTableModel) tabProduto.getModel();
        model.setRowCount(0);

    for (Produto p : fm.getProdutos()) {
        model.addRow(new Object[]{
            p.getSku(), p.getNome(), p.getCategoria(),
            p.getPrecoUnitario(), p.getQuantidade()
        });
    }
}
        private void atualizarTabelaSaidas() {
        DefaultTableModel model = (DefaultTableModel) tabRemocao.getModel();
        model.setRowCount(0);

        for (Saida s : fm.getSaidas()) {
            model.addRow(new Object[]{
                s.getData(), s.getSku(), s.getQuantidade()
            });
        }
    }
        private void atualizarTabelaMovimentos() {
        DefaultTableModel model = (DefaultTableModel) tabMovimentos.getModel();
        model.setRowCount(0);

    // Carregar entradas
    for (Entrada e : fm.getEntradas()) {
        model.addRow(new Object[]{
            e.getData(),
            "ENTRADA",
            e.getSku(),
            e.getQuantidade(),
            e.getValorUnitario()
        });
    }

    // Carregar saídas
    for (Saida s : fm.getSaidas()) {
        model.addRow(new Object[]{
            s.getData(),
            "SAÍDA",
            s.getSku(),
            s.getQuantidade(),
            "—"
        });
    }
}



    
    FileManager fm = new FileManager();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tab = new javax.swing.JTabbedPane();
        jpProduto = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txSKU = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txNome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jcCategoria = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txPreco = new javax.swing.JTextField();
        jcTabela = new javax.swing.JScrollPane();
        tabProduto = new javax.swing.JTable();
        jbNovo = new javax.swing.JButton();
        jbExcluir = new javax.swing.JButton();
        jbSalvar = new javax.swing.JButton();
        jpEntrada = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txSkuEntrada = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txDataEntrada = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txQtdEntrada = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txValorEntrada = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabAdicao = new javax.swing.JTable();
        jbRegEntrada = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jpSaida = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txSkuSaida = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txDataSaida = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txQtdSaida = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabRemocao = new javax.swing.JTable();
        jbRegSaida = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jpSaldo = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txDataInicial = new javax.swing.JTextField();
        txDataFim = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txSaldoTotal = new javax.swing.JTextField();
        jbChecarSaldo = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jpMovimentos = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabMovimentos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        bntProduto = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnEntry = new javax.swing.JButton();
        btnSaldo = new javax.swing.JButton();
        btnLista = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tab.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tab.setPreferredSize(new java.awt.Dimension(450, 772));

        jpProduto.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jpProduto.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Cadastro de Produtos");
        jLabel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("SKU");

        txSKU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txSKUActionPerformed(evt);
            }
        });

        jLabel3.setText("Nome");

        jLabel4.setText("Categoria");

        jcCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hardware", "Perifericos", "Acessorios", "Outros" }));

        jLabel5.setText("Preço Unitario Padrão");

        txPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txPrecoActionPerformed(evt);
            }
        });

        tabProduto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "SKU", "Nome", "Categoria", "Valor Unitario", "Estoque"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jcTabela.setViewportView(tabProduto);

        jbNovo.setText("Novo");

        jbExcluir.setText("Excluir");

        jbSalvar.setText("Salvar");
        jbSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpProdutoLayout = new javax.swing.GroupLayout(jpProduto);
        jpProduto.setLayout(jpProdutoLayout);
        jpProdutoLayout.setHorizontalGroup(
            jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpProdutoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbExcluir)
                    .addComponent(jbNovo))
                .addGap(21, 21, 21))
            .addGroup(jpProdutoLayout.createSequentialGroup()
                .addGroup(jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProdutoLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel4)
                                .addComponent(jLabel2)
                                .addComponent(txSKU)
                                .addComponent(jLabel3)
                                .addComponent(txNome)
                                .addComponent(jcCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(txPreco, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))))
                    .addGroup(jpProdutoLayout.createSequentialGroup()
                        .addGap(202, 202, 202)
                        .addComponent(jbSalvar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpProdutoLayout.setVerticalGroup(
            jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProdutoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jpProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jbNovo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbExcluir)
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txSKU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbSalvar)
                .addGap(15, 15, 15)
                .addComponent(jcTabela, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        tab.addTab("", jpProduto);

        jLabel6.setText("SKU");

        jLabel7.setText("Data");

        txDataEntrada.setToolTipText("");

        jLabel8.setText("Produtos a Adicionar");

        jLabel9.setText("Valor Unitario");

        tabAdicao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Data", "SKU do produto", "Quantidade adicionada"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabAdicao);

        jbRegEntrada.setText("Registrar");
        jbRegEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegEntradaActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Registrar Entrada");

        javax.swing.GroupLayout jpEntradaLayout = new javax.swing.GroupLayout(jpEntrada);
        jpEntrada.setLayout(jpEntradaLayout);
        jpEntradaLayout.setHorizontalGroup(
            jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEntradaLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
            .addGroup(jpEntradaLayout.createSequentialGroup()
                .addGroup(jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpEntradaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txValorEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEntradaLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel17)
                                .addGroup(jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(txSkuEntrada)
                                    .addComponent(txDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txQtdEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbRegEntrada)
                .addGap(65, 65, 65))
        );
        jpEntradaLayout.setVerticalGroup(
            jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEntradaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jbRegEntrada))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txSkuEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txQtdEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txValorEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(174, Short.MAX_VALUE))
        );

        tab.addTab("", jpEntrada);

        jLabel10.setText("SKU");

        jLabel11.setText("Data de Saida");

        jLabel12.setText("Quantidade a ser Removida");

        txQtdSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txQtdSaidaActionPerformed(evt);
            }
        });

        tabRemocao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Data", "SKU do produto", "Quantidade removida"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tabRemocao);

        jbRegSaida.setText("Registrar");
        jbRegSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegSaidaActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("Registar Saida");

        javax.swing.GroupLayout jpSaidaLayout = new javax.swing.GroupLayout(jpSaida);
        jpSaida.setLayout(jpSaidaLayout);
        jpSaidaLayout.setHorizontalGroup(
            jpSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSaidaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jpSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpSaidaLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jpSaidaLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(jpSaidaLayout.createSequentialGroup()
                        .addGroup(jpSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12)
                            .addComponent(txDataSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(jLabel10)
                            .addComponent(txSkuSaida)
                            .addComponent(jLabel11)
                            .addComponent(txQtdSaida))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbRegSaida)
                        .addGap(36, 36, 36))))
        );
        jpSaidaLayout.setVerticalGroup(
            jpSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSaidaLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpSaidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jbRegSaida))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txSkuSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txDataSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txQtdSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(225, Short.MAX_VALUE))
        );

        tab.addTab("", jpSaida);

        jLabel13.setText("Data - começo:");

        jLabel14.setText("Data - Fim");

        jLabel15.setText("Saldo Total");

        txSaldoTotal.setEditable(false);
        txSaldoTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txSaldoTotalActionPerformed(evt);
            }
        });

        jbChecarSaldo.setText("Ver Saldo");
        jbChecarSaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbChecarSaldoActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Consultar Saldo");

        javax.swing.GroupLayout jpSaldoLayout = new javax.swing.GroupLayout(jpSaldo);
        jpSaldo.setLayout(jpSaldoLayout);
        jpSaldoLayout.setHorizontalGroup(
            jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSaldoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel16)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jpSaldoLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpSaldoLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addGap(133, 133, 133))
                    .addGroup(jpSaldoLayout.createSequentialGroup()
                        .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txSaldoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                            .addComponent(txDataInicial))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpSaldoLayout.createSequentialGroup()
                                .addComponent(txDataFim, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpSaldoLayout.createSequentialGroup()
                                .addComponent(jbChecarSaldo)
                                .addGap(86, 86, 86))))
                    .addGroup(jpSaldoLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jpSaldoLayout.setVerticalGroup(
            jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSaldoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txDataFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txSaldoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbChecarSaldo))
                .addContainerGap(524, Short.MAX_VALUE))
        );

        tab.addTab("", jpSaldo);

        tabMovimentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Data", "Tipo", "SKU", "Estoque Movido", "Saldo"
            }
        ));
        jScrollPane4.setViewportView(tabMovimentos);

        javax.swing.GroupLayout jpMovimentosLayout = new javax.swing.GroupLayout(jpMovimentos);
        jpMovimentos.setLayout(jpMovimentosLayout);
        jpMovimentosLayout.setHorizontalGroup(
            jpMovimentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMovimentosLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpMovimentosLayout.setVerticalGroup(
            jpMovimentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMovimentosLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(276, Short.MAX_VALUE))
        );

        tab.addTab("", jpMovimentos);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        bntProduto.setText("Produto");
        bntProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntProdutoActionPerformed(evt);
            }
        });

        btnExit.setText("Registrar Saida");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnEntry.setText("Registrar Entrada");
        btnEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntryActionPerformed(evt);
            }
        });

        btnSaldo.setText("Consultar Saldo");
        btnSaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaldoActionPerformed(evt);
            }
        });

        btnLista.setText("Listar Movimentos");
        btnLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bntProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaldo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(btnLista, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnEntry, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bntProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(btnSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(btnLista, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(89, 89, 89)
                    .addComponent(btnEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(517, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        tab.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txSKUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txSKUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txSKUActionPerformed

    private void txPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txPrecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txPrecoActionPerformed

    private void txQtdSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txQtdSaidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txQtdSaidaActionPerformed

    private void txSaldoTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txSaldoTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txSaldoTotalActionPerformed

    private void bntProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntProdutoActionPerformed
        tab.setSelectedIndex(0);
    }//GEN-LAST:event_bntProdutoActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        tab.setSelectedIndex(2);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntryActionPerformed
        tab.setSelectedIndex(1);
    }//GEN-LAST:event_btnEntryActionPerformed

    private void btnSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaldoActionPerformed
        tab.setSelectedIndex(3);
    }//GEN-LAST:event_btnSaldoActionPerformed

    private void btnListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListaActionPerformed
        tab.setSelectedIndex(4);
    }//GEN-LAST:event_btnListaActionPerformed

    private void jbSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSalvarActionPerformed
           
    try {
        int sku = Integer.parseInt(txSKU.getText());
        String nome = txNome.getText();
        String cat = jcCategoria.getSelectedItem().toString();
        double preco = Double.parseDouble(txPreco.getText());

        Produto p = new Produto(sku, nome, cat, preco, 0);
        fm.addProduto(p);

        atualizarTabelaProdutos();

    } catch (Exception e) {
        System.out.println("Erro ao salvar produto: " + e.getMessage());
    }



    }//GEN-LAST:event_jbSalvarActionPerformed

    private void jbRegEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegEntradaActionPerformed
                                         
        try {
        // --- Ler campos ---
            String data = txDataEntrada.getText().trim();
            String skuTxt = txSkuEntrada.getText().trim();
            String qtdTxt = txQtdEntrada.getText().trim();
            String valorTxt = txValorEntrada.getText().trim();

        // --- Validar campos vazios ---
            if (data.isEmpty() || skuTxt.isEmpty() || qtdTxt.isEmpty() || valorTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                   "Todos os campos devem ser preenchidos!",
                   "Erro",
                  JOptionPane.ERROR_MESSAGE);
                  return;
            }

        // --- Validar números ---
            int sku, qtd;
            double valor;
            try {
                sku = Integer.parseInt(skuTxt);
                qtd = Integer.parseInt(qtdTxt);
                valor = Double.parseDouble(valorTxt);
            } catch (NumberFormatException ex) {
               JOptionPane.showMessageDialog(this, 
                    "SKU, quantidade e valor devem ser numéricos!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (qtd <= 0) {
                    JOptionPane.showMessageDialog(this, 
                    "A quantidade deve ser maior que zero!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (valor <= 0) {
                JOptionPane.showMessageDialog(this,
                    "O valor unitário deve ser maior que zero!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

        // --- Validar SKU ---
            Produto p = fm.getProdutoBySku(sku);
            if (p == null) {
                JOptionPane.showMessageDialog(this,
                    "SKU não encontrado no cadastro de produtos!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

        // --- Registrar entrada ---
            Entrada ent = new Entrada(data, sku, qtd, valor);
            fm.addEntrada(ent);

        // --- Atualizar tabelas ---
            atualizarTabelaProdutos();
            atualizarTabelaEntradas();
            atualizarTabelaSaidas();
            atualizarTabelaMovimentos();


        // --- Confirmação ---
            JOptionPane.showMessageDialog(this,
                "Entrada registrada com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro inesperado ao registrar entrada!",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jbRegEntradaActionPerformed

    private void jbRegSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegSaidaActionPerformed
        try {
        // Apenas pega a data da SAÍDA
            String data = txDataSaida.getText().trim();
            int sku = Integer.parseInt(txSkuSaida.getText());
            int qtd = Integer.parseInt(txQtdSaida.getText());

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data de saída não pode estar vazia!");
                return;
            }

        // Valida produto
            Produto p = fm.getProdutoBySku(sku);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "SKU não encontrado!");
                return;
           }

            if (p.getQuantidade() < qtd) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente!");
                return;
            }

        // Registrar saída
            Saida s = new Saida(data, sku, qtd);
            fm.addSaida(s); // salva no CSV + atualiza estoque automaticamente

        // Atualizar tabelas
            atualizarTabelaProdutos();
            atualizarTabelaEntradas();
            atualizarTabelaSaidas();
            atualizarTabelaMovimentos();

            JOptionPane.showMessageDialog(this, "Saída registrada com sucesso!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar saída!");
            e.printStackTrace();
        }



    }//GEN-LAST:event_jbRegSaidaActionPerformed

    private void jbChecarSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbChecarSaldoActionPerformed
                                                  
    try {
        String inicioStr = txDataInicial.getText().trim();  // <-- CORRIGIDO
        String fimStr = txDataFim.getText().trim();

        System.out.println("DEBUG inicio=" + inicioStr);
        System.out.println("DEBUG fim=" + fimStr);

        LocalDate inicio = parseDataFlex(inicioStr);
        LocalDate fim = parseDataFlex(fimStr);

        double saldo = 0;

        // ENTRADAS diminuem o saldo
        for (Entrada e : fm.getEntradas()) {
            LocalDate data = parseDataFlex(e.getData());
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                saldo -= e.getQuantidade() * e.getValorUnitario();
            }
        }

        // SAÍDAS aumentam o saldo
        for (Saida s : fm.getSaidas()) {
            LocalDate data = parseDataFlex(s.getData());
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                Produto p = fm.getProdutoBySku(s.getSku());
                if (p != null) {
                    saldo += s.getQuantidade() * p.getPrecoUnitario();
                }
            }
        }

        txSaldoTotal.setText(String.valueOf(saldo));

    } catch (Exception e) {
        txSaldoTotal.setText("ERRO");
        System.out.println("Erro ao calcular saldo: " + e.getMessage());
    }


    }//GEN-LAST:event_jbChecarSaldoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InventoryGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventoryGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventoryGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventoryGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventoryGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntProduto;
    private javax.swing.JButton btnEntry;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLista;
    private javax.swing.JButton btnSaldo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton jbChecarSaldo;
    private javax.swing.JButton jbExcluir;
    private javax.swing.JButton jbNovo;
    private javax.swing.JButton jbRegEntrada;
    private javax.swing.JButton jbRegSaida;
    private javax.swing.JButton jbSalvar;
    private javax.swing.JComboBox<String> jcCategoria;
    private javax.swing.JScrollPane jcTabela;
    private javax.swing.JPanel jpEntrada;
    private javax.swing.JPanel jpMovimentos;
    private javax.swing.JPanel jpProduto;
    private javax.swing.JPanel jpSaida;
    private javax.swing.JPanel jpSaldo;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JTable tabAdicao;
    private javax.swing.JTable tabMovimentos;
    private javax.swing.JTable tabProduto;
    private javax.swing.JTable tabRemocao;
    private javax.swing.JTextField txDataEntrada;
    private javax.swing.JTextField txDataFim;
    private javax.swing.JTextField txDataInicial;
    private javax.swing.JTextField txDataSaida;
    private javax.swing.JTextField txNome;
    private javax.swing.JTextField txPreco;
    private javax.swing.JTextField txQtdEntrada;
    private javax.swing.JTextField txQtdSaida;
    private javax.swing.JTextField txSKU;
    private javax.swing.JTextField txSaldoTotal;
    private javax.swing.JTextField txSkuEntrada;
    private javax.swing.JTextField txSkuSaida;
    private javax.swing.JTextField txValorEntrada;
    // End of variables declaration//GEN-END:variables
}
