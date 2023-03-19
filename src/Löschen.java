import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.event.*;
import java.sql.*;

public class Löschen {

    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";

    public Löschen(String tab, DefaultTableModel model, JTable table) {

        //Bestätigung des Users
        int response = JOptionPane.showConfirmDialog(null, "Wollen Sie dein Eintrag löschen?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == 0) {
            try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                Statement s = conn.createStatement();

                //Backup erstellen und Datensatz löschen
                //SELECT *, CURRENT_TIMESTAMP FROM spiel WHERE spiel.Heim =1 or spiel.Gast =1 AND spiel.Spiel_ID NOT IN (SELECT b.Spiel_ID FROM bundesliga_archiv.spiel as b);
                ResultSet r = s.executeQuery("INSERT into bundesliga_archiv.verein SELECT * ,CURRENT_TIMESTAMP FROM `verein` WHERE V_ID = " + Einzelansicht.vid);
                r = s.executeQuery("DELETE  From " + tab + " WHERE " + model.getColumnName(0) + "=" + Einzelansicht.vid);

                //In den vorherigen oder nächsten Datensatz wechseln
                if (Einzelansicht.r.previous()) {
                    Einzelansicht.standart(Einzelansicht.r.getRow());
                } else if (Einzelansicht.r.next()) {
                    Einzelansicht.standart(Einzelansicht.r.getRow());
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {

        }

    }


}
