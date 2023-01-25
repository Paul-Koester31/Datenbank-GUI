import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Menu extends JFrame implements ActionListener {
    JScrollPane sc = null;
    JButton a = null;

    JLabel i = null;
    JList list = null;
    ImageIcon img = null;

    String url = "jdbc:mariadb://127.0.0.1:3306/bundesliga";


    public static void main(String[] args) {
        Menu m = new Menu();
        m.setVisible(true);
    }
    public Menu() {
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setBackground(Color.WHITE);



        a = new JButton("Anzeigen");
        a.setBounds(200, 200, 150, 70);
        a.setFont(new Font("Arial",Font.PLAIN,15));
        a.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, "root", "")){
                String table = list.getSelectedValue().toString();
                Tabellen t = new Tabellen(url,table);
                t.setVisible(true);
                t.setTitle(list.getSelectedValue().toString());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            this.dispose();
        });
        this.add(a);


        DefaultListModel liste = new DefaultListModel();
        list = new JList(liste);
        sc = new JScrollPane();
        sc.setViewportView(list);
        sc.setBounds(30, 150, 150, 200);
        this.add(sc);

        try (Connection conn = DriverManager.getConnection(url, "root", "")){
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
        img = new ImageIcon("src/image/Bundesliga.jpg");
        i = new JLabel(img);
        i.setBounds(150, 280, 200, 100);
        this.add(i);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {


    }
}
