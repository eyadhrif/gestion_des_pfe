package ui.main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NavigationController {
    private final JPanel contentView;
    private final CardLayout cardLayout;
    private final Map<String, JPanel> panels = new HashMap<>();

    public NavigationController(JPanel contentView) {
        this.contentView = contentView;
        this.cardLayout = (CardLayout) contentView.getLayout();
    }

    public void addPanel(String name, JPanel panel) {
        panels.put(name, panel);
        contentView.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(contentView, name);
    }
}