package ui.login;

import service.AuthService;
import service.AuthServiceImpl;
import ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private boolean succeeded = false;
    private final AuthService auth = new AuthServiceImpl();

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();

        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(lblPassword);
        panel.add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String pw = new String(txtPassword.getPassword());
            if (auth.authenticate(email, pw)) {
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this, "Invalid credentials", "Login failed", JOptionPane.ERROR_MESSAGE);
                succeeded = false;
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel);
        buttons.add(btnLogin);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() { return succeeded; }
}
