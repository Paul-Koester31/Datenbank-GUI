import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Einzelansicht extends JFrame implements TableModelListener {

    JScrollPane sc = null;
    JButton a = null;
    JButton einf = null;
    JButton del = null;
    static JButton zur = null;
    static JButton vor = null;
    static ResultSetMetaData rm = null;
    static DefaultTableModel t = new DefaultTableModel();
    static JTable table = new JTable(t);
    static ResultSet r = null;
    static String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";

    JTextField s = null;
    String tab = null;

    JLabel such = null;

    Font font = new Font("Arial", Font.PLAIN, 12);
    String sql = null;
    String sq = "";
    static int i = 0;

    public Einzelansicht(String ta) {
        this.setSize(500, 400);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        tab = ta;
        this.setTitle(tab);


        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            r = s.executeQuery("Select * FROM " + tab);
            rm = r.getMetaData();
            sql = "Select * From " + tab + " Where " + rm.getColumnName(1) + " =" + 1;
            einfügen(tab, sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        vor = new JButton("Weiter");
        vor.setBounds(350, 150, 90, 50);
        vor.setFont(font);
        vor.setBackground(Color.white);
        vor.setForeground(Color.black);
        vor.addActionListener(e -> {
            i++;
            try {
                sql = "Select * From " + tab + " Where " + rm.getColumnName(1) + " =" + i;
                einfügen(tab, sql);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(vor);

        zur = new JButton("Letzte");
        zur.setBounds(80, 150, 90, 50);
        zur.setFont(font);
        zur.setBackground(Color.white);
        zur.setForeground(Color.black);
        zur.addActionListener(e -> {
            if (i > 1) {
                i--;
            }
            try {
                sql = "Select * From " + tab + " Where " + rm.getColumnName(1) + " =" + i;
                einfügen(tab, sql);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(zur);


        sc = new JScrollPane(Einzelansicht.table);
        sc.setBounds(50, 100, 400, 50);
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
        einf.setBounds(150, 280, 100, 50);
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
        del.setBounds(300, 280, 100, 50);
        del.setForeground(Color.BLACK);
        del.setBackground(Color.white);
        del.setFont(font);
        del.addActionListener(e -> {
            System.out.println(t.getValueAt(0, 0));
            String wh = t.getValueAt(0, 0).toString();
            int response = JOptionPane.showConfirmDialog(null, "Wollen Sie den Eintrag löschen?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == 0) {
                try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                    Statement s = conn.createStatement();
                    ResultSet r = s.executeQuery("DELETE  From " + tab + " WHERE " + t.getColumnName(0) + "=" + wh);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                Tabellen.einfügen(tab);
            } else {

            }
            Einzelansicht.table.clearSelection();
            table.clearSelection();
            t.removeRow(0);
            t.fireTableDataChanged();

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

    public static void einfügen(String tab, String sql) {
        String[][] temp = {{""}};


        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            r = s.executeQuery(sql);
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
            }


            t.addRow(rows);
            t.fireTableDataChanged();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void suchen() {
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
                } else {
                    try {
                        for (int j = 1; j <= rm.getColumnCount(); j++) {
                            if (j == rm.getColumnCount()) {
                                sq = sq + rm.getColumnName(j) + " LIKE '%" + st + "%'";
                            } else {
                                sq = sq + rm.getColumnName(j) + " LIKE '%" + st + "%' or ";
                            }
                        }
                        sql = "SELECT * FROM " + tab + " WHERE " + sq;
                        System.out.println(sql);
                        einfügen(tab, sql);
                        sq = "";
                        sql = "";

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int zeile = table.getSelectedRow();
        int spalte = table.getSelectedColumn();
        if (spalte > 0) {
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
