package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class main {

    // Classe representando um usuário
    static class User {
        String username;
        String password;
        ArrayList<Transaction> transactions;
        ArrayList<Category> categories;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.transactions = new ArrayList<>();
            this.categories = new ArrayList<>();
            categories.add(new Category("Geral")); // Categoria padrão
        }
    }

    // Classe representando uma categoria
    static class Category {
        private static int counter = 1;
        int id;
        String name;

        public Category(String name) {
            this.name = name;
            this.id = counter++;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Classe representando uma transação
    static class Transaction {
        double amount;
        Category category;
        LocalDate date;
        String description;
        String type; // Receita ou Despesa

        public Transaction(double amount, Category category, LocalDate date, String description, String type) {
            this.amount = amount;
            this.category = category;
            this.date = date;
            this.description = description;
            this.type = type;
        }
    }

    // Dados e componentes
    private static ArrayList<User> users = new ArrayList<>();
    private User currentUser;

    private JFrame loginFrame;
    private JFrame mainFrame;
    private DefaultTableModel transactionTableModel;
    private DefaultTableModel categoryTableModel;
    private JLabel lblBalance, lblIncome, lblExpense;

    private JComboBox<Category> transactionCategoryCombo;
    private JTable transactionTable;

    // Construtor
    public main() {
        createLoginFrame();
    }

    // Login
    private void createLoginFrame() {
        loginFrame = new JFrame("Login - Gestão Financeira");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Entre ou Registre-se", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Painel central com grid para centralizar os campos
        JPanel centerPanel = new JPanel(new GridLayout(4, 1));
        JLabel lblUser = new JLabel("Usuário:");
        JTextField txtUser = new JTextField();
        JLabel lblPass = new JLabel("Senha:");
        JPasswordField txtPass = new JPasswordField();

        centerPanel.add(lblUser);
        centerPanel.add(txtUser);
        centerPanel.add(lblPass);
        centerPanel.add(txtPass);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Registrar");
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);

        panel.add(btnPanel, BorderLayout.SOUTH);

        loginFrame.add(panel);
        loginFrame.setVisible(true);

        // Ações dos botões
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            User u = authenticate(user, pass);
            if (u != null) {
                currentUser = u;
                loginFrame.dispose();
                createMainFrame();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha inválidos!");
            }
        });

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Informe usuário e senha para registrar.");
                return;
            }
            if (authenticate(user, pass) != null) {
                JOptionPane.showMessageDialog(loginFrame, "Usuário já existe!");
                return;
            }
            User newUser = new User(user, pass);
            users.add(newUser);
            JOptionPane.showMessageDialog(loginFrame, "Registrado com sucesso! Faça login.");
        });
    }

    // Autenticação
    private User authenticate(String username, String password) {
        for (User u : users) {
            if (u.username.equals(username) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Tela principal
    private void createMainFrame() {
        mainFrame = new JFrame("Gestão Financeira - " + currentUser.username);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 800);
        mainFrame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Transações", createTransactionsPanel());
        tabbedPane.add("Resumo", createSummaryPanel());
        tabbedPane.add("Categorias", createCategoriesPanel());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            currentUser = null;
            mainFrame.dispose();
            createLoginFrame();
        });

        mainFrame.add(btnLogout, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    // Aba Transações
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        transactionTableModel = new DefaultTableModel(new Object[]{"Tipo", "Valor", "Categoria", "Data", "Descrição"}, 0);
        transactionTable = new JTable(transactionTableModel);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Receita", "Despesa"});
        JTextField txtValue = new JTextField(6);
        transactionCategoryCombo = new JComboBox<>();
        updateCategoryCombo(transactionCategoryCombo);
        JTextField txtDate = new JTextField(10);
        txtDate.setText(LocalDate.now().toString());
        JTextField txtDesc = new JTextField(10);
        JButton btnAdd = new JButton("Adicionar");
        JButton btnRemove = new JButton("Remover");

        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(cbType);
        formPanel.add(new JLabel("Valor:"));
        formPanel.add(txtValue);
        formPanel.add(new JLabel("Categoria:"));
        formPanel.add(transactionCategoryCombo);
        formPanel.add(new JLabel("Data:"));
        formPanel.add(txtDate);
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(txtDesc);
        formPanel.add(btnAdd);
        formPanel.add(btnRemove);

        panel.add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            try {
                String type = (String) cbType.getSelectedItem();
                double value = Double.parseDouble(txtValue.getText());
                Category cat = (Category) transactionCategoryCombo.getSelectedItem();
                LocalDate date = LocalDate.parse(txtDate.getText());
                String desc = txtDesc.getText();

                Transaction t = new Transaction(value, cat, date, desc, type);
                currentUser.transactions.add(t);

                transactionTableModel.addRow(new Object[]{type, value, cat.name, date.toString(), desc});
                updateSummary();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao adicionar transação: " + ex.getMessage());
            }
        });

        btnRemove.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row != -1) {
                String desc = (String) transactionTableModel.getValueAt(row, 4);
                LocalDate date = LocalDate.parse((String) transactionTableModel.getValueAt(row, 3));

                currentUser.transactions.removeIf(t -> t.description.equals(desc) && t.date.equals(date));
                transactionTableModel.removeRow(row);
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione uma transação para remover.");
            }
        });

        return panel;
    }

    // Aba Resumo
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new FlowLayout());
        lblIncome = new JLabel("Receitas: 0.0");
        lblExpense = new JLabel("Despesas: 0.0");
        lblBalance = new JLabel("Saldo: 0.0");
        infoPanel.add(lblIncome);
        infoPanel.add(lblExpense);
        infoPanel.add(lblBalance);

        JButton btnRefresh = new JButton("Atualizar");
        btnRefresh.addActionListener(e -> updateSummary());

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    // Aba Categorias
    private JPanel createCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        categoryTableModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0);
        JTable table = new JTable(categoryTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout());
        JTextField txtCatName = new JTextField(10);
        JButton btnAdd = new JButton("Adicionar");
        JButton btnRemove = new JButton("Remover");

        form.add(new JLabel("Nome:"));
        form.add(txtCatName);
        form.add(btnAdd);
        form.add(btnRemove);

        panel.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            String name = txtCatName.getText().trim();
            if (!name.isEmpty()) {
                Category cat = new Category(name);
                currentUser.categories.add(cat);
                categoryTableModel.addRow(new Object[]{cat.id, cat.name});
                txtCatName.setText("");

                if (transactionCategoryCombo != null) {
                    updateCategoryCombo(transactionCategoryCombo);
                }
            }
        });

        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) categoryTableModel.getValueAt(row, 0);
                currentUser.categories.removeIf(c -> c.id == id);
                categoryTableModel.removeRow(row);
                updateCategoryCombo(transactionCategoryCombo);
            }
        });

        return panel;
    }

    // Atualiza ComboBox de categorias
    private void updateCategoryCombo(JComboBox<Category> combo) {
        combo.removeAllItems();
        for (Category cat : currentUser.categories) {
            combo.addItem(cat);
        }
    }

    // Atualiza totais
    private void updateSummary() {
        double income = 0.0;
        double expense = 0.0;
        for (Transaction t : currentUser.transactions) {
            if ("Receita".equals(t.type))
                income += t.amount;
            else
                expense += t.amount;
        }
        lblIncome.setText("Receitas: " + income);
        lblExpense.setText("Despesas: " + expense);
        lblBalance.setText("Saldo: " + (income - expense));
    }

    public static void main(String[] args) {
        users.add(new User("usuario", "usuario123"));
        SwingUtilities.invokeLater(main::new);
    }
}
