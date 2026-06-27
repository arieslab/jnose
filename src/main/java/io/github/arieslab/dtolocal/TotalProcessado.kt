package io.github.arieslab.dtolocal

import java.io.Serializable

class TotalProcessado : Serializable {
    var valor: Int = 0
        private set

    fun setValor(valor: Int) {
        this.valor = valor
    }
}
