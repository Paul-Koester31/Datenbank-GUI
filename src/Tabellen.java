import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class Tabellen extends JFrame implements TableModelListener {
    JScrollPane sc = null;
    JButton a = null;
    JButton einf = null;
    JButton del = null;
    static ResultSetMetaData rm = null;
    static DefaultTableModel t = new DefaultTableModel();
    static JTable table = new JTable(t);
    static ResultSet r = null;
    static String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";
    TableRowSorter sorter = null;

    JTextField s = null;
    String tab = null;

    JLabel such = null;

    Font font = new Font("Arial",Font.PLAIN,12);


    public Tabellen(String url, String ta) {
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        tab = ta;


        einfügen(tab);


        sc = new JScrollPane(table);
        sc.setBounds(50, 100, 400, 350);
        sc.getHorizontalScrollBar();
        this.add(sc);

        a = new JButton("Zurück");
        a.setBounds(50, 30, 80, 50);
        a.setForeground(Color.BLACK);
        a.setBackground(Color.white);
        a.setFont(font);
        a.addActionListener(e -> {
            this.dispose();
            Menu m = new Menu();
            m.setVisible(true);
        });
        this.add(a);

        einf = new JButton("Einfügen");
        einf.setBounds(50, 480, 100, 50);
        einf.setForeground(Color.BLACK);
        einf.setBackground(Color.white);
        einf.setFont(font);
        einf.addActionListener(e -> {
            Hinzufügen d = new Hinzufügen(r, tab);
            d.setVisible(true);
            this.setFocusable(false);

        });
        this.add(einf);


        del = new JButton("Löschen");
        del.setBounds(180, 480, 100, 50);
        del.setForeground(Color.BLACK);
        del.setBackground(Color.white);
        del.setFont(font);
        del.addActionListener(e -> {
            Löschen l = new Löschen(tab, t, table);
            table.clearSelection();

        });
        this.add(del);

        such = new JLabel("Suchen");
        such.setBounds(300, 10, 50, 20);
        this.add(such);

        s = new JTextField();
        s.setBounds(300, 30, 150, 40);
        this.add(s);
        suchen();

        t.addTableModelListener(this);

    }

    public static void einfügen(String tab) {
        String[][] temp = {{""}};


        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            r = s.executeQuery("Select * From " + tab);
            rm = r.getMetaData();
            rm.getColumnCount();
            String column[] = new String[rm.getColumnCount()];


            for (int i = 1; i <= rm.getColumnCount(); i++) {
                column[i - 1] = rm.getColumnName(i);
                t.addColumn(column[i - 1]);
            }
            t.setDataVector(temp, column);
            t.removeRow(0);
            Object rows[] = new Object[rm.getColumnCount()];
            while (r.next()) {
                for (int i = 1; i <= rm.getColumnCount(); i++) {
                    rows[i - 1] = r.getString(i);

                }
                t.addRow(rows);
                t.fireTableDataChanged();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void suchen() {
        sorter = new TableRowSorter(t);
        table.setRowSorter(sorter);
        s.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(s.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(s.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(s.getText());
            }

            public void search(String st) {
                if (st.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(st));
                }
            }
        });
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int zeile = table.getSelectedRow();
        int spalte = table.getSelectedColumn();
        System.out.println(zeile+" "+ spalte);
        if (spalte > 0 || zeile > 0) {
            int response = 0;
            try {
                response = JOptionPane.showConfirmDialog(null, "Wollen Sie dein Eintrag in Zeile " + (zeile + 1) + ", Spalte :" + (rm.getColumnName(spalte + 1)) + " ändern?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if (response == 0) {
                try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                    Statement s = conn.createStatement();
                    System.out.println(t.getColumnName(spalte) + "\n" + rm.getColumnTypeName(spalte + 1));
                    if (rm.getColumnTypeName(spalte + 1).equalsIgnoreCase("Varchar")) {
                        ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= '" + t.getValueAt(zeile, spalte).toString() + "' WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                    } else if (rm.getColumnTypeName(spalte + 1).equalsIgnoreCase("Date")) {
                        ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= '" + t.getValueAt(zeile, spalte).toString() + "' WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                    } else {
                        ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= " + t.getValueAt(zeile, spalte).toString() + " WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                    }
                    table.clearSelection();
                } catch (SQLException f) {
                    throw new RuntimeException(f);
                }
                t.fireTableDataChanged();
            } else {
                table.clearSelection();
            }

        }

    }

}

