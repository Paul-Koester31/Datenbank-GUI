import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Menü extends JFrame implements ActionListener {
    JScrollPane sc = null;
    JButton a = null;
    JButton v = null;
    JList list = null;
    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";
    String table = null;

    public static void main(String[] args) {
        Menü m = new Menü();
        m.setVisible(true);
    }

    public Menü() {
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        this.setTitle("Bundesliga");

        JLabel lbl = new JLabel("");
        ImageIcon img = new ImageIcon(new ImageIcon(Menü.class.getResource("/image/Bundesiga.jfif")).getImage());
        lbl.setIcon(img);
        lbl.setBounds(150, 50, 200, 202);
        this.getContentPane().add(lbl);
        this.add(lbl);

        v = new JButton("Einzelansicht");
        v.setBounds(200, 420, 100, 60);
        v.setFont(new Font("Arial", Font.PLAIN, 11));
        v.setForeground(Color.black);
        v.setBackground(Color.white);
        v.addActionListener(e -> {
            table = list.getSelectedValue().toString();
            Einzelansicht a = new Einzelansicht(table);
            a.setVisible(true);
            this.dispose();

        });

        a = new JButton("Anzeigen");
        a.setBounds(200, 350, 90, 60);
        a.setFont(new Font("Arial", Font.PLAIN, 12));
        a.setForeground(Color.black);
        a.setBackground(Color.white);
        a.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, "root", "")) {
                if (list.isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(null, "Wählen sie eine Tabelle aus!");
                } else {
                    table = list.getSelectedValue().toString();
                    Tabellen t = new Tabellen(url, table);
                    t.setVisible(true);
                    t.setTitle(list.getSelectedValue().toString());
                    this.dispose();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
        this.add(a);


        DefaultListModel liste = new DefaultListModel();
        list = new JList(liste);
        list.addListSelectionListener(e -> {
            if (list.getSelectedValue().toString().equalsIgnoreCase("Verein")) {
                this.add(v);

            } else
                this.remove(v);
        });
        sc = new JScrollPane();
        sc.setViewportView(list);
        sc.setBounds(30, 300, 150, 200);
        this.add(sc);

        try (Connection conn = DriverManager.getConnection(url, "root", "")) {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery("Show Tables");
            while (r.next()) {
                liste.addElement(r.getString("Tables_In_bundesliga"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void paint(Graphics g) {
        super.paint(g);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {


    }
}
