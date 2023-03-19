import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Hinzufügen extends Dialog implements ActionListener {
    JButton a = null;
    ResultSetMetaData rm = null;
    JButton einf = null;
    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";
    String spalten = "";
    String sname = "";
    Font font = new Font("Arial", Font.PLAIN, 12);


    public Hinzufügen(ResultSet r, String tab, JFrame owner, boolean modal) {
        //Ein Modaler Dialog braucht einen Owner, in dme Fall der Frame
        super(owner, modal);

        //Framesettings
        this.setSize(500, 600);
        this.setLayout(null);

        //Button um den Vorgang abzubrechen
        a = new JButton("Zurück");
        a.setBounds(50, 30, 80, 50);
        a.setFont(font);
        a.setBackground(Color.white);
        a.setForeground(Color.black);
        a.addActionListener(e -> {
            this.dispose();
        });
        this.add(a);

        //Es wird für jede Spalte der Tabelle ein Textfeld und ein Bezeichner (JLabel) erzeugt
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
            //Der Button zum Bestätigen der Eingabe
            einf = new JButton("Einfügen");
            einf.setBounds(50, 480, 100, 50);
            einf.setFont(font);
            einf.setForeground(Color.black);
            einf.setBackground(Color.white);
            einf.addActionListener(e -> {
                //Erst wird jedes Textfeld durchgegangen und unterschieden, ob die eingabe Null ist oder nicht
                for (int i = 1; i < z; i++) {
                    try {
                        //Ist die letze Spalte bzw Textfeld erreicht dann wird Folgendes ausgeführt
                        if (i == z - 1) {
                            //Wenn ein Textfeld Null ist, dann wird der User gefragt, ob er dies so übernehmen möchte
                            if (b[i].getText().length() == 0) {
                                int response = JOptionPane.showConfirmDialog(null, "Soll der Wert für " + rm.getColumnName(i + 1) + " 'NULL' übernommen werden?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                System.out.println(response);
                                if (response == 0) {
                                    //Der Wert Null wird übernommen
                                    spalten = spalten + " Null";
                                    sname = sname + rm.getColumnName(i + 1);
                                } else {
                                    //Der Wert Null wird nicht übernommen
                                    spalten ="";
                                    sname="";
                                }
                            } else {
                                //Um dem SQL-Syntax gerecht zu werden hat der letzte Wert kein ','
                                spalten = spalten + "'" + b[i].getText() + "'";
                                sname = sname + rm.getColumnName(i + 1);
                            }
                        } else {
                            //Liefert das Textfeld Text bzw Werte, dann werden Strings gefült
                            spalten = spalten + "'" + b[i].getText() + "',";
                            sname = sname + rm.getColumnName(i + 1) + ",";
                        }
                        System.out.println(b[i].getText());

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                System.out.println(sname);
                System.out.println(spalten);
                String sql = "Insert into " + tab + "(" + sname + ") Values (" + spalten + ")";
                System.out.println(sql);
                Connection conn = null;

                //Der Eintrag wird in die Datenbank eingefügt
                try {
                    conn = DriverManager.getConnection(url, "root", "");
                    Statement s = conn.createStatement();
                    ResultSet a = s.executeQuery(sql);
                    this.dispose();
                    //Der eingefügte Datensatz wird angezeigt
                    Einzelansicht.r.last();
                    Einzelansicht.standart(Einzelansicht.r.getRow() + 1);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Da hat etwas nicht geklappt", "Fehler!", JOptionPane.ERROR_MESSAGE);
                    sname = "";
                    spalten = "";
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
