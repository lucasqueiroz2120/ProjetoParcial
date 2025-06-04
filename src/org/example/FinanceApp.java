package org.example;

import org.example.model.Category;
import org.example.model.Transaction;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FinanceApp {

    private static List<User> users = new ArrayList<>();
    private User currentUser;

    private JFrame loginFrame;
    private JFrame mainFrame;

    private DefaultTableModel transactionTableModel;
    private DefaultTableModel categoryTableModel;

    private JLabel lblBalance, lblIncome, lblExpense;
    private JTable transactionTable;

    private JComboBox<Category> transactionCategoryCombo;

    // Filtros
    private JTextField txtFilterStartDate;
    private JTextField txtFilterEndDate;
    private JComboBox<Category> filterCategoryCombo;

    public FinanceApp() {
        users.add(new User("usuario", "usuario123"));
        createLoginFrame();
    }

    private void createLoginFrame() {
        loginFrame = new JFrame("Login - Gestão Financeira");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Entre ou Registre-se", SwingConstants.CENTER);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(4, 1));
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        center.add(new JLabel("Usuário:"));
        center.add(txtUser);
        center.add(new JLabel("Senha:"));
        center.add(txtPass);
        panel.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Registrar");
        buttons.add(btnLogin);
        buttons.add(btnRegister);
        panel.add(buttons, BorderLayout.SOUTH);

        loginFrame.add(panel);
        loginFrame.setVisible(true);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            for (User u : users) {
                if (u.getUsername().equals(user) && u.getPassword().equals(pass)) {
                    currentUser = u;
                    loginFrame.dispose();
                    createMainFrame();
                    return;
                }
            }
            JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha inválidos!");
        });

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Informe usuário e senha.");
                return;
            }
            for (User u : users) {
                if (u.getUsername().equals(user)) {
                    JOptionPane.showMessageDialog(loginFrame, "Usuário já existe!");
                    return;
                }
            }
            users.add(new User(user, pass));
            JOptionPane.showMessageDialog(loginFrame, "Registrado com sucesso!");
        });
    }

    private void createMainFrame() {
        mainFrame = new JFrame("FinanceApp - " + currentUser.getUsername());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 700);
        mainFrame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Transações", createTransactionsPanel());
        tabs.add("Resumo", createSummaryPanel());
        tabs.add("Categorias", createCategoriesPanel());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            currentUser = null;
            mainFrame.dispose();
            createLoginFrame();
        });

        mainFrame.add(btnLogout, BorderLayout.NORTH);
        mainFrame.add(tabs, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        transactionTableModel = new DefaultTableModel(new Object[]{"Tipo", "Valor", "Categoria", "Data", "Descrição"}, 0);
        transactionTable = new JTable(transactionTableModel);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        JPanel form = new JPanel();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Receita", "Despesa"});
        JTextField txtValue = new JTextField(6);
        transactionCategoryCombo = new JComboBox<>();
        updateCategoryCombo(transactionCategoryCombo);
        JTextField txtDate = new JTextField(10);
        txtDate.setText(LocalDate.now().toString());
        JTextField txtDesc = new JTextField(10);
        JButton btnAdd = new JButton("Adicionar");
        JButton btnEdit = new JButton("Editar");
        JButton btnRemove = new JButton("Remover");

        form.add(new JLabel("Tipo:"));
        form.add(cbType);
        form.add(new JLabel("Valor:"));
        form.add(txtValue);
        form.add(new JLabel("Categoria:"));
        form.add(transactionCategoryCombo);
        form.add(new JLabel("Data:"));
        form.add(txtDate);
        form.add(new JLabel("Descrição:"));
        form.add(txtDesc);
        form.add(btnAdd);
        form.add(btnEdit);
        form.add(btnRemove);

        panel.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            try {
                Transaction t = new Transaction(
                        Double.parseDouble(txtValue.getText()),
                        (Category) transactionCategoryCombo.getSelectedItem(),
                        LocalDate.parse(txtDate.getText()),
                        txtDesc.getText(),
                        (String) cbType.getSelectedItem()
                );
                currentUser.getTransactions().add(t);
                transactionTableModel.addRow(new Object[]{
                        t.getType(), t.getAmount(), t.getCategory().getName(), t.getDate(), t.getDescription()
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row != -1) {
                Transaction t = currentUser.getTransactions().get(row);
                try {
                    t.setType((String) cbType.getSelectedItem());
                    t.setAmount(Double.parseDouble(txtValue.getText()));
                    t.setCategory((Category) transactionCategoryCombo.getSelectedItem());
                    t.setDate(LocalDate.parse(txtDate.getText()));
                    t.setDescription(txtDesc.getText());

                    transactionTableModel.setValueAt(t.getType(), row, 0);
                    transactionTableModel.setValueAt(t.getAmount(), row, 1);
                    transactionTableModel.setValueAt(t.getCategory().getName(), row, 2);
                    transactionTableModel.setValueAt(t.getDate(), row, 3);
                    transactionTableModel.setValueAt(t.getDescription(), row, 4);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Erro: " + ex.getMessage());
                }
            }
        });

        btnRemove.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row != -1) {
                currentUser.getTransactions().remove(row);
                transactionTableModel.removeRow(row);
            }
        });

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();

        lblIncome = new JLabel("Receitas: 0.0");
        lblExpense = new JLabel("Despesas: 0.0");
        lblBalance = new JLabel("Saldo: 0.0");

        topPanel.add(lblIncome);
        topPanel.add(lblExpense);
        topPanel.add(lblBalance);

        JPanel filterPanel = new JPanel();
        txtFilterStartDate = new JTextField(8);
        txtFilterEndDate = new JTextField(8);
        filterCategoryCombo = new JComboBox<>();
        updateCategoryCombo(filterCategoryCombo);

        JButton btnFilter = new JButton("Filtrar");

        filterPanel.add(new JLabel("Data Início:"));
        filterPanel.add(txtFilterStartDate);
        filterPanel.add(new JLabel("Data Fim:"));
        filterPanel.add(txtFilterEndDate);
        filterPanel.add(new JLabel("Categoria:"));
        filterPanel.add(filterCategoryCombo);
        filterPanel.add(btnFilter);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.CENTER);

        btnFilter.addActionListener(e -> updateSummary());

        return panel;
    }

    private JPanel createCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        categoryTableModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0);
        JTable table = new JTable(categoryTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel();
        JTextField txtCatName = new JTextField(10);
        JButton btnAdd = new JButton("Adicionar");
        JButton btnEdit = new JButton("Editar");
        JButton btnRemove = new JButton("Remover");

        form.add(new JLabel("Nome:"));
        form.add(txtCatName);
        form.add(btnAdd);
        form.add(btnEdit);
        form.add(btnRemove);

        panel.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            Category cat = new Category(txtCatName.getText());
            currentUser.getCategories().add(cat);
            categoryTableModel.addRow(new Object[]{cat.getId(), cat.getName()});
            updateCategoryCombo(transactionCategoryCombo);
            updateCategoryCombo(filterCategoryCombo);
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Category cat = currentUser.getCategories().get(row);
                cat.setName(txtCatName.getText());
                categoryTableModel.setValueAt(cat.getName(), row, 1);
                updateCategoryCombo(transactionCategoryCombo);
                updateCategoryCombo(filterCategoryCombo);
            }
        });

        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                currentUser.getCategories().remove(row);
                categoryTableModel.removeRow(row);
                updateCategoryCombo(transactionCategoryCombo);
                updateCategoryCombo(filterCategoryCombo);
            }
        });

        return panel;
    }

    private void updateCategoryCombo(JComboBox<Category> combo) {
        if (combo != null) {
            combo.removeAllItems();
            for (Category cat : currentUser.getCategories()) {
                combo.addItem(cat);
            }
        }
    }

    private void updateSummary() {
        LocalDate start = txtFilterStartDate.getText().isEmpty() ? LocalDate.MIN : LocalDate.parse(txtFilterStartDate.getText());
        LocalDate end = txtFilterEndDate.getText().isEmpty() ? LocalDate.MAX : LocalDate.parse(txtFilterEndDate.getText());
        Category selectedCategory = (Category) filterCategoryCombo.getSelectedItem();

        double income = 0, expense = 0;

        List<Transaction> filtered = currentUser.getTransactions().stream()
                .filter(t -> (t.getDate().isEqual(start) || t.getDate().isAfter(start)) &&
                        (t.getDate().isEqual(end) || t.getDate().isBefore(end)) &&
                        (selectedCategory == null || t.getCategory().equals(selectedCategory)))
                .collect(Collectors.toList());

        for (Transaction t : filtered) {
            if (t.getType().equals("Receita")) income += t.getAmount();
            else expense += t.getAmount();
        }

        lblIncome.setText("Receitas: " + income);
        lblExpense.setText("Despesas: " + expense);
        lblBalance.setText("Saldo: " + (income - expense));
    }
}
