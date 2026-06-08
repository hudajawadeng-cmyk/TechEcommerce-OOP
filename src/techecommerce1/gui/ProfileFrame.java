package techecommerce1.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import static techecommerce1.gui.LoginFrame.*;

/**
 * My Profile – view and edit account details.
 */
public class ProfileFrame extends JFrame {

    private JTextField nameField, emailField, phoneField, addressField;
    private JLabel avatarLabel;
    private JComboBox<String> countryBox;
    private String userEmail;

    public ProfileFrame(String userEmail) {
        this.userEmail = userEmail;
        setTitle("TechCommerce — My Profile");
        setSize(540, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setBackground(BG_DARK);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // ── Avatar header
        JPanel avatarPanel = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0,160,220,180), getWidth(), 0, new Color(80,40,200,180));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Avatar circle
        avatarLabel = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,200,255));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(BG_DARK);
                g2.setFont(new Font("Monospaced", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                String initials = userEmail.length() > 0 ? String.valueOf(userEmail.charAt(0)).toUpperCase() : "?";
                g2.drawString(initials, (getWidth()-fm.stringWidth(initials))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        avatarLabel.setPreferredSize(new Dimension(72, 72));

        JPanel userInfo = new JPanel(new GridLayout(3,1, 0, 3));
        userInfo.setOpaque(false);
        JLabel nameLbl = new JLabel("Account Profile");
        nameLbl.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLbl.setForeground(Color.WHITE);
        JLabel emailLbl = new JLabel(userEmail);
        emailLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailLbl.setForeground(new Color(200, 230, 255));
        JLabel memberLbl = new JLabel("Member since: May 2025");
        memberLbl.setFont(new Font("Monospaced", Font.ITALIC, 11));
        memberLbl.setForeground(new Color(160, 200, 240));
        userInfo.add(nameLbl);
        userInfo.add(emailLbl);
        userInfo.add(memberLbl);

        avatarPanel.add(avatarLabel, BorderLayout.WEST);
        avatarPanel.add(userInfo, BorderLayout.CENTER);
        root.add(avatarPanel, BorderLayout.NORTH);

        // ── Form card
        JPanel formCard = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,22,38));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(20,22,20,22));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 0, 7, 10);

        String[] labels = {"Full Name", "Email", "Phone", "Country", "Address"};
        nameField    = darkField(); nameField.setText("John Doe");
        emailField   = darkField(); emailField.setText(userEmail);
        phoneField   = darkField(); phoneField.setText("+1 555 000 1234");
        countryBox   = new JComboBox<>(new String[]{"United States","Saudi Arabia","UAE","Egypt","Libya","Other"});
        countryBox.setBackground(new Color(25,32,55));
        countryBox.setForeground(TEXT_MAIN);
        countryBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        addressField = darkField(); addressField.setText("123 Tech Street, San Francisco, CA");

        Component[] fields = {nameField, emailField, phoneField, countryBox, addressField};

        for (int i = 0; i < labels.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.35;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Monospaced", Font.PLAIN, 11));
            l.setForeground(TEXT_DIM);
            formCard.add(l, gc);
            gc.gridx=1; gc.weightx=0.65;
            formCard.add(fields[i], gc);
        }

        // Password section
        gc.gridx=0; gc.gridy=5; gc.gridwidth=2; gc.insets=new Insets(14,0,4,0);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        formCard.add(sep, gc);

        gc.gridy=6; gc.gridwidth=1; gc.insets=new Insets(7,0,7,10);
        JLabel pwLbl = new JLabel("New Password");
        pwLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pwLbl.setForeground(TEXT_DIM);
        gc.gridx=0; gc.weightx=0.35;
        formCard.add(pwLbl, gc);
        gc.gridx=1; gc.weightx=0.65;
        JPasswordField pwField = new JPasswordField();
        styleField(pwField);
        formCard.add(pwField, gc);

        root.add(formCard, BorderLayout.CENTER);

        // ── Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnRow.setOpaque(false);

        JButton saveBtn = makeAccentButton("💾  Save Changes", ACCENT, BG_DARK);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,"Profile updated successfully! ✔","Saved",JOptionPane.INFORMATION_MESSAGE);
        });

        JButton pwBtn = makeGhostButton("🔐 Change PW");
        pwBtn.setPreferredSize(new Dimension(140, 36));
        pwBtn.addActionListener(e -> {
            if (new String(pwField.getPassword()).trim().isEmpty())
                JOptionPane.showMessageDialog(this,"Enter a new password first.","Warning",JOptionPane.WARNING_MESSAGE);
            else
                JOptionPane.showMessageDialog(this,"Password changed successfully! 🔐","Saved",JOptionPane.INFORMATION_MESSAGE);
        });

        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(saveBtn);
        btnRow.add(pwBtn);
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    private JTextField darkField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(25,32,55));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5,8,5,8)));
    }
}
