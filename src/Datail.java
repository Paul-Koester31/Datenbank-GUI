import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Datail {
    public static ResultSet spieler(int verein, Connection con) {
        //liefert das Resulset mit allen Spielern des Vereins
        try {
            Statement s = con.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM Spieler WHERE Vereins_ID = " + verein +" ORDER BY Spieler_Name");
            return r;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static DefaultTableModel detailmodel(DefaultTableModel t,int verein, String url) {
        //Füllt das mitgegebene Tabelmodel mit den Daten der Spieler
        String[][] temp = {{""}};

        try (Connection conn = DriverManager.getConnection(url, "root", "")) {

            ResultSet r = spieler(verein, conn);
            ResultSetMetaData rm = r.getMetaData();
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
        //Gibt das Tabelmodel zurück
        return t;
    }
}
