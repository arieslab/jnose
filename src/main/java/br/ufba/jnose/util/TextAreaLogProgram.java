package br.ufba.jnose.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.text.BadLocationException;

import javax.swing.UIManager.*;

//@startuml
public class TextAreaLogProgram extends JFrame {

    public JTextArea textArea;

    public JTextArea textAreaErrors;

    public JProgressBar progressBar;

    ImageIcon img = new ImageIcon("icon.png");
    private JButton buttonStart = new JButton("Projetos");
    private JButton buttonClear = new JButton("Apagar Log");

    private PrintStream standardOut;

    private PrintStream standardOutErros;

    public TextAreaLogProgram() {
        super("JNose Test");

        try {

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setIconImage(img.getImage());

        textArea = new JTextArea(20, 10);
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.LIGHT_GRAY);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textAreaErrors = new JTextArea(20, 10);
        textAreaErrors.setEditable(false);
        textAreaErrors.setBackground(Color.BLACK);
        textAreaErrors.setForeground(Color.RED);
        textAreaErrors.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textAreaErrors.setLineWrap(true);
        textAreaErrors.setWrapStyleWord(true);

        CustomOutputStream customOutputStreamErrors = new CustomOutputStream(textAreaErrors);
        PrintStream printStreamErrors = new PrintStream(customOutputStreamErrors);
        System.setErr(printStreamErrors);
        standardOutErros = printStreamErrors;

        CustomOutputStream customOutputStream = new CustomOutputStream(textArea);
        PrintStream printStream = new PrintStream(customOutputStream);
        System.setOut(printStream);
        standardOut = printStream;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        add(buttonStart, constraints);

        constraints.gridx = 1;
        add(buttonClear, constraints);

        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        add(progressBar, constraints);


        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(textArea), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(textAreaErrors), constraints);

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                printLog();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    textArea.getDocument().remove(0,
                            textArea.getDocument().getLength());
                    standardOut.println("Text area cleared");
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    private void printLog() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Main.start(standardOut);
//                while (true) {
//                    System.out.println("Time now is " + (new Date()));
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
            }
        });
        thread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TextAreaLogProgram().setVisible(true);
            }
        });
    }
}
