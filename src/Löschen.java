import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;
import java.awt.event.*;
import java.sql.*;

public class Löschen {

    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";

    public Löschen(String tab, DefaultTableModel model, JTable table) {

        int response = JOptionPane.showConfirmDialog(null, "Wollen Sie dein Eintrag löschen?", "Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == 0) {
            try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery("INSERT into bundesliga_archiv.verein SELECT * ,CURRENT_TIMESTAMP FROM `verein` WHERE V_ID = " + Einzelansicht.vid);
                r = s.executeQuery("DELETE  From " + tab + " WHERE " + model.getColumnName(0) + "=" + Einzelansicht.vid);

                Einzelansicht.r.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {

        }

    }


}
