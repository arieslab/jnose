package io.github.arieslab.dtolocal

import java.io.Serializable
import java.util.Date

data class Commit(
    val id: String,
    val authorName: String,
    val date: Date?,
    val msg: String,
    val tag: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
