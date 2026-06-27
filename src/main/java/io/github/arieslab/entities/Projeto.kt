package io.github.arieslab.entities

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
class Projeto : Serializable {

    @Id
    @GeneratedValue
    var id: Long? = null

    var name: String = ""

    var path: String = ""

    var url: String = ""

    var stars: Int = 0

    var junitVersion: String = ""

    var dateUpdate: Date? = null

    @OneToMany
    var testSmell: List<TestSmell>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Projeto) return false
        return id == other.id
    }

    override fun hashCode() = Objects.hash(id)

    override fun toString() = "Projeto{id=$id, name='$name', path='$path', url='$url', stars=$stars, junitVersion='$junitVersion'}"
}
