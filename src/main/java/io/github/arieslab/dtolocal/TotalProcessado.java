package io.github.arieslab.dtolocal;

import java.io.Serializable;

public class TotalProcessado implements Serializable {
    private static final long serialVersionUID = 1L;

    private int valor;

    /**
     * Creates a TotalProcessado initialized to 0.
     */
    public TotalProcessado() {
        this.valor = 0;
    }

    /**
     * Returns the current progress value.
     *
     * @return the progress value
     */
    public int getValor() {
        return valor;
    }

    /**
     * Sets the progress value.
     *
     * @param valor the progress value
     */
    public void setValor(int valor) {
        this.valor = valor;
    }
}
