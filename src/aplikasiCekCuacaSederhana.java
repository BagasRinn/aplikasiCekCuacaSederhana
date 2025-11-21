import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author lenov
 */
public class aplikasiCekCuacaSederhana extends javax.swing.JFrame {

    /**
     * Creates new form aplikasiCekCuacaSederhana
     */
    public aplikasiCekCuacaSederhana() {
        initComponents();
        
        setupAutoComplete();

        styleButton(btnCek);
        styleButton(btnExportCSV);
        styleButton(btnLoadCSV);
        styleButton(btnTambahFavorit);
    }
    
    private String fetchWeather(String kota) throws Exception {
        kota = URLEncoder.encode(kota, "UTF-8");

        // URL API
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" 
                + kota + "&appid=" + API_KEY + "&units=metric&lang=id";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Baca Response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }
    
    private void setupAutoComplete() {
        // 1. Siapkan Data Kota (Database Sederhana)
        ArrayList<String> daftarKota = new ArrayList<>();
        daftarKota.add("Jakarta");
        daftarKota.add("Bandung");
        daftarKota.add("Surabaya");
        daftarKota.add("Yogyakarta");
        daftarKota.add("Medan");
        daftarKota.add("Makassar");
        daftarKota.add("Semarang");
        daftarKota.add("Palembang");
        daftarKota.add("Banjarbaru"); // Kota kamu
        daftarKota.add("Banjarmasin");
        daftarKota.add("Balikpapan");
        daftarKota.add("Samarinda");
        daftarKota.add("Denpasar");
        daftarKota.add("London");
        daftarKota.add("Tokyo");
        daftarKota.add("New York");
        // Tambahkan kota lain sesuka hati di sini

        // 2. Buat Popup Menu untuk menampung saran
        JPopupMenu popup = new JPopupMenu();

        // 3. Pasang Event Listener di txtKota
        txtKota.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Jangan jalankan jika tombol yang ditekan adalah Enter atau Panah
                if (e.getKeyCode() == KeyEvent.VK_ENTER || 
                    e.getKeyCode() == KeyEvent.VK_UP || 
                    e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return;
                }

                String textInput = txtKota.getText().trim().toLowerCase();
                popup.setVisible(false); // Sembunyikan dulu
                popup.removeAll(); // Bersihkan saran lama

                // Jika kotak kosong, jangan tampilkan apa-apa
                if (textInput.isEmpty()) {
                    return;
                }

                // 4. Filter kota yang cocok
                boolean adaSaran = false;
                for (String kota : daftarKota) {
                    // Cek apakah nama kota mengandung huruf yang diketik
                    if (kota.toLowerCase().startsWith(textInput)) {
                        JMenuItem item = new JMenuItem(kota);
                        
                        // Saat item dipilih, isi txtKota dengan nama kota tsb
                        item.addActionListener(evt -> {
                            txtKota.setText(kota);
                            popup.setVisible(false);
                            // Opsional: Langsung otomatis klik tombol cek
                            // btnCek.doClick(); 
                        });
                        
                        popup.add(item);
                        adaSaran = true;
                    }
                }

                // 5. Tampilkan Popup jika ada saran
                if (adaSaran) {
                    // Tampilkan popup persis di bawah txtKota
                    popup.show(txtKota, 0, txtKota.getHeight());
                    txtKota.requestFocus(); // Tetap fokus ke text field
                }
            }
        });
    }


    private void tambahKeRiwayat(String kota) {
        // Ambil model dari tabel yang sudah ada (tblRiwayat)
        DefaultTableModel model = (DefaultTableModel) tblRiwayat.getModel();

        // Ambil data suhu dan kondisi saat ini agar tabel lebih lengkap
        String suhu = lblSuhu.getText();
        String kondisi = lblKondisi.getText();

        // Format waktu agar lebih rapi
        String waktu = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                .format(LocalDateTime.now());

        // Tambahkan baris baru ke tabel
        model.addRow(new Object[]{kota, suhu, kondisi, waktu});
    }


    private void updateWeatherUI(String json) throws Exception {
        JSONObject obj = (JSONObject) new JSONParser().parse(json);

        String nama = (String) obj.get("name");

        JSONObject main = (JSONObject) obj.get("main");
        double suhu = (double) main.get("temp");

        JSONArray weather = (JSONArray) obj.get("weather");
        JSONObject info = (JSONObject) weather.get(0);

        String kondisi = (String) info.get("description");
        String icon = (String) info.get("icon");

        lblNamaKota.setText(nama);
        lblSuhu.setText(suhu + " °C");
        lblKondisi.setText(kondisi);

        lblIconCuaca.setIcon(new ImageIcon("icons/" + icon + ".png"));
    }



    
    private final String API_KEY = "7b258a6330ebe0e04bced2727626e11b";

    
    private void styleButton(JButton btn) {
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtKota = new javax.swing.JTextField();
        btnCek = new javax.swing.JButton();
        cmbFavorit = new javax.swing.JComboBox<>();
        btnTambahFavorit = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblNamaKota = new javax.swing.JLabel();
        lblSuhu = new javax.swing.JLabel();
        lblKondisi = new javax.swing.JLabel();
        lblIconCuaca = new javax.swing.JLabel();
        lblNamaKota1 = new javax.swing.JLabel();
        lblNamaKota2 = new javax.swing.JLabel();
        lblNamaKota3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRiwayat = new javax.swing.JTable();
        btnExportCSV = new javax.swing.JButton();
        btnLoadCSV = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Aplikasi Pengecek Cuaca");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(230, 230, 230)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Masukan Kota :");

        btnCek.setText("Cek Cuaca");
        btnCek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCekActionPerformed(evt);
            }
        });

        cmbFavorit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        btnTambahFavorit.setText("Tambah Favorit");
        btnTambahFavorit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahFavoritActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Favorit :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtKota)
                    .addComponent(cmbFavorit, 0, 259, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCek, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambahFavorit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCek, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFavorit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambahFavorit)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblNamaKota.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblSuhu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblKondisi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblIconCuaca.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblNamaKota1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNamaKota1.setText("Kota :");

        lblNamaKota2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNamaKota2.setText("Kondisi :");

        lblNamaKota3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNamaKota3.setText("Suhu :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNamaKota2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNamaKota3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNamaKota1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(48, 48, 48)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNamaKota)
                    .addComponent(lblSuhu)
                    .addComponent(lblKondisi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblIconCuaca)
                .addGap(122, 122, 122))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNamaKota)
                    .addComponent(lblNamaKota1))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSuhu)
                    .addComponent(lblNamaKota3)
                    .addComponent(lblIconCuaca))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKondisi)
                    .addComponent(lblNamaKota2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kota", "Suhu (°C)", "Kondisi", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblRiwayat);

        btnExportCSV.setText("Export CSV");
        btnExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCSVActionPerformed(evt);
            }
        });

        btnLoadCSV.setText("Import CSV");
        btnLoadCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExportCSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLoadCSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnExportCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(btnLoadCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCekActionPerformed
        try {
            String kota = txtKota.getText();
            String json = fetchWeather(kota);

            updateWeatherUI(json);
            tambahKeRiwayat(kota);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cek cuaca! " + e.getMessage());
        }
    }//GEN-LAST:event_btnCekActionPerformed

    private void btnTambahFavoritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahFavoritActionPerformed
        cmbFavorit.addItem(txtKota.getText());
    }//GEN-LAST:event_btnTambahFavoritActionPerformed

    private void btnExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCSVActionPerformed
        try {
            FileWriter fw = new FileWriter("riwayat.csv");

            for (int i = 0; i < tblRiwayat.getRowCount(); i++) {
                fw.write(tblRiwayat.getValueAt(i,0) + "," +
                         tblRiwayat.getValueAt(i,1) + "," +
                         tblRiwayat.getValueAt(i,2) + "\n");
            }

            fw.close();
            JOptionPane.showMessageDialog(this, "Berhasil simpan CSV!");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan!");
        }
    }//GEN-LAST:event_btnExportCSVActionPerformed

    private void btnLoadCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadCSVActionPerformed
        try {
            BufferedReader br = new BufferedReader(new FileReader("riwayat.csv"));
            DefaultTableModel model = (DefaultTableModel) tblRiwayat.getModel();
            model.setRowCount(0);

            String line;
            while ((line = br.readLine()) != null) {
                model.addRow(line.split(","));
            }

            br.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load CSV!");
        }
    }//GEN-LAST:event_btnLoadCSVActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(aplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(aplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(aplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(aplikasiCekCuacaSederhana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new aplikasiCekCuacaSederhana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCek;
    private javax.swing.JButton btnExportCSV;
    private javax.swing.JButton btnLoadCSV;
    private javax.swing.JButton btnTambahFavorit;
    private javax.swing.JComboBox<String> cmbFavorit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblIconCuaca;
    private javax.swing.JLabel lblKondisi;
    private javax.swing.JLabel lblNamaKota;
    private javax.swing.JLabel lblNamaKota1;
    private javax.swing.JLabel lblNamaKota2;
    private javax.swing.JLabel lblNamaKota3;
    private javax.swing.JLabel lblSuhu;
    private javax.swing.JTable tblRiwayat;
    private javax.swing.JTextField txtKota;
    // End of variables declaration//GEN-END:variables
}
