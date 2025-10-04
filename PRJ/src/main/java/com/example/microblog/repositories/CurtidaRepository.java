package com.example.microblog.repositories;

import com.example.microblog.entities.Curtida;
import com.example.microblog.entities.Post;
import com.example.microblog.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {
    Optional<Curtida> findByUsuarioAndPost(Usuario usuario, Post post);
    long countByPost(Post post);

    Optional<Curtida> findByUsuarioIdAndPostId(Long usuarioId, Long postId);

}

