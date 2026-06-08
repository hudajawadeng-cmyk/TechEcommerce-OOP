package techecommerce1.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Track Shipment — visual progress tracker with order lookup.
 */
public class TrackShipmentFrame extends JFrame {

    private JTextField trackingField;
    private JComboBox<String> orderBox;
    private JPanel  timelinePanel;
    private JLabel  statusBadge, etaLabel, carrierLabel, originLabel, destLabel;

    private static final Object[][] SHIPMENTS = {
            {"ORD-2026-003","TRK-9182736450","FedEx",  "Shanghai, CN",    "San Francisco, CA",2,"In Transit",      "2026-06-10"},
            {"ORD-2026-001","TRK-1029384756","DHL",    "Shenzhen, CN",    "New York, NY",     4,"Delivered",       "2026-05-12"},
            {"ORD-2026-004","TRK-5647382910","UPS",    "Austin, TX",      "Chicago, IL",      1,"Dispatched",      "2026-06-14"},
            {"ORD-2026-002","TRK-8374651029","FedEx",  "Los Angeles, CA", "Boston, MA",       3,"Out for Delivery","2026-06-06"},
            {"ORD-2026-005","TRK-2938475610","Aramex", "Dubai, UAE",      "Benghazi, LY",     0,"Processing",      "2026-06-18"},
    };

    private static final String[] STEPS       = {"Processing","Dispatched","In Transit","Out for Delivery","Delivered"};
    private static final String[] STEP_ICONS  = {"📋","📦","✈","🚚","✅"};
    private static final String[] STEP_DESC   = {
            "Order confirmed\n& being packed",
            "Handed to\ncarrier",
            "En route to\ndestination",
            "Out for delivery\nin your area",
            "Package\ndelivered"
    };

    private int currentStep = 2;

    public TrackShipmentFrame() {
        setTitle("TechCommerce — Track Shipment");
        setSize(760, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadShipment(0);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // HEADER
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("🚚  Track Your Shipment");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(100, 180, 255));
        header.add(title, BorderLayout.WEST);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setOpaque(false);

        JLabel orderLbl = new JLabel("Order:");
        orderLbl.setForeground(TEXT_DIM);
        orderLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));

        String[] orderIds = new String[SHIPMENTS.length];
        for (int i = 0; i < SHIPMENTS.length; i++) orderIds[i] = (String) SHIPMENTS[i][0];
        orderBox = new JComboBox<>(orderIds);
        orderBox.setBackground(new Color(22, 28, 48));
        orderBox.setForeground(TEXT_MAIN);
        orderBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        orderBox.addActionListener(e -> loadShipment(orderBox.getSelectedIndex()));

        JLabel trackLbl = new JLabel("  Tracking #:");
        trackLbl.setForeground(TEXT_DIM);
        trackLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));

        trackingField = new JTextField(14);
        trackingField.setBackground(new Color(22, 28, 48));
        trackingField.setForeground(TEXT_MAIN);
        trackingField.setCaretColor(ACCENT);
        trackingField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        trackingField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        trackingField.setEditable(false);

        searchRow.add(orderLbl);
        searchRow.add(orderBox);
        searchRow.add(trackLbl);
        searchRow.add(trackingField);
        header.add(searchRow, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        // Info cards
        JPanel infoRow = new JPanel(new GridLayout(1, 4, 12, 0));
        infoRow.setOpaque(false);
        statusBadge  = new JLabel("", SwingConstants.LEFT);
        etaLabel     = new JLabel("", SwingConstants.LEFT);
        carrierLabel = new JLabel("", SwingConstants.LEFT);
        originLabel  = new JLabel("", SwingConstants.LEFT);
        infoRow.add(infoCard("Status",  statusBadge,  new Color(0,200,255)));
        infoRow.add(infoCard("ETA",     etaLabel,     new Color(0,200,130)));
        infoRow.add(infoCard("Carrier", carrierLabel, new Color(180,130,255)));
        infoRow.add(infoCard("Origin",  originLabel,  new Color(255,160,0)));
        center.add(infoRow, BorderLayout.NORTH);

        // Timeline
        timelinePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTimeline((Graphics2D) g);
            }
        };
        timelinePanel.setOpaque(false);
        timelinePanel.setPreferredSize(new Dimension(0, 210));

        // Events panel
        JPanel eventsCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14,18,32));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        eventsCard.setOpaque(false);
        eventsCard.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        JLabel evTitle = new JLabel("📍  Shipment Events");
        evTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        evTitle.setForeground(TEXT_DIM);
        evTitle.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        eventsCard.add(evTitle, BorderLayout.NORTH);
        destLabel = new JLabel();
        destLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        destLabel.setForeground(TEXT_MAIN);
        eventsCard.add(destLabel, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, timelinePanel, eventsCard);
        split.setDividerLocation(210);
        split.setDividerSize(4);
        split.setOpaque(false);
        split.setBorder(null);
        split.setBackground(BG_DARK);
        center.add(split, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        // BOTTOM buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnRow.setOpaque(false);

        JButton refreshBtn = makeAccentButton("🔄  Refresh", new Color(0,160,200), BG_DARK);
        refreshBtn.addActionListener(e -> {
            loadShipment(orderBox.getSelectedIndex());
            JOptionPane.showMessageDialog(this,"Tracking info updated!","Refreshed",JOptionPane.INFORMATION_MESSAGE);
        });

        JButton contactBtn = makeAccentButton("📞  Contact Support", new Color(100,80,220), BG_DARK);
        contactBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Support Email: support@techcommerce.com\nPhone: +1-800-TECH-123",
                "Contact Support", JOptionPane.INFORMATION_MESSAGE));

        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(refreshBtn);
        btnRow.add(contactBtn);
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    private JPanel infoCard(String label, JLabel valueLbl, Color accent) {
        JPanel card = new JPanel(new GridLayout(2,1,0,4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,24,42));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(accent);
                g2.fillRoundRect(0,0,4,getHeight(),4,4);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10,16,10,10));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(TEXT_DIM);
        valueLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        valueLbl.setForeground(accent);
        card.add(lbl);
        card.add(valueLbl);
        return card;
    }

    private void drawTimeline(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = timelinePanel.getWidth();
        int h = timelinePanel.getHeight();
        if (w == 0) return;

        int n = STEPS.length;
        int margin = 70;
        int stepW  = (w - 2*margin) / (n-1);
        int cy = h/2 - 15;
        int r  = 22;

        // Background line
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(40,50,80));
        g2.drawLine(margin, cy, margin + stepW*(n-1), cy);

        // Progress line
        if (currentStep > 0) {
            GradientPaint gp = new GradientPaint(
                    margin, cy, new Color(0,160,220),
                    margin + stepW*Math.min(currentStep,n-1), cy, new Color(0,220,150));
            g2.setPaint(gp);
            g2.drawLine(margin, cy, margin + stepW*Math.min(currentStep,n-1), cy);
        }

        for (int i = 0; i < n; i++) {
            int cx   = margin + stepW*i;
            boolean done    = i < currentStep;
            boolean current = i == currentStep;
            boolean future  = i > currentStep;

            // Glow
            if (current) {
                g2.setColor(new Color(0,200,255,35));
                g2.fillOval(cx-r-10, cy-r-10, (r+10)*2, (r+10)*2);
            }

            // Fill
            if (done) {
                g2.setColor(new Color(0,180,130));
            } else if (current) {
                GradientPaint cp = new GradientPaint(cx-r,cy-r,new Color(0,180,255),cx+r,cy+r,new Color(0,100,220));
                g2.setPaint(cp);
            } else {
                g2.setColor(new Color(25,32,55));
            }
            g2.setStroke(new BasicStroke(1));
            g2.fillOval(cx-r, cy-r, r*2, r*2);

            // Border
            g2.setStroke(new BasicStroke(2));
            g2.setColor(done?new Color(0,200,140):current?new Color(0,200,255):new Color(50,65,100));
            g2.drawOval(cx-r, cy-r, r*2, r*2);

            // Icon
            g2.setColor(future ? new Color(70,90,130) : Color.WHITE);
            String icon = done ? "✓" : STEP_ICONS[i];
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, current?16:13));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(icon, cx - fm.stringWidth(icon)/2, cy + fm.getAscent()/2 - 2);

            // Label above
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            fm = g2.getFontMetrics();
            g2.setColor(done?new Color(0,200,140):current?new Color(0,200,255):new Color(80,100,140));
            String name = STEPS[i];
            g2.drawString(name, cx - fm.stringWidth(name)/2, cy-r-10);

            // Desc below
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            fm = g2.getFontMetrics();
            g2.setColor(future ? new Color(50,70,110) : new Color(120,150,190));
            String[] lines = STEP_DESC[i].split("\n");
            for (int li=0; li<lines.length; li++)
                g2.drawString(lines[li], cx-fm.stringWidth(lines[li])/2, cy+r+16+li*13);
        }
    }

    private void loadShipment(int idx) {
        if (idx < 0 || idx >= SHIPMENTS.length) return;
        Object[] s = SHIPMENTS[idx];
        String carrier = (String)s[2];
        String origin  = (String)s[3];
        String dest    = (String)s[4];
        currentStep    = (int)s[5];
        String status  = (String)s[6];
        String eta     = (String)s[7];

        trackingField.setText((String)s[1]);
        carrierLabel.setText(carrier);
        originLabel.setText(origin);
        etaLabel.setText(eta);
        statusBadge.setText(status);

        switch (status) {
            case "Delivered":        statusBadge.setForeground(SUCCESS_C); break;
            case "Out for Delivery": statusBadge.setForeground(new Color(0,220,255)); break;
            case "In Transit":       statusBadge.setForeground(new Color(100,180,255)); break;
            case "Dispatched":       statusBadge.setForeground(new Color(180,130,255)); break;
            default:                 statusBadge.setForeground(new Color(255,200,50));
        }

        // Build events HTML
        String[][] events = {
                {"2026-06-03 09:00","Order placed and payment confirmed","🏪 "+dest},
                {"2026-06-03 14:30","Order packed and ready for pickup", "🏪 "+dest},
                {"2026-06-04 08:00","Picked up by "+carrier,            "📦 "+origin},
                {"2026-06-04 20:00","Departed origin facility",          "✈  "+origin},
                {"2026-06-05 11:00","Arrived at sorting hub",            "🏭 Transit Hub"},
                {"2026-06-06 07:00","Out for delivery",                  "🚚 "+dest},
                {"2026-06-06 14:22","Delivered — signed by recipient",   "✅ "+dest},
        };
        int showUntil = Math.min(currentStep*2+2, events.length);
        StringBuilder html = new StringBuilder("<html><table cellpadding='3'>");
        for (int i = showUntil-1; i >= 0; i--) {
            String col = (i == showUntil-1) ? "#00C8FF" : "#607090";
            html.append(String.format(
                    "<tr><td style='color:#405070;font-size:10px;white-space:nowrap;'>%s</td>"
                            +"<td style='color:%s;font-size:11px;padding-left:12px;'>%s</td>"
                            +"<td style='color:#304060;font-size:10px;padding-left:12px;'>%s</td></tr>",
                    events[i][0], col, events[i][1], events[i][2]));
        }
        html.append("</table></html>");
        destLabel.setText(html.toString());

        timelinePanel.repaint();
    }
}
