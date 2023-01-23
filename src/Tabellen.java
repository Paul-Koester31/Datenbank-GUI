import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Tabellen extends JFrame implements ActionListener {
    JScrollPane sc = null;
    JButton a = null;

    JButton änd = null;
    JButton einf = null;
    JButton del = null;
    static ResultSetMetaData rm = null;
    static JTable table = null;
    static DefaultTableModel t = null;
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
            {
            }
        });
        this.add(einf);

        änd = new JButton("Ändern");
        änd.setBounds(170, 480, 100, 50);
        änd.addActionListener(e -> {

        });
        this.add(änd);

        del = new JButton("Löschen");
        del.setBounds(290, 480, 100, 50);
        del.addActionListener(e -> {
            Löschen l = new Löschen(tab, t, table);


        });
        this.add(del);
    }

    public static void einfügen(String tab) {
        String[][] temp = {{""}};
        t = new DefaultTableModel();
        table = new JTable(t);

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
                t.fireTableRowsInserted(0, table.getRowCount());
                table.repaint();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
