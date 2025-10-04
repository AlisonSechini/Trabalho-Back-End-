package com.example.microblog.entities;

import com.fasterxml.jackson.annotation.JsonBackReference; // 1. Importe a anotação correta
import jakarta.persistence.*;

@Entity
@Table(name = "curtidas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "post_id"}))
public class Curtida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference // 2. Use @JsonBackReference aqui
    private Post post;

    // Construtor, getters e setters...

    public Curtida() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}