import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.*;
import java.sql.*;

public class Tabellen extends JFrame implements TableModelListener {
    JScrollPane sc = null;
    JButton a = null;
    JButton einf = null;
    JButton del = null;
    static ResultSetMetaData rm = null;
    static DefaultTableModel t = new DefaultTableModel();
    static JTable table =new JTable(t);
    static ResultSet r = null;
    static String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";


    public Tabellen(String url, String tab) {
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);


        einfügen(tab);

        sc = new JScrollPane(table);
        sc.setBounds(50, 100, 400, 350);
        sc.getHorizontalScrollBar();
        this.add(sc);

        a = new JButton("Zurück");
        a.setBounds(50, 30, 80, 50);
        a.addActionListener(e -> {
            this.dispose();
            Menu m = new Menu();
            m.setVisible(true);
        });
        this.add(a);

        einf = new JButton("Einfügen");
        einf.setBounds(50, 480, 100, 50);
        einf.addActionListener(e -> {
            Hinzufügen d = new Hinzufügen(r, tab);
            d.setVisible(true);
            this.setFocusable(false);

        });
        this.add(einf);


        del = new JButton("Löschen");
        del.setBounds(290, 480, 100, 50);
        del.addActionListener(e -> {
            Löschen l = new Löschen(tab, t, table);


        });
        this.add(del);

        t.addTableModelListener(e->{
            int zeile =table.getSelectedRow();
            int spalte = table.getSelectedColumn();
            if(spalte>0 && zeile >0){
                int response = JOptionPane.showConfirmDialog(null, "Wollen Sie dein Eintrag in Zeile " + (zeile+1) +", Spalte "+(spalte +1)+ " ändern?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == 0) {
                    try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                        Statement s = conn.createStatement();
                        System.out.println(t.getColumnName(spalte)+"\n"+rm.getColumnTypeName(spalte+1));
                        if(rm.getColumnTypeName(spalte+1).equalsIgnoreCase("Varchar")){
                            ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= '" + t.getValueAt(zeile, spalte).toString() + "' WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                        } else if (rm.getColumnTypeName(spalte+1).equalsIgnoreCase("Date")) {
                            ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= '" + t.getValueAt(zeile, spalte).toString() + "' WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                        }else {
                            ResultSet r = s.executeQuery("Update " + tab + " set " + t.getColumnName(spalte) + "= " + t.getValueAt(zeile, spalte).toString() + " WHERE " + t.getColumnName(0) + " = " + t.getValueAt(zeile, 0));

                        }
                        t.removeTableModelListener(this::tableChanged);
                    } catch (SQLException f) {
                        throw new RuntimeException(f);
                    }
                    t.fireTableDataChanged();
                } else if (response == 1) {
                    einfügen(tab);
                    t.fireTableDataChanged();
                }

            }

        });
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

    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println(table.getSelectedRow()+"" +table.getSelectedColumn());



    }
}
