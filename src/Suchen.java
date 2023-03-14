import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;

public class Suchen {

    static String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";
    static JFrame frame;
    static DefaultListModel liste;
    static JList list;
    static JScrollPane sc;
    static int index = 0;
    static boolean auswahl = false;
    static boolean fertig = false;


    public static ResultSet search(String st, ResultSet rs) {
        String sq = "";
        String sql = "";
        liste = new DefaultListModel();
        list = new JList(liste);
        if (st.length() != 0) {
            try {
                //SQL String
                ResultSetMetaData rm = rs.getMetaData();
                for (int j = 1; j <= rm.getColumnCount(); j++) {
                    if (j == rm.getColumnCount()) {
                        sq = sq + rm.getColumnName(j) + " LIKE '%" + st + "%'";
                    } else {
                        sq = sq + rm.getColumnName(j) + " LIKE '%" + st + "%' or ";
                    }
                }
                sql = "SELECT * FROM Verein WHERE " + sq;
                System.out.println(sql);
                Connection conn = DriverManager.getConnection(url, "root", "");
                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery(sql);

                //Ergebnisse in Liste
                if (r.next()) {
                    r.beforeFirst();
                    while (r.next()) {
                        liste.addElement(r.getString("Name"));
                    }
                    r.first();
                    frame = new JFrame("Suchergebnis");
                    frame.setSize(300, 300);
                    frame.setVisible(true);

                    sc = new JScrollPane();
                    sc.setViewportView(list);
                    sc.setBounds(30, 100, 150, 200);
                    frame.add(sc);

                    //Auswahl durch Doppelklick
                    list.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            JList list = (JList) e.getSource();
                            if (e.getClickCount() == 2) {
                                index = list.locationToIndex(e.getPoint()) + 1;
                                frame.dispose();
                                list = null;
                                liste = null;
                                try {
                                    r.absolute(index);
                                    System.out.println("index =" + index);
                                    Einzelansicht.einfügen(r);
                                    Einzelansicht.r = r;
                                    auswahl = true;
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }

                            }
                        }
                    });


                    System.out.println(auswahl);
                    return r;
                } else {
                    throw new Exception();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                return rs;
            }

        }
        return rs;
    }
}


