package com.itgm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Avaliacao.
 */
@Entity
@Table(name = "avaliacao")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Avaliacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "status")
    private Integer status;

    @Size(max = 1000000)
    @Column(name = "codigo", length = 1000000)
    private String codigo;

    @Column(name = "resultado")
    private String resultado;

    @OneToOne(mappedBy = "avaliacao")
    @JsonIgnore
    private Prognose prognose;

    @OneToMany(mappedBy = "avaliacao")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ModeloExclusivo> modelosavaliados = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public Avaliacao nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getStatus() {
        return status;
    }

    public Avaliacao status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCodigo() {
        return codigo;
    }

    public Avaliacao codigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getResultado() {
        return resultado;
    }

    public Avaliacao resultado(String resultado) {
        this.resultado = resultado;
        return this;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Prognose getPrognose() {
        return prognose;
    }

    public Avaliacao prognose(Prognose prognose) {
        this.prognose = prognose;
        return this;
    }

    public void setPrognose(Prognose prognose) {
        this.prognose = prognose;
    }

    public Set<ModeloExclusivo> getModelosavaliados() {
        return modelosavaliados;
    }

    public Avaliacao modelosavaliados(Set<ModeloExclusivo> modeloExclusivos) {
        this.modelosavaliados = modeloExclusivos;
        return this;
    }

    public Avaliacao addModelosavaliados(ModeloExclusivo modeloExclusivo) {
        this.modelosavaliados.add(modeloExclusivo);
        modeloExclusivo.setAvaliacao(this);
        return this;
    }

    public Avaliacao removeModelosavaliados(ModeloExclusivo modeloExclusivo) {
        this.modelosavaliados.remove(modeloExclusivo);
        modeloExclusivo.setAvaliacao(null);
        return this;
    }

    public void setModelosavaliados(Set<ModeloExclusivo> modeloExclusivos) {
        this.modelosavaliados = modeloExclusivos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Avaliacao avaliacao = (Avaliacao) o;
        if (avaliacao.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
            "id=" + id +
            ", nome='" + nome + "'" +
            ", status='" + status + "'" +
            ", codigo='" + codigo + "'" +
            ", resultado='" + resultado + "'" +
            '}';
    }
}
