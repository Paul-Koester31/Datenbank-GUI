import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Hinzufügen extends JFrame implements ActionListener {
    JButton a = null;
    ResultSetMetaData rm = null;


    JButton einf = null;

    JTable table = null;
    DefaultTableModel t = null;

    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";

    String spalten = "";
    String sname = "";
    Font font = new Font("Arial",Font.PLAIN,12);

    public Hinzufügen(ResultSet r, String tab) {
        this.setSize(500, 600);
        this.setLayout(null);



        a = new JButton("Zurück");
        a.setBounds(50, 30, 80, 50);
        a.setFont(font);
        a.setBackground(Color.white);
        a.setForeground(Color.black);
        a.addActionListener(e -> {
            this.dispose();
        });
        this.add(a);

        try {
            rm = r.getMetaData();
            int x = 10;
            int y = 100;
            JTextField[] b = new JTextField[rm.getColumnCount()];
            int z = rm.getColumnCount();
            for (int i = 2; i <= z; i++) {
                int w = 90;
                JLabel a = new JLabel(rm.getColumnName(i));
                a.setBounds(x, y, w, 60);
                b[i - 1] = new JTextField();
                b[i - 1].setBounds(x, y + 70, w, 70);
                this.add(b[i - 1]);
                this.add(a);
                x = x + w;
                if (x >= 400) {
                    x = 10;
                    y = 300;
                }
            }
            einf = new JButton("Einfügen");
            einf.setBounds(50, 480, 100, 50);
            einf.setFont(font);
            einf.setForeground(Color.black);
            einf.setBackground(Color.white);
            einf.addActionListener(e -> {
                for (int i = 1; i < z; i++) {
                    try {
                        System.out.println(b[i].getText());
                        if (b[i].getText().length() == 0) {
                            int response = JOptionPane.showConfirmDialog(null, "Soll der Wert für " + rm.getColumnName(i + 1) + " 'NULL' übernommen werden?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            System.out.println(response);
                            if (response == 0) {
                                //null
                            } else {
                                break;
                            }
                        }

                        if (i == z - 1) {
                            spalten = spalten + "'" + b[i].getText() + "'";
                            sname = sname + rm.getColumnName(i + 1);
                        } else {
                            spalten = spalten + "'" + b[i].getText() + "',";
                            sname = sname + rm.getColumnName(i + 1) + ",";
                        }


                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                System.out.println(sname);
                System.out.println(spalten);
                String sql = "Insert into " + tab + "(" + sname + ") Values (" + spalten + ")";
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(url, "root", "");
                    Statement s = conn.createStatement();
                    ResultSet a = s.executeQuery(sql);
                    this.dispose();
                    Tabellen.einfügen(tab);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            this.add(einf);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
