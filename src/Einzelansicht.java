import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;

public class Einzelansicht extends JFrame implements TableModelListener, ActionListener {

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
    JMenuBar bar = null;
    JMenu dat = null;
    JMenu bew = null;
    JMenu adm = null;
    JMenuItem i1, i2, i3, i4, i5, i6, i7;

    static JTable detail;
    static JScrollPane dsc;
     int vid = 1;


    public Einzelansicht(String ta) {
        this.setSize(600, 800);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        tab = ta;
        this.setTitle(tab);

        bar = new JMenuBar();
        dat = new JMenu("Datei");
        bew = new JMenu("Bewegen");
        adm = new JMenu("Administration");
        i1 = new JMenuItem("Programm schließen");
        i1.addActionListener(e -> {
            this.dispose();
            Menü m = new Menü();
            m.setVisible(true);
        });
        i1.setActionCommand("close");

        i2 = new JMenuItem("Nächster Datensatz");
        i2.addActionListener(this);
        i2.setActionCommand("vor");

        i3 = new JMenuItem("Vorheriger Datensatz");
        i3.addActionListener(this);
        i3.setActionCommand("zur");

        i4 = new JMenuItem("Suchen");
        i4.addActionListener(e -> {
            s.setText(JOptionPane.showInputDialog("Wonach wollen Sie suchen?"));
        });

        i5 = new JMenuItem("Einfügen");
        i5.addActionListener(this);
        i5.setActionCommand("einf");

        i6 = new JMenuItem("Ändern");
        i6.addActionListener(this);
        i6.setActionCommand("änd");

        i7 = new JMenuItem("Löschen");
        i7.addActionListener(this);
        i7.setActionCommand("lösch");

        dat.add(i1);
        bew.add(i2);
        bew.add(i3);
        adm.add(i4);
        adm.add(i5);
        adm.add(i6);
        adm.add(i7);
        bar.add(dat);
        bar.add(bew);
        bar.add(adm);
        this.setJMenuBar(bar);


        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            r = s.executeQuery("Select * FROM " + tab);
            rm = r.getMetaData();
            sql = "Select * From " + tab;
            r.first();
            einfügen(r);
            detailtab(vid);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        vor = new JButton("Weiter");
        vor.setBounds(350, 150, 90, 50);
        vor.setFont(font);
        vor.setBackground(Color.white);
        vor.setForeground(Color.black);
        vor.addActionListener(this);
        vor.setActionCommand("vor");
        this.add(vor);

        zur = new JButton("Letzte");
        zur.setBounds(80, 150, 90, 50);
        zur.setFont(font);
        zur.setBackground(Color.white);
        zur.setForeground(Color.black);
        zur.addActionListener(this);
        zur.setActionCommand("zur");
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
        a.addActionListener(this);
        a.setActionCommand("close");
        this.add(a);

        einf = new JButton("Einfügen");
        einf.setBounds(470, 50, 100, 50);
        einf.setForeground(Color.BLACK);
        einf.setBackground(Color.white);
        einf.setFont(font);
        einf.addActionListener(this);
        einf.setActionCommand("einf");
        this.add(einf);


        del = new JButton("Löschen");
        del.setBounds(470, 120, 100, 50);
        del.setForeground(Color.BLACK);
        del.setBackground(Color.white);
        del.setFont(font);
        del.addActionListener(this);
        del.setActionCommand("lösch");
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

    public static void einfügen(ResultSet r) {
        String[][] temp = {{""}};

        try {
            rm = r.getMetaData();
            String column[] = new String[rm.getColumnCount()];

            for (int i = 1; i <= rm.getColumnCount(); i++) {
                column[i - 1] = rm.getColumnName(i);
                t.addColumn(column[i - 1]);
            }
            t.setDataVector(temp, column);
            t.removeRow(0);

            Object rows[] = new Object[rm.getColumnCount()];
            for (int i = 1; i <= rm.getColumnCount(); i++) {
                rows[i - 1] = r.getString(i);
            }
            t.addRow(rows);
            t.fireTableDataChanged();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void suchen() {

        s.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER){
                    try {
                        r = Suchen.search(s.getText(),r);
                        s.setText("");
                        einfügen(r);
                        vid = r.getInt("V_ID");
                        detailtab(vid);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

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

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "vor":
                try {
                    if (r.next()) {
                        einfügen(r);
                        vid = r.getInt("V_ID");
                        detailtab(vid);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "zur":
                try {
                    if (r.previous()) {
                        einfügen(r);
                        vid = r.getInt("V_ID");
                        detailtab(vid);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "einf":
                Hinzufügen d = new Hinzufügen(r, tab, this, true);
                d.setVisible(true);

                break;
            case "änd":
                break;
            case "lösch":
                System.out.println(t.getValueAt(0, 0));
                String wh = t.getValueAt(0, 0).toString();
                int response = JOptionPane.showConfirmDialog(null, "Wollen Sie den Eintrag löschen?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == 0) {
                    try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                        Statement s = conn.createStatement();
                        ResultSet r = s.executeQuery("DELETE  From " + tab + " WHERE " + t.getColumnName(0) + "=" + wh);
                        ResultSet r2 = s.executeQuery("Select * from " + tab);
                        r2.previous();
                        einfügen(r2);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                } else {

                }
                table.clearSelection();
                t.removeRow(0);
                t.fireTableDataChanged();
                break;
            case "close":
                this.dispose();
                Menü m = new Menü();
                m.setVisible(true);
                break;

        }

    }

    public void detailtab(int vid) {

        detail = new JTable(Datail.detailmodel(vid, url));
        detail.setAutoCreateRowSorter(true);
        dsc = new JScrollPane(detail);
        dsc.setBounds(20, 250, 550, 400);
        dsc.getHorizontalScrollBar();
        this.add(dsc);

    }
}
