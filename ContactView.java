package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.Timer;

public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactView(ContactController controller) {
        super("Agenda Rosa Moderna - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Font baseFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Panel principal con fondo rosa suave
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(255, 240, 245));
        setContentPane(contentPanel);

        // Mensaje de bienvenida
        JLabel welcomeLabel = new JLabel("Bienvenido a la Agenda Rosa Moderna", SwingConstants.CENTER);
        welcomeLabel.setFont(baseFont.deriveFont(Font.BOLD, 26f));
        welcomeLabel.setForeground(new Color(255, 20, 147));
        welcomeLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(35);
        table.setForeground(new Color(50, 50, 50));
        table.setBackground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(255, 182, 193));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(baseFont.deriveFont(Font.BOLD, 18f));
        table.getTableHeader().setBackground(new Color(255, 105, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        // Centramos columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        scrollPane.setBackground(new Color(255, 240, 245));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonsPanel.setBackground(new Color(255, 240, 245));

        JButton addBtn = new JButton("Agregar");
        styleButton(addBtn, new Color(255, 105, 180), new Color(156, 86, 98));

        JButton deleteBtn = new JButton("Eliminar");
        styleButton(deleteBtn, new Color(255, 20, 147), new Color(255, 105, 180));

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Acciones botones
        addBtn.addActionListener(e -> showAddContactDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContact());
    }

    private void styleButton(JButton button, Color baseColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setPreferredSize(new Dimension(160, 45));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
        });
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
            showToast("Contacto agregado con éxito", new Color(76, 175, 80));
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showToast("Seleccione un contacto para eliminar", new Color(244, 67, 54));
            return;
        }
        String id = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            showToast("Contacto eliminado con éxito", new Color(244, 67, 54));
        }
    }

    private void showToast(String message, Color bgColor) {
        JWindow toast = new JWindow();
        toast.setBackground(new Color(0,0,0,0));

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(bgColor);
                g.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10,20,10,20));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label);

        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - toast.getWidth() - 50;
        int y = screenSize.height - toast.getHeight() - 50;
        toast.setLocation(x, y);
        toast.setAlwaysOnTop(true);
        toast.setVisible(true);

        new Timer(2500, e -> toast.dispose()).start();
    }
}
