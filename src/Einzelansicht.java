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

public class Einzelansicht extends JFrame implements ActionListener {

    JScrollPane sc = null;
    JButton a = null;
    JButton einf = null;
    JButton del = null;
    JButton zur = null;
    JButton vor = null;
    JButton ch = null;
    JButton sz = null;
    static ResultSetMetaData rm = null;
    static DefaultTableModel t = new DefaultTableModel();
    static JTable table = new JTable(t);
    static ResultSet r = null;
    static String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";
    JTextField s = null;
    static String tab = null;
    JLabel such = null;
    Font font = new Font("Arial", Font.PLAIN, 12);
    static String sql = null;
    JMenuBar bar = null;
    JMenu dat = null;
    JMenu bew = null;
    JMenu adm = null;
    JMenuItem i1, i2, i3, i4, i5, i6, i7;
    static DefaultTableModel dtm = new DefaultTableModel();
    static JTable detail = new JTable(dtm);
    static JScrollPane dsc;
    static int vid = 1;


    public Einzelansicht(String ta) {
        //Frameeigenschaften werden deklariert
        this.setSize(600, 800);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        tab = ta;
        this.setTitle(tab);

        //Menüs für Menüleiste deklarieren
        bar = new JMenuBar();
        dat = new JMenu("Datei");
        bew = new JMenu("Bewegen");
        adm = new JMenu("Administration");

        //Menüitems mit Namen versehen und aAtionen hinterlegen
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
        i3.setActionCommand("rück");

        i4 = new JMenuItem("Suchen");
        i4.addActionListener(e -> {
            s.setText(JOptionPane.showInputDialog("Wonach wollen Sie suchen?"));
            r = Suchen.search(s.getText(), r);
            s.setText("");
            einfügen(r);
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

        //Menüitems an Menüs binden
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


        //Buttons
        vor = new JButton("Vorwärts");
        vor.setBounds(350, 150, 90, 50);
        vor.setFont(font);
        vor.setBackground(Color.white);
        vor.setForeground(Color.black);
        vor.addActionListener(this);
        vor.setActionCommand("vor");
        this.add(vor);

        zur = new JButton("Rückwärts");
        zur.setBounds(80, 150, 90, 50);
        zur.setFont(font);
        zur.setBackground(Color.white);
        zur.setForeground(Color.black);
        zur.addActionListener(this);
        zur.setActionCommand("rück");
        this.add(zur);

        a = new JButton("Zurück");
        a.setBounds(50, 30, 80, 30);
        a.setForeground(Color.BLACK);
        a.setBackground(Color.white);
        a.setFont(font);
        a.addActionListener(this);
        a.setActionCommand("close");
        this.add(a);

        einf = new JButton("Einfügen");
        einf.setBounds(470, 20, 80, 30);
        einf.setForeground(Color.BLACK);
        einf.setBackground(Color.white);
        einf.setFont(font);
        einf.addActionListener(this);
        einf.setActionCommand("einf");
        this.add(einf);

        ch = new JButton("Ändern");
        ch.setBounds(470, 80, 80, 30);
        ch.setFont(font);
        ch.setBackground(Color.white);
        ch.setForeground(Color.black);
        ch.addActionListener(this);
        ch.setActionCommand("änd");
        this.add(ch);


        del = new JButton("Löschen");
        del.setBounds(470, 140, 80, 30);
        del.setForeground(Color.BLACK);
        del.setBackground(Color.white);
        del.setFont(font);
        del.addActionListener(this);
        del.setActionCommand("lösch");
        this.add(del);

        sz = new JButton("Zurücksetzen");
        sz.setBounds(185, 10, 105, 30);
        sz.setForeground(Color.BLACK);
        sz.setBackground(Color.white);
        sz.setFont(font);
        sz.addActionListener(e -> {
            standart(1);
            this.remove(sz);
            this.repaint();
        });


        //Mastertabelle in Scrollbar
        table.setCellEditor(null);
        sc = new JScrollPane(Einzelansicht.table);
        sc.setBounds(50, 100, 400, 50);
        sc.getHorizontalScrollBar();
        this.add(sc);

        //Jlabel und JTextfield für die Suche
        such = new JLabel("Suchen");
        such.setBounds(300, 10, 50, 20);
        this.add(such);

        s = new JTextField();
        s.setBounds(300, 30, 150, 40);
        this.add(s);
        suchen();

        //Detailtabelle in Scrollpane
        detail.setAutoCreateRowSorter(true);
        dsc = new JScrollPane(detail);
        dsc.setBounds(20, 250, 550, 400);
        dsc.getHorizontalScrollBar();
        this.add(dsc);

        standart(1);

    }

    public static void standart(int row) {
        //Alle Datensätze in das Resultset und an bestimmter Position anzeigen
        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            sql = "Select * From " + tab;
            r = s.executeQuery(sql);
            rm = r.getMetaData();
            r.absolute(row);
            einfügen(r);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void einfügen(ResultSet r) {
        //Datensatz an bestimmter Position des Resultsets in die Tabele einfügen
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

            //Methode für die Detailtabelle
            vid = r.getInt(1);
            detailtab(vid);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void suchen() {
        //Bei dem bestätigen der Suchanfrage mit Enter wird die Suche gestartet und die ergebnisse als Resultset angezeigt
        s.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    r = Suchen.search(s.getText(), r);
                    s.setText("");
                    einfügen(r);
                    Einzelansicht.this.add(sz);
                    Einzelansicht.this.repaint();
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
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        //Aktionen des Menüs und der Buttons
        switch (command) {
            case "vor":
                try {
                    if (r.next()) {
                        einfügen(r);
                        vid = r.getInt("V_ID");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "rück":
                try {
                    if (r.previous()) {
                        einfügen(r);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "einf":
                //Ein Objekt der Klasse Hinzufügen wird erzeugt und ein Modaler Dialog wird angezeigt
                Hinzufügen d = new Hinzufügen(r, tab, this, true);
                d.setVisible(true);

                break;
            case "änd":
                int spalte = table.getSelectedColumn();
                //ist die ausgewählte Spalte > 0 dann wird der Datensatz nach bestätigung geändert
                if (spalte > 0) {
                    try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                        Statement s = conn.createStatement();
                        String eingabe = JOptionPane.showInputDialog(t.getColumnName(spalte), r.getString(spalte + 1));
                        if (eingabe != null) {
                            System.out.println("test");
                            ResultSet ra = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= '" + eingabe + "' WHERE " + t.getColumnName(0) + " = " + r.getString(1));
                            standart(r.getRow());
                        }
                        table.clearSelection();
                    } catch (SQLException f) {
                        JOptionPane.showMessageDialog(null, "Da hat etwas nicht geklappt", "Fehler!", JOptionPane.ERROR_MESSAGE);
                        table.clearSelection();
                        throw new RuntimeException(f);
                    }
                    t.fireTableDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Wählen Sie bitte eine Zelle der Tabelle aus. \n Die " + t.getColumnName(0) + " ist nicht veränderbar.");
                    table.clearSelection();
                }

                break;
            case "lösch":
                //Ein Objekt der Klasse Löschen wird erzeugt
                Löschen l = new Löschen(tab, t, table);
                break;
            case "close":
                //Die Einzelansicht schließt sich und man kehr tins Hauptmenü zurück
                this.dispose();
                Menü m = new Menü();
                m.setVisible(true);
                break;

        }

    }

    public static void detailtab(int vid) {
        //Tabelmodel wird an die Methode weitergegeben und für die ansicht der Spieler verändert
        Datail.detailmodel(dtm, vid, url);
        dtm.fireTableDataChanged();

        System.out.println("detail =" + vid);


    }
}
