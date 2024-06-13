/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ql_dienmay_chinhthuc;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class Menu extends javax.swing.JFrame {

    int x = 210;    //chieu rong
    int y = 600;    //chieu cao

    /**
     * Creates new form Menu
     */
    String typetk;
    String username;

    public Menu(String loaitk, String name) {
        initComponents();
        typetk = loaitk.trim();
        username = name.trim();
        loadTaiKhoan();
        jplSlideMenu.setSize(210, 600);
    }

    public void loadTaiKhoan() {
        //Load menu
        lblUsername.setText(username);

        if (typetk == "kh") {
            pnlMenuKH.setVisible(true);
            pnlMenuQL.setVisible(false);
        } else {
            pnlMenuKH.setVisible(false);
            pnlMenuQL.setVisible(true);
        }
    }

    private String generateMaSanPham() {
        String maSanPham = "";
        try {
            Connection connection = KetNoi.getConnection();
            // Truy vấn để lấy ra mã sản phẩm cuối cùng
            String query = "SELECT TOP 1 ma_sp FROM db_sanpham ORDER BY ma_sp DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String lastMaSanPham = resultSet.getString("ma_sp");
                // Tách phần số từ mã sản phẩm cuối cùng
                String numberPart = lastMaSanPham.substring(2); // Bỏ qua "SP" ở đầu
                // Chuyển phần số sang kiểu số nguyên
                int number = Integer.parseInt(numberPart);
                // Tăng số lên 1
                number++;
                // Tạo mã sản phẩm mới
                maSanPham = "SP" + String.format("%08d", number); // Đảm bảo mã sản phẩm luôn có 8 chữ số
            } else {
                // Nếu không có mã sản phẩm nào trong cơ sở dữ liệu, ta sẽ bắt đầu từ SP00000001
                maSanPham = "SP00000001";
            }
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return maSanPham;
    }

    public void openMenu() {
        jplSlideMenu.setSize(x, y);
        if (x == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i <= 210; i++) {
                            jplSlideMenu.setSize(i, y);
                            Thread.sleep(1);
                        }
                    } catch (Exception e) {
                    }
                }
            }).start();
            x = 210;
        }
    }

    public void closeMenu() {
        jplSlideMenu.setSize(x, y);
        if (x == 210) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 210; i >= 0; i--) {
                            jplSlideMenu.setSize(i, y);
                            Thread.sleep(2);
                        }
                    } catch (Exception e) {
                    }
                }
            }).start();
            x = 0;
        }
    }

    public void resetMainPanel() {
        pnlSanPhamPage.setVisible(false);
        pnlGioHangPage.setVisible(false);
        pnlDonMuaPage.setVisible(false);
        pnlTaiKhoanPage.setVisible(false);
        pnlQL_HoaDon.setVisible(false);
        pnlQL_KhachHang.setVisible(false);
        pnlQL_NCC.setVisible(false);
        pnlQL_NhanVien.setVisible(false);
        pnlQL_PhieuNhap.setVisible(false);
        pnlQL_SanPham.setVisible(false);
        pnlQL_ThongKe.setVisible(false);
        closeMenu();
    }

    private boolean isValidData(String text) {
        return !text.isEmpty();
    }

    private boolean isValidNumber(String text) {
        try {
            int number = Integer.parseInt(text);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void editProduct() {
        int selectedRowIndex = TableProduct.getSelectedRow();
        if (selectedRowIndex != -1) {
            TxtMaHang.setText(TableProduct.getValueAt(selectedRowIndex, 0).toString());
            CbLoaiHang.setSelectedItem(TableProduct.getValueAt(selectedRowIndex, 1).toString());
            TxtTenHang.setText(TableProduct.getValueAt(selectedRowIndex, 2).toString());
            TxtDonViTinh.setText(TableProduct.getValueAt(selectedRowIndex, 3).toString());
            TxtGiaTien.setText(TableProduct.getValueAt(selectedRowIndex, 4).toString());
            TxtThoiGianBH.setText(TableProduct.getValueAt(selectedRowIndex, 5).toString());
            TxtSoLuong.setText(TableProduct.getValueAt(selectedRowIndex, 6).toString());

            // Lấy mã nhà cung cấp từ bảng sản phẩm
            Object maNCCObject = TableProduct.getValueAt(selectedRowIndex, 7);
            if (maNCCObject != null) {
                String maNCC = maNCCObject.toString();

                // Load tên nhà cung cấp tương ứng vào combobox
                try {
                    Connection connection = KetNoi.getConnection();
                    String query = "SELECT ten_ncc FROM db_nha_cung_cap WHERE ma_ncc = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, maNCC);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        String tenNhaCungCap = resultSet.getString("ten_ncc");
                        CbHangSanXuat.setSelectedItem(tenNhaCungCap);
                    }
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                // Nếu mã nhà cung cấp là null, không chọn gì trong combobox
                CbHangSanXuat.setSelectedIndex(-1);
            }
        }
    }

    private void insertDataIntoDatabase() {
        String maHang = generateMaSanPham();
        String tenHang = TxtTenHang.getText();
        String donViTinh = TxtDonViTinh.getText();
        String giaTien = TxtGiaTien.getText();
        String thoiGianBH = TxtThoiGianBH.getText();
        String soLuong = TxtSoLuong.getText();
        String tenNhaCungCap = CbHangSanXuat.getSelectedItem().toString();
        String hinhAnh = jTextField1.getText();

        Connection connection = KetNoi.getConnection();
        if (!TxtMaHang.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng clear dữ liệu trước khi thêm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidData(tenHang) || !isValidData(donViTinh) || !isValidData(giaTien)
                || !isValidData(thoiGianBH) || !isValidData(soLuong) || !isValidData(tenNhaCungCap)) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin vào các trường.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidNumber(giaTien) || !isValidNumber(soLuong)) {
            JOptionPane.showMessageDialog(null, "Giá tiền và số lượng phải là số lớn hơn 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Truy vấn để lấy mã nhà cung cấp dựa trên tên nhà cung cấp
            String queryMaNCC = "SELECT ma_ncc FROM db_nha_cung_cap WHERE ten_ncc = ?";
            PreparedStatement statementMaNCC = connection.prepareStatement(queryMaNCC);
            statementMaNCC.setString(1, tenNhaCungCap);
            ResultSet resultSetMaNCC = statementMaNCC.executeQuery();

            // Kiểm tra xem kết quả có tồn tại không
            if (!resultSetMaNCC.next()) {
                JOptionPane.showMessageDialog(null, "Tên nhà cung cấp không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy mã nhà cung cấp từ kết quả truy vấn
            String maNCC = resultSetMaNCC.getString("ma_ncc");

            // Thêm sản phẩm vào cơ sở dữ liệu
            String query = "INSERT INTO db_sanpham (ma_sp, ma_loai, ten_sp, don_vi_tinh, gia_sp, thoi_gian_bh, soluong, ma_ncc, hinhanh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, maHang);
            statement.setString(2, CbLoaiHang.getSelectedItem().toString());
            statement.setString(3, tenHang);
            statement.setString(4, donViTinh);
            statement.setInt(5, Integer.parseInt(giaTien));
            statement.setString(6, thoiGianBH);
            statement.setInt(7, Integer.parseInt(soLuong));
            statement.setString(8, maNCC); // Sử dụng mã nhà cung cấp
            statement.setString(9, hinhAnh);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Dữ liệu đã được thêm vào cơ sở dữ liệu.");
            TxtMaHang.setText("");
            TxtTenHang.setText("");
            TxtDonViTinh.setText("");
            TxtGiaTien.setText("");
            TxtThoiGianBH.setText("");
            TxtSoLuong.setText("");
            jTextField1.setText("");
            jLabel7.setText("");

            connection.close();
        } catch (SQLException ex) {
            System.out.println("Lỗi khi thêm dữ liệu vào cơ sở dữ liệu: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void DisplayPr() {
        String query = "Select * from db_sanpham";
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        Connection connection = KetNoi.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = (DefaultTableModel) TableProduct.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getString("ma_sp"),
                    resultSet.getString("ma_loai"),
                    resultSet.getString("ten_sp"),
                    resultSet.getString("don_vi_tinh"),
                    decimalFormat.format(resultSet.getInt("gia_sp")),
                    resultSet.getInt("thoi_gian_bh"),
                    resultSet.getInt("soluong"),
                    resultSet.getString("ma_ncc"),
                    resultSet.getString("hinhanh"),};
                model.addRow(row);
            }
            connection.close();
        } catch (SQLException ex) {
            System.out.println("Lỗi khi truy vấn dữ liệu từ cơ sở dữ liệu: " + ex.getMessage());
        }
        loadHangSanXuat(CbHangSanXuat);
    }

    private void deleteProduct() {
        int selectedRowIndex = TableProduct.getSelectedRow();
        Connection connection = KetNoi.getConnection();
        if (selectedRowIndex != -1) {
            String selectedProductId = TableProduct.getValueAt(selectedRowIndex, 0).toString();

            int confirmDialog = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirmDialog == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM db_sanpham WHERE ma_sp = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, selectedProductId);
                    statement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    DisplayPr();
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println("Lỗi khi xóa sản phẩm từ cơ sở dữ liệu: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa.", "Lưu ý", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void loadHangSanXuat(JComboBox<String> cbHangSanXuat) {
        cbHangSanXuat.removeAllItems();
        try (Connection conn = KetNoi.getConnection()) {
            String query = "SELECT ten_ncc FROM db_nha_cung_cap";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String tenNCC = rs.getString("ten_ncc");
                cbHangSanXuat.addItem(tenNCC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jplSlideMenu = new javax.swing.JPanel();
        pnlThongTin = new javax.swing.JPanel();
        avatar = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        lblCloseMenu = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnDangXuat = new javax.swing.JLabel();
        btnGthieu = new javax.swing.JLabel();
        pnlMenuKH = new javax.swing.JPanel();
        btnKH_SanPham = new javax.swing.JLabel();
        btnKH_DonMua = new javax.swing.JLabel();
        btnKH_GioHang = new javax.swing.JLabel();
        btnKH_TaiKhoan = new javax.swing.JLabel();
        pnlMenuQL = new javax.swing.JPanel();
        btnQL_SanPham = new javax.swing.JLabel();
        btnQL_HoaDon = new javax.swing.JLabel();
        btnQL_NhaCungCap = new javax.swing.JLabel();
        btnQL_KhachHang = new javax.swing.JLabel();
        btnQL_PhieuNhap = new javax.swing.JLabel();
        btnQL_ThongKe = new javax.swing.JLabel();
        btnQL_NhanVien = new javax.swing.JLabel();
        btnHelp = new javax.swing.JLabel();
        jpllMenuBar = new javax.swing.JPanel();
        lblOpenMenu = new javax.swing.JLabel();
        jplTitle = new javax.swing.JPanel();
        jplMain = new javax.swing.JPanel();
        pnlSanPhamPage = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlGioHangPage = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlDonMuaPage = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        pnlTaiKhoanPage = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        pnlQL_SanPham = new javax.swing.JPanel();
        pnlQL_SP_Bottom = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        CbLoaiHang = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        TxtMaHang = new javax.swing.JTextField();
        TxtTenHang = new javax.swing.JTextField();
        TxtSoLuong = new javax.swing.JTextField();
        TxtGiaTien = new javax.swing.JTextField();
        TxtThoiGianBH = new javax.swing.JTextField();
        BtnChonAnh = new javax.swing.JButton();
        lblLoadAnhSp = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        CbHangSanXuat = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        TxtDonViTinh = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableProduct = new javax.swing.JTable();
        BtnThem = new javax.swing.JButton();
        BtnSua = new javax.swing.JButton();
        BtnXoa = new javax.swing.JButton();
        BtnReset = new javax.swing.JButton();
        BtnThoat = new javax.swing.JButton();
        pnlQL_HoaDon = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        pnlQL_NCC = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        pnlQL_KhachHang = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        pnlQL_PhieuNhap = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        pnlQL_ThongKe = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        pnlQL_NhanVien = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jplSlideMenu.setBackground(new java.awt.Color(255, 255, 255));
        jplSlideMenu.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jplSlideMenu.setPreferredSize(new java.awt.Dimension(190, 590));
        jplSlideMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlThongTin.setBackground(new java.awt.Color(255, 255, 255));
        pnlThongTin.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ql_dienmay_chinhthuc/icon/avatar.png"))); // NOI18N

        lblUsername.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        lblUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsername.setText("NKD");

        lblCloseMenu.setFont(new java.awt.Font("Segoe UI Symbol", 0, 24)); // NOI18N
        lblCloseMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCloseMenu.setText("X");
        lblCloseMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseMenuMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlThongTinLayout = new javax.swing.GroupLayout(pnlThongTin);
        pnlThongTin.setLayout(pnlThongTinLayout);
        pnlThongTinLayout.setHorizontalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(avatar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCloseMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 18, Short.MAX_VALUE))
        );
        pnlThongTinLayout.setVerticalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCloseMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplSlideMenu.add(pnlThongTin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 150));
        jplSlideMenu.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 452, 210, 10));

        btnDangXuat.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        btnDangXuat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnDangXuat.setText("Đăng Xuất");
        btnDangXuat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDangXuatMouseClicked(evt);
            }
        });
        jplSlideMenu.add(btnDangXuat, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 210, 30));

        btnGthieu.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        btnGthieu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGthieu.setText("Giới Thiệu");
        jplSlideMenu.add(btnGthieu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, 210, 30));

        pnlMenuKH.setBackground(new java.awt.Color(255, 255, 255));

        btnKH_SanPham.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnKH_SanPham.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnKH_SanPham.setText("Sản phẩm");
        btnKH_SanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKH_SanPhamMouseClicked(evt);
            }
        });

        btnKH_DonMua.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnKH_DonMua.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnKH_DonMua.setText("Đơn mua");
        btnKH_DonMua.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKH_DonMuaMouseClicked(evt);
            }
        });

        btnKH_GioHang.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnKH_GioHang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnKH_GioHang.setText("Giỏ hàng");
        btnKH_GioHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKH_GioHangMouseClicked(evt);
            }
        });

        btnKH_TaiKhoan.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnKH_TaiKhoan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnKH_TaiKhoan.setText("Tài khoản");
        btnKH_TaiKhoan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKH_TaiKhoanMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlMenuKHLayout = new javax.swing.GroupLayout(pnlMenuKH);
        pnlMenuKH.setLayout(pnlMenuKHLayout);
        pnlMenuKHLayout.setHorizontalGroup(
            pnlMenuKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnKH_SanPham, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnKH_GioHang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnKH_DonMua, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnKH_TaiKhoan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        pnlMenuKHLayout.setVerticalGroup(
            pnlMenuKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuKHLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(btnKH_SanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKH_GioHang, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKH_DonMua, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKH_TaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        jplSlideMenu.add(pnlMenuKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 190, 300));

        pnlMenuQL.setBackground(new java.awt.Color(255, 255, 255));

        btnQL_SanPham.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_SanPham.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_SanPham.setText("Sản phẩm");
        btnQL_SanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_SanPhamMouseClicked(evt);
            }
        });

        btnQL_HoaDon.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_HoaDon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_HoaDon.setText("Hóa đơn");
        btnQL_HoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_HoaDonMouseClicked(evt);
            }
        });

        btnQL_NhaCungCap.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_NhaCungCap.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_NhaCungCap.setText("Nhà cung cấp");
        btnQL_NhaCungCap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_NhaCungCapMouseClicked(evt);
            }
        });

        btnQL_KhachHang.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_KhachHang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_KhachHang.setText("Khách hàng");
        btnQL_KhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_KhachHangMouseClicked(evt);
            }
        });

        btnQL_PhieuNhap.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_PhieuNhap.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_PhieuNhap.setText("Phiếu nhập");
        btnQL_PhieuNhap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_PhieuNhapMouseClicked(evt);
            }
        });

        btnQL_ThongKe.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_ThongKe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_ThongKe.setText("Thống kê");
        btnQL_ThongKe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_ThongKeMouseClicked(evt);
            }
        });

        btnQL_NhanVien.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        btnQL_NhanVien.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQL_NhanVien.setText("Nhân viên");
        btnQL_NhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQL_NhanVienMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlMenuQLLayout = new javax.swing.GroupLayout(pnlMenuQL);
        pnlMenuQL.setLayout(pnlMenuQLLayout);
        pnlMenuQLLayout.setHorizontalGroup(
            pnlMenuQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnQL_SanPham, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlMenuQLLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMenuQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnQL_HoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnQL_NhaCungCap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnQL_KhachHang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnQL_PhieuNhap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnQL_ThongKe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnQL_NhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlMenuQLLayout.setVerticalGroup(
            pnlMenuQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuQLLayout.createSequentialGroup()
                .addComponent(btnQL_SanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_HoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_NhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_KhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_PhieuNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_ThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQL_NhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jplSlideMenu.add(pnlMenuQL, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 190, 300));

        btnHelp.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        btnHelp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnHelp.setText("Trợ Giúp");
        jplSlideMenu.add(btnHelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 480, 210, 30));

        jPanel1.add(jplSlideMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 0, 600));

        jpllMenuBar.setBackground(new java.awt.Color(255, 255, 255));

        lblOpenMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ql_dienmay_chinhthuc/icon/menu.png"))); // NOI18N
        lblOpenMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOpenMenuMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jpllMenuBarLayout = new javax.swing.GroupLayout(jpllMenuBar);
        jpllMenuBar.setLayout(jpllMenuBarLayout);
        jpllMenuBarLayout.setHorizontalGroup(
            jpllMenuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpllMenuBarLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblOpenMenu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpllMenuBarLayout.setVerticalGroup(
            jpllMenuBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpllMenuBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblOpenMenu)
                .addContainerGap(557, Short.MAX_VALUE))
        );

        jPanel1.add(jpllMenuBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 940, 60));

        jplTitle.setBackground(new java.awt.Color(0, 168, 255));

        javax.swing.GroupLayout jplTitleLayout = new javax.swing.GroupLayout(jplTitle);
        jplTitle.setLayout(jplTitleLayout);
        jplTitleLayout.setHorizontalGroup(
            jplTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 940, Short.MAX_VALUE)
        );
        jplTitleLayout.setVerticalGroup(
            jplTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jplTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 30));

        jplMain.setBackground(new java.awt.Color(255, 255, 255));
        jplMain.setLayout(new java.awt.CardLayout());

        pnlSanPhamPage.setBackground(new java.awt.Color(255, 255, 255));
        pnlSanPhamPage.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Sản phẩm");

        javax.swing.GroupLayout pnlSanPhamPageLayout = new javax.swing.GroupLayout(pnlSanPhamPage);
        pnlSanPhamPage.setLayout(pnlSanPhamPageLayout);
        pnlSanPhamPageLayout.setHorizontalGroup(
            pnlSanPhamPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSanPhamPageLayout.createSequentialGroup()
                .addGap(347, 347, 347)
                .addComponent(jLabel1)
                .addContainerGap(487, Short.MAX_VALUE))
        );
        pnlSanPhamPageLayout.setVerticalGroup(
            pnlSanPhamPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSanPhamPageLayout.createSequentialGroup()
                .addGap(201, 201, 201)
                .addComponent(jLabel1)
                .addContainerGap(310, Short.MAX_VALUE))
        );

        jplMain.add(pnlSanPhamPage, "card2");

        pnlGioHangPage.setBackground(new java.awt.Color(255, 255, 255));
        pnlGioHangPage.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Gio hàng");

        javax.swing.GroupLayout pnlGioHangPageLayout = new javax.swing.GroupLayout(pnlGioHangPage);
        pnlGioHangPage.setLayout(pnlGioHangPageLayout);
        pnlGioHangPageLayout.setHorizontalGroup(
            pnlGioHangPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGioHangPageLayout.createSequentialGroup()
                .addGap(347, 347, 347)
                .addComponent(jLabel2)
                .addContainerGap(499, Short.MAX_VALUE))
        );
        pnlGioHangPageLayout.setVerticalGroup(
            pnlGioHangPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGioHangPageLayout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(jLabel2)
                .addContainerGap(311, Short.MAX_VALUE))
        );

        jplMain.add(pnlGioHangPage, "card2");

        pnlDonMuaPage.setBackground(new java.awt.Color(255, 255, 255));
        pnlDonMuaPage.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Đơn mua");

        javax.swing.GroupLayout pnlDonMuaPageLayout = new javax.swing.GroupLayout(pnlDonMuaPage);
        pnlDonMuaPage.setLayout(pnlDonMuaPageLayout);
        pnlDonMuaPageLayout.setHorizontalGroup(
            pnlDonMuaPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDonMuaPageLayout.createSequentialGroup()
                .addGap(347, 347, 347)
                .addComponent(jLabel3)
                .addContainerGap(495, Short.MAX_VALUE))
        );
        pnlDonMuaPageLayout.setVerticalGroup(
            pnlDonMuaPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDonMuaPageLayout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(jLabel3)
                .addContainerGap(311, Short.MAX_VALUE))
        );

        jplMain.add(pnlDonMuaPage, "card2");

        pnlTaiKhoanPage.setBackground(new java.awt.Color(255, 255, 255));
        pnlTaiKhoanPage.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("Tài Khoản");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("jLabel5");

        jTextField1.setText("Lê Mạnh Tường");
        jTextField1.setBorder(null);
        jTextField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField1.setEnabled(false);

        jLabel6.setText("jLabel5");

        jTextField2.setText("Lê Mạnh Tường");
        jTextField2.setBorder(null);
        jTextField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField2.setEnabled(false);

        jLabel7.setText("jLabel5");

        jTextField3.setText("Lê Mạnh Tường");
        jTextField3.setBorder(null);
        jTextField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField3.setEnabled(false);

        jButton1.setText("jButton1");

        jButton2.setText("jButton2");

        jButton3.setText("jButton3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel5)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel6)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7)))
                .addGap(62, 62, 62)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlTaiKhoanPageLayout = new javax.swing.GroupLayout(pnlTaiKhoanPage);
        pnlTaiKhoanPage.setLayout(pnlTaiKhoanPageLayout);
        pnlTaiKhoanPageLayout.setHorizontalGroup(
            pnlTaiKhoanPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTaiKhoanPageLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel4)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlTaiKhoanPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTaiKhoanPageLayout.setVerticalGroup(
            pnlTaiKhoanPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTaiKhoanPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlTaiKhoanPage, "card2");

        pnlQL_SanPham.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_SanPham.setPreferredSize(new java.awt.Dimension(940, 540));

        pnlQL_SP_Bottom.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin hàng"));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel8.setText("Mã hàng:");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel9.setText("Tên hàng:");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel10.setText("Thời gian bh:");

        CbLoaiHang.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        CbLoaiHang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DC", "DD", "DL", "DT", "GD", "VT" }));

        jLabel11.setText("Loại Hàng:");

        jLabel36.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel36.setText("Số lượng:");

        jLabel37.setText("Giá tiền:");

        TxtMaHang.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        TxtMaHang.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        TxtMaHang.setEnabled(false);

        TxtTenHang.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        TxtSoLuong.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        TxtGiaTien.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        TxtThoiGianBH.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        BtnChonAnh.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnChonAnh.setText("Chọn ảnh");
        BtnChonAnh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChonAnhActionPerformed(evt);
            }
        });

        jLabel40.setText("Hãng sản xuất:");

        CbHangSanXuat.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel41.setText("Đơn vị tính:");

        TxtDonViTinh.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlQL_SP_BottomLayout = new javax.swing.GroupLayout(pnlQL_SP_Bottom);
        pnlQL_SP_Bottom.setLayout(pnlQL_SP_BottomLayout);
        pnlQL_SP_BottomLayout.setHorizontalGroup(
            pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TxtDonViTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TxtTenHang, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TxtMaHang, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TxtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel36)))
                .addGap(79, 79, 79)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10))
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(CbLoaiHang, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(TxtThoiGianBH, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(BtnChonAnh)
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CbHangSanXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(38, 38, 38)
                                .addComponent(TxtGiaTien, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addComponent(lblLoadAnhSp, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlQL_SP_BottomLayout.setVerticalGroup(
            pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(TxtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQL_SP_BottomLayout.createSequentialGroup()
                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(TxtThoiGianBH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10))
                                        .addGap(18, 18, 18)
                                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel11)
                                            .addComponent(CbLoaiHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                .addComponent(BtnChonAnh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel40)
                                    .addComponent(CbHangSanXuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblLoadAnhSp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32)
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(TxtGiaTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlQL_SP_BottomLayout.createSequentialGroup()
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(TxtMaHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(TxtTenHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlQL_SP_BottomLayout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jLabel39)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlQL_SP_BottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(TxtDonViTinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(89, 89, 89)))
                .addGap(32, 32, 32))
        );

        TableProduct.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        TableProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã hàng", "Mã loại", "Tên hàng", "Đơn vị tính", "Giá tiền", "Thời gian bảo hành", "Số lượng", "Mã NCC", "Hình ảnh"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TableProduct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TableProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableProduct);

        BtnThem.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnThem.setText("Thêm");
        BtnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemActionPerformed(evt);
            }
        });

        BtnSua.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnSua.setText("Sửa");
        BtnSua.setPreferredSize(new java.awt.Dimension(80, 40));
        BtnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuaActionPerformed(evt);
            }
        });

        BtnXoa.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnXoa.setText("Xóa");
        BtnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaActionPerformed(evt);
            }
        });

        BtnReset.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnReset.setText("Clear");
        BtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetActionPerformed(evt);
            }
        });

        BtnThoat.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        BtnThoat.setText("Thoát");
        BtnThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThoatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQL_SanPhamLayout = new javax.swing.GroupLayout(pnlQL_SanPham);
        pnlQL_SanPham.setLayout(pnlQL_SanPhamLayout);
        pnlQL_SanPhamLayout.setHorizontalGroup(
            pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 940, Short.MAX_VALUE)
            .addGroup(pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlQL_SanPhamLayout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addGroup(pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(pnlQL_SP_Bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlQL_SanPhamLayout.createSequentialGroup()
                            .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(BtnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(BtnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(BtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(113, 113, 113)
                            .addComponent(BtnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1))
                    .addContainerGap(37, Short.MAX_VALUE)))
        );
        pnlQL_SanPhamLayout.setVerticalGroup(
            pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
            .addGroup(pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlQL_SanPhamLayout.createSequentialGroup()
                    .addGap(23, 23, 23)
                    .addComponent(pnlQL_SP_Bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addGroup(pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlQL_SanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(BtnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BtnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BtnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(23, 23, 23)))
        );

        jplMain.add(pnlQL_SanPham, "card2");

        pnlQL_HoaDon.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_HoaDon.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setText("Tài Khoản");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setText("jLabel5");

        jTextField7.setText("Lê Mạnh Tường");
        jTextField7.setBorder(null);
        jTextField7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField7.setEnabled(false);

        jLabel14.setText("jLabel5");

        jTextField8.setText("Lê Mạnh Tường");
        jTextField8.setBorder(null);
        jTextField8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField8.setEnabled(false);

        jLabel15.setText("jLabel5");

        jTextField9.setText("Lê Mạnh Tường");
        jTextField9.setBorder(null);
        jTextField9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField9.setEnabled(false);

        jButton7.setText("jButton1");

        jButton8.setText("jButton2");

        jButton9.setText("jButton3");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton7))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel13)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel14)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton7))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel15)))
                .addGap(62, 62, 62)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_HoaDonLayout = new javax.swing.GroupLayout(pnlQL_HoaDon);
        pnlQL_HoaDon.setLayout(pnlQL_HoaDonLayout);
        pnlQL_HoaDonLayout.setHorizontalGroup(
            pnlQL_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_HoaDonLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel12)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_HoaDonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_HoaDonLayout.setVerticalGroup(
            pnlQL_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_HoaDonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_HoaDon, "card2");

        pnlQL_NCC.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_NCC.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setText("Tài Khoản");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setText("jLabel5");

        jTextField10.setText("Lê Mạnh Tường");
        jTextField10.setBorder(null);
        jTextField10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField10.setEnabled(false);

        jLabel18.setText("jLabel5");

        jTextField11.setText("Lê Mạnh Tường");
        jTextField11.setBorder(null);
        jTextField11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField11.setEnabled(false);

        jLabel19.setText("jLabel5");

        jTextField12.setText("Lê Mạnh Tường");
        jTextField12.setBorder(null);
        jTextField12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField12.setEnabled(false);

        jButton10.setText("jButton1");

        jButton11.setText("jButton2");

        jButton12.setText("jButton3");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton10))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel17)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel18)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton10))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel19)))
                .addGap(62, 62, 62)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_NCCLayout = new javax.swing.GroupLayout(pnlQL_NCC);
        pnlQL_NCC.setLayout(pnlQL_NCCLayout);
        pnlQL_NCCLayout.setHorizontalGroup(
            pnlQL_NCCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_NCCLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel16)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_NCCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_NCCLayout.setVerticalGroup(
            pnlQL_NCCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_NCCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_NCC, "card2");

        pnlQL_KhachHang.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_KhachHang.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setText("Tài Khoản");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel21.setText("jLabel5");

        jTextField13.setText("Lê Mạnh Tường");
        jTextField13.setBorder(null);
        jTextField13.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField13.setEnabled(false);

        jLabel22.setText("jLabel5");

        jTextField14.setText("Lê Mạnh Tường");
        jTextField14.setBorder(null);
        jTextField14.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField14.setEnabled(false);

        jLabel23.setText("jLabel5");

        jTextField15.setText("Lê Mạnh Tường");
        jTextField15.setBorder(null);
        jTextField15.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField15.setEnabled(false);

        jButton13.setText("jButton1");

        jButton14.setText("jButton2");

        jButton15.setText("jButton3");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton13))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel21)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel22)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton13))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel23)))
                .addGap(62, 62, 62)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_KhachHangLayout = new javax.swing.GroupLayout(pnlQL_KhachHang);
        pnlQL_KhachHang.setLayout(pnlQL_KhachHangLayout);
        pnlQL_KhachHangLayout.setHorizontalGroup(
            pnlQL_KhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_KhachHangLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel20)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_KhachHangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_KhachHangLayout.setVerticalGroup(
            pnlQL_KhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_KhachHangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_KhachHang, "card2");

        pnlQL_PhieuNhap.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_PhieuNhap.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel24.setText("Tài Khoản");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel25.setText("jLabel5");

        jTextField16.setText("Lê Mạnh Tường");
        jTextField16.setBorder(null);
        jTextField16.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField16.setEnabled(false);

        jLabel26.setText("jLabel5");

        jTextField17.setText("Lê Mạnh Tường");
        jTextField17.setBorder(null);
        jTextField17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField17.setEnabled(false);

        jLabel27.setText("jLabel5");

        jTextField18.setText("Lê Mạnh Tường");
        jTextField18.setBorder(null);
        jTextField18.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField18.setEnabled(false);

        jButton16.setText("jButton1");

        jButton17.setText("jButton2");

        jButton18.setText("jButton3");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton16))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel25)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel26)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton16))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel27)))
                .addGap(62, 62, 62)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_PhieuNhapLayout = new javax.swing.GroupLayout(pnlQL_PhieuNhap);
        pnlQL_PhieuNhap.setLayout(pnlQL_PhieuNhapLayout);
        pnlQL_PhieuNhapLayout.setHorizontalGroup(
            pnlQL_PhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_PhieuNhapLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel24)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_PhieuNhapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_PhieuNhapLayout.setVerticalGroup(
            pnlQL_PhieuNhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_PhieuNhapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_PhieuNhap, "card2");

        pnlQL_ThongKe.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_ThongKe.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel28.setText("Tài Khoản");

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel29.setText("jLabel5");

        jTextField19.setText("Lê Mạnh Tường");
        jTextField19.setBorder(null);
        jTextField19.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField19.setEnabled(false);

        jLabel30.setText("jLabel5");

        jTextField20.setText("Lê Mạnh Tường");
        jTextField20.setBorder(null);
        jTextField20.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField20.setEnabled(false);

        jLabel31.setText("jLabel5");

        jTextField21.setText("Lê Mạnh Tường");
        jTextField21.setBorder(null);
        jTextField21.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField21.setEnabled(false);

        jButton19.setText("jButton1");

        jButton20.setText("jButton2");

        jButton21.setText("jButton3");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton19))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel29)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel30)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton19))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel31)))
                .addGap(62, 62, 62)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_ThongKeLayout = new javax.swing.GroupLayout(pnlQL_ThongKe);
        pnlQL_ThongKe.setLayout(pnlQL_ThongKeLayout);
        pnlQL_ThongKeLayout.setHorizontalGroup(
            pnlQL_ThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_ThongKeLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel28)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_ThongKeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_ThongKeLayout.setVerticalGroup(
            pnlQL_ThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_ThongKeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_ThongKe, "card2");

        pnlQL_NhanVien.setBackground(new java.awt.Color(255, 255, 255));
        pnlQL_NhanVien.setPreferredSize(new java.awt.Dimension(940, 540));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel32.setText("Tài Khoản");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel33.setText("jLabel5");

        jTextField22.setText("Lê Mạnh Tường");
        jTextField22.setBorder(null);
        jTextField22.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField22.setEnabled(false);

        jLabel34.setText("jLabel5");

        jTextField23.setText("Lê Mạnh Tường");
        jTextField23.setBorder(null);
        jTextField23.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField23.setEnabled(false);

        jLabel35.setText("jLabel5");

        jTextField24.setText("Lê Mạnh Tường");
        jTextField24.setBorder(null);
        jTextField24.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField24.setEnabled(false);

        jButton22.setText("jButton1");

        jButton23.setText("jButton2");

        jButton24.setText("jButton3");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(39, 39, 39)
                                .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addComponent(jButton22))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel33)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel34)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton22))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel35)))
                .addGap(62, 62, 62)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(295, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlQL_NhanVienLayout = new javax.swing.GroupLayout(pnlQL_NhanVien);
        pnlQL_NhanVien.setLayout(pnlQL_NhanVienLayout);
        pnlQL_NhanVienLayout.setHorizontalGroup(
            pnlQL_NhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_NhanVienLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel32)
                .addContainerGap(498, Short.MAX_VALUE))
            .addGroup(pnlQL_NhanVienLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQL_NhanVienLayout.setVerticalGroup(
            pnlQL_NhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQL_NhanVienLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jplMain.add(pnlQL_NhanVien, "card2");

        jPanel1.add(jplMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 940, 540));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblCloseMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMenuMouseClicked
        closeMenu();
    }//GEN-LAST:event_lblCloseMenuMouseClicked

    private void lblOpenMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenMenuMouseClicked
        openMenu();
    }//GEN-LAST:event_lblOpenMenuMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        jplSlideMenu.setSize(0, y);
        x = 0;
    }//GEN-LAST:event_formWindowOpened

    private void btnDangXuatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDangXuatMouseClicked
        DangNhap dn = new DangNhap();
        dn.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnDangXuatMouseClicked

    private void btnKH_SanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKH_SanPhamMouseClicked
        resetMainPanel();
        pnlSanPhamPage.setVisible(true);
    }//GEN-LAST:event_btnKH_SanPhamMouseClicked

    private void btnKH_GioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKH_GioHangMouseClicked
        resetMainPanel();
        pnlGioHangPage.setVisible(true);
    }//GEN-LAST:event_btnKH_GioHangMouseClicked

    private void btnKH_DonMuaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKH_DonMuaMouseClicked
        resetMainPanel();
        pnlDonMuaPage.setVisible(true);
    }//GEN-LAST:event_btnKH_DonMuaMouseClicked

    private void btnKH_TaiKhoanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKH_TaiKhoanMouseClicked
        resetMainPanel();
        pnlTaiKhoanPage.setVisible(true);
    }//GEN-LAST:event_btnKH_TaiKhoanMouseClicked

    private void btnQL_SanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_SanPhamMouseClicked
        resetMainPanel();
        pnlQL_SanPham.setVisible(true);
        DisplayPr();
    }//GEN-LAST:event_btnQL_SanPhamMouseClicked

    private void btnQL_HoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_HoaDonMouseClicked
        resetMainPanel();
        pnlQL_HoaDon.setVisible(true);
    }//GEN-LAST:event_btnQL_HoaDonMouseClicked

    private void btnQL_NhaCungCapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_NhaCungCapMouseClicked
        resetMainPanel();
        pnlQL_NCC.setVisible(true);
    }//GEN-LAST:event_btnQL_NhaCungCapMouseClicked

    private void btnQL_KhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_KhachHangMouseClicked
        resetMainPanel();
        pnlQL_KhachHang.setVisible(true);
    }//GEN-LAST:event_btnQL_KhachHangMouseClicked

    private void btnQL_PhieuNhapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_PhieuNhapMouseClicked
        resetMainPanel();
        pnlQL_PhieuNhap.setVisible(true);
    }//GEN-LAST:event_btnQL_PhieuNhapMouseClicked

    private void btnQL_ThongKeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_ThongKeMouseClicked
        resetMainPanel();
        pnlQL_ThongKe.setVisible(true);
    }//GEN-LAST:event_btnQL_ThongKeMouseClicked

    private void btnQL_NhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQL_NhanVienMouseClicked
        resetMainPanel();
        pnlQL_NhanVien.setVisible(true);
    }//GEN-LAST:event_btnQL_NhanVienMouseClicked

    private void BtnChonAnhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChonAnhActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
            int labelWidth = jLabel7.getWidth();
            int labelHeight = jLabel7.getHeight();

            Image image = imageIcon.getImage().getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);

            ImageIcon ScaleImg = new ImageIcon(image);
            jLabel7.setIcon(ScaleImg);

            jTextField1.setText(file.getName());

        }
    }//GEN-LAST:event_BtnChonAnhActionPerformed

    private void TableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableProductMouseClicked
        // TODO add your handling code here:
        TxtMaHang.enable(false);
        editProduct();
    }//GEN-LAST:event_TableProductMouseClicked

    private void BtnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemActionPerformed
        insertDataIntoDatabase();
        DisplayPr();
    }//GEN-LAST:event_BtnThemActionPerformed

    private void BtnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuaActionPerformed
        String newProductName = TxtTenHang.getText();
        String newIdType = CbLoaiHang.getSelectedItem().toString();
        String newDVT = TxtDonViTinh.getText();
        String newGiaTienStr = TxtGiaTien.getText().replaceAll(",", "");
        String newThoiGianBHStr = TxtThoiGianBH.getText();
        String newSoLuongStr = TxtSoLuong.getText();
        String newTenNCC = CbHangSanXuat.getSelectedItem().toString(); // Lấy tên nhà cung cấp từ combobox
        String newHinhAnh = jTextField1.getText();

        // Kiểm tra dữ liệu đầu vào
        if (newProductName.isEmpty() || newIdType.isEmpty() || newDVT.isEmpty() || newGiaTienStr.isEmpty()
                || newThoiGianBHStr.isEmpty() || newSoLuongStr.isEmpty() || newTenNCC.isEmpty() || newHinhAnh.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin vào các trường.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newGiaSP, newTgBH, newSoLuong;
        try {
            newGiaSP = Integer.parseInt(newGiaTienStr);
            newTgBH = Integer.parseInt(newThoiGianBHStr);
            newSoLuong = Integer.parseInt(newSoLuongStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Giá tiền, thời gian bảo hành và số lượng phải là số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = KetNoi.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE db_sanpham SET ma_loai = ?, ten_sp = ?, don_vi_tinh = ?, gia_sp = ?, thoi_gian_bh = ?, soluong = ?, ma_ncc = ?, hinhanh = ? WHERE ma_sp = ?")) {

            // Truy vấn để lấy mã nhà cung cấp từ tên nhà cung cấp
            String query = "SELECT ma_ncc FROM db_nha_cung_cap WHERE ten_ncc = ?";
            PreparedStatement nccStatement = connection.prepareStatement(query);
            nccStatement.setString(1, newTenNCC);
            ResultSet resultSet = nccStatement.executeQuery();

            String maNCC = null;
            if (resultSet.next()) {
                maNCC = resultSet.getString("ma_ncc");
            }

            if (maNCC == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy mã nhà cung cấp cho tên nhà cung cấp đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            statement.setString(1, newIdType);
            statement.setString(2, newProductName);
            statement.setString(3, newDVT);
            statement.setInt(4, newGiaSP);
            statement.setInt(5, newTgBH);
            statement.setInt(6, newSoLuong);
            statement.setString(7, maNCC); // Sử dụng mã nhà cung cấp
            statement.setString(8, newHinhAnh);
            statement.setString(9, TxtMaHang.getText());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Sửa sản phẩm thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                DisplayPr();
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi khi sửa sản phẩm trong cơ sở dữ liệu: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Sửa sản phẩm thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BtnSuaActionPerformed

    private void BtnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaActionPerformed
        // TODO add your handling code here:
        deleteProduct();
        TxtMaHang.setText("");
        TxtTenHang.setText("");
        TxtDonViTinh.setText("");
        TxtGiaTien.setText("");
        TxtThoiGianBH.setText("");
        TxtSoLuong.setText("");
        jTextField1.setText("");
    }//GEN-LAST:event_BtnXoaActionPerformed

    private void BtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnResetActionPerformed
        // TODO add your handling code here:
        TxtMaHang.setText("");
        TxtTenHang.setText("");
        TxtDonViTinh.setText("");
        TxtGiaTien.setText("");
        TxtThoiGianBH.setText("");
        TxtSoLuong.setText("");
        jTextField1.setText("");
        jLabel7.setText("");
        loadHangSanXuat(CbHangSanXuat);
    }//GEN-LAST:event_BtnResetActionPerformed

    private void BtnThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThoatActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_BtnThoatActionPerformed

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
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnChonAnh;
    private javax.swing.JButton BtnReset;
    private javax.swing.JButton BtnSua;
    private javax.swing.JButton BtnThem;
    private javax.swing.JButton BtnThoat;
    private javax.swing.JButton BtnXoa;
    private javax.swing.JComboBox<String> CbHangSanXuat;
    private javax.swing.JComboBox<String> CbLoaiHang;
    private javax.swing.JTable TableProduct;
    private javax.swing.JTextField TxtDonViTinh;
    private javax.swing.JTextField TxtGiaTien;
    private javax.swing.JTextField TxtMaHang;
    private javax.swing.JTextField TxtSoLuong;
    private javax.swing.JTextField TxtTenHang;
    private javax.swing.JTextField TxtThoiGianBH;
    private javax.swing.JLabel avatar;
    private javax.swing.JLabel btnDangXuat;
    private javax.swing.JLabel btnGthieu;
    private javax.swing.JLabel btnHelp;
    private javax.swing.JLabel btnKH_DonMua;
    private javax.swing.JLabel btnKH_GioHang;
    private javax.swing.JLabel btnKH_SanPham;
    private javax.swing.JLabel btnKH_TaiKhoan;
    private javax.swing.JLabel btnQL_HoaDon;
    private javax.swing.JLabel btnQL_KhachHang;
    private javax.swing.JLabel btnQL_NhaCungCap;
    private javax.swing.JLabel btnQL_NhanVien;
    private javax.swing.JLabel btnQL_PhieuNhap;
    private javax.swing.JLabel btnQL_SanPham;
    private javax.swing.JLabel btnQL_ThongKe;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel jplMain;
    private javax.swing.JPanel jplSlideMenu;
    private javax.swing.JPanel jplTitle;
    private javax.swing.JPanel jpllMenuBar;
    private javax.swing.JLabel lblCloseMenu;
    private javax.swing.JLabel lblLoadAnhSp;
    private javax.swing.JLabel lblOpenMenu;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlDonMuaPage;
    private javax.swing.JPanel pnlGioHangPage;
    private javax.swing.JPanel pnlMenuKH;
    private javax.swing.JPanel pnlMenuQL;
    private javax.swing.JPanel pnlQL_HoaDon;
    private javax.swing.JPanel pnlQL_KhachHang;
    private javax.swing.JPanel pnlQL_NCC;
    private javax.swing.JPanel pnlQL_NhanVien;
    private javax.swing.JPanel pnlQL_PhieuNhap;
    private javax.swing.JPanel pnlQL_SP_Bottom;
    private javax.swing.JPanel pnlQL_SanPham;
    private javax.swing.JPanel pnlQL_ThongKe;
    private javax.swing.JPanel pnlSanPhamPage;
    private javax.swing.JPanel pnlTaiKhoanPage;
    private javax.swing.JPanel pnlThongTin;
    // End of variables declaration//GEN-END:variables
}
