package clientppnbm;

import serverppnbm.Operation;
import serverppnbm.ServerRMI;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class PPnBMCalculator extends javax.swing.JFrame {
    private Operation remoteService;
    private JTextField txtHargaBarang;
    private JComboBox<String> cboJenisBarang;
    private JTextField txtTarifPPnBM;
    private JTextField txtHasilPPnBM;
    private JButton btnHitung;
    private JButton btnReset;  // Tombol reset

    public PPnBMCalculator() {
        initComponents();
        connectToServer();
    }

    private void initComponents() {
        // Gunakan Look and Feel Nimbus yang lebih modern
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            // Jika Nimbus tidak ada, fallback ke system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) { }
        }
        
        setTitle("Kalkulator PPnBM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Perbesar ukuran GUI
        setPreferredSize(new Dimension(600, 350));
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Komponen
        JLabel lblHarga = new JLabel("Harga Barang:");
        txtHargaBarang = new JTextField(15);

        JLabel lblJenis = new JLabel("Jenis Barang:");
        cboJenisBarang = new JComboBox<>(new String[]{
            "Pilih Jenis Barang",
            "Perhiasan Mewah",
            "Hunian Mewah",
            "Hobby Mewah"
        });

        JLabel lblTarif = new JLabel("Tarif PPnBM (%):");
        txtTarifPPnBM = new JTextField(15);
        txtTarifPPnBM.setEditable(false);

        JLabel lblHasil = new JLabel("Hasil PPnBM:");
        txtHasilPPnBM = new JTextField(15);
        txtHasilPPnBM.setEditable(false);

        btnHitung = new JButton("Hitung");
        btnReset = new JButton("Reset"); // Inisialisasi tombol Reset

        // Penataan letak (layout)
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0; 
        gbc.gridy = 0;
        add(lblHarga, gbc);
        gbc.gridx = 1;
        add(txtHargaBarang, gbc);

        // Row 2
        gbc.gridx = 0; 
        gbc.gridy = 1;
        add(lblJenis, gbc);
        gbc.gridx = 1;
        add(cboJenisBarang, gbc);

        // Row 3
        gbc.gridx = 0; 
        gbc.gridy = 2;
        add(lblTarif, gbc);
        gbc.gridx = 1;
        add(txtTarifPPnBM, gbc);

        // Row 4
        gbc.gridx = 0; 
        gbc.gridy = 3;
        add(lblHasil, gbc);
        gbc.gridx = 1;
        add(txtHasilPPnBM, gbc);

        // Row 5 - Button Hitung
        gbc.gridx = 1; 
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnHitung, gbc);

        // Row 6 - Button Reset
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(btnReset, gbc);

        addListeners();

        pack();
        setLocationRelativeTo(null);
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 4000);
            remoteService = (Operation) registry.lookup("services");
            if (remoteService == null) {
                throw new Exception("Tidak dapat terhubung ke server");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error connecting to server: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addListeners() {
        cboJenisBarang.addActionListener(e -> updateTarifPPnBM()); // Hanya perbarui tarif
        btnHitung.addActionListener(e -> hitungPPnBM()); // Hitung hanya saat tombol diklik

        btnReset.addActionListener(e -> resetForm());
        
        txtHargaBarang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formatHargaBarang(); // Hanya format harga, tidak menghitung otomatis
            }
        });
    }

    private void updateTarifPPnBM() {
        try {
            String selectedItem = cboJenisBarang.getSelectedItem().toString();
            if (!selectedItem.equals("Pilih Jenis Barang")) {
                double tarif;
                switch (selectedItem) {
                    case "Perhiasan Mewah":
                        tarif = 30.0; // Tarif tetap untuk Perhiasan Mewah
                        break;
                    case "Hunian Mewah":
                        tarif = 40.0; // Tarif tetap untuk Hunian Mewah
                        break;
                    case "Hobby Mewah":
                        tarif = 20.0; // Tarif tetap untuk Hobby Mewah
                        break;
                    default:
                        tarif = 0.0; // Default jika tidak sesuai
                }
                txtTarifPPnBM.setText(String.format(Locale.ENGLISH, "%.1f%%", tarif)); // Tampilkan tarif dengan simbol %
            } else {
                txtTarifPPnBM.setText(""); // Kosongkan jika "Pilih Jenis Barang"
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void formatHargaBarang() {
        try {
            if (!txtHargaBarang.getText().trim().isEmpty()) {
                double amount = parseAmount(txtHargaBarang.getText());
                NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                txtHargaBarang.setText(currencyFormat.format(amount));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format harga tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseAmount(String amount) {
        String cleaned = amount.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(cleaned);
    }

    private void hitungPPnBM() {
        try {
            if (txtHargaBarang.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan harga barang!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cboJenisBarang.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Pilih jenis barang!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double hargaBarang = parseAmount(txtHargaBarang.getText());
            double tarifPPnBM = Double.parseDouble(txtTarifPPnBM.getText().replace("%", "")); // Hapus simbol %

            double hasil = hargaBarang * (tarifPPnBM / 100.0); // Perhitungan PPnBM

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            txtHasilPPnBM.setText(currencyFormat.format(hasil));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void resetForm() {
        txtHargaBarang.setText("");
        cboJenisBarang.setSelectedIndex(0);
        txtTarifPPnBM.setText("");
        txtHasilPPnBM.setText("");
    }

    public static void main(String args[]) {
        // Jalankan di Event Dispatch Thread
        java.awt.EventQueue.invokeLater(() -> {
            new PPnBMCalculator().setVisible(true);
        });
    }
}