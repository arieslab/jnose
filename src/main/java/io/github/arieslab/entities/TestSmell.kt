package io.github.arieslab.entities

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
class TestSmell : Serializable {

    @Id
    @GeneratedValue
    var id: Long? = null

    var nome: String = ""

    var pathTestClass: String = ""

    var pathProductionClass: String = ""

    var method: String = ""

    var begin: String = ""

    var end: String = ""

    @ManyToOne
    var projeto: Projeto? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestSmell) return false
        return id == other.id
    }

    override fun hashCode() = Objects.hash(id)

    override fun toString() = "TestSmell{id=$id, nome='$nome', pathTestClass='$pathTestClass', pathProductionClass='$pathProductionClass', method='$method', begin='$begin', end='$end', projeto=$projeto}"
}
