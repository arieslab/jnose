package br.ufba.jnose.util;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }


    @Override
    public void write(int b) throws IOException {
        String out = String.valueOf((char) b);
        if(b!= 50 && b != 91 && b != 27 && b != 59 && b != 52 && b !=109 && b != 49 && b != 51 && b != 93) {
            textArea.append(out);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
