package br.com.casadocodigo.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Produto extends PanacheEntity {
    public String titulo;
    public String descricao;
    public int paginas;
}
