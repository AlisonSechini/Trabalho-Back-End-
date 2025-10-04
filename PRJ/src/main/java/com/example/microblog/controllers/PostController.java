package com.example.microblog.controllers;

import com.example.microblog.entities.Curtida;
import com.example.microblog.entities.Post;
import com.example.microblog.entities.Usuario;
import com.example.microblog.repositories.CurtidaRepository;
import com.example.microblog.repositories.PostRepository;
import com.example.microblog.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepo;
    private final UsuarioRepository usuarioRepo;
    private final CurtidaRepository curtidaRepo;

    @Autowired
    public PostController(PostRepository postRepo, UsuarioRepository usuarioRepo, CurtidaRepository curtidaRepo) {
        this.postRepo = postRepo;
        this.usuarioRepo = usuarioRepo;
        this.curtidaRepo = curtidaRepo;
    }

    @GetMapping
    public List<Post> all() {
        return postRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable Long id) {
        return postRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cria um post com um JSON como: { "conteudo":"texto", "usuario": { "id": 1 } }
    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        if (post.getUsuario() != null && post.getUsuario().getId() != null) {
            Usuario u = usuarioRepo.findById(post.getUsuario().getId()).orElse(null);
            if (u == null) {
                return ResponseEntity.badRequest().build();
            }
            post.setUsuario(u);
        } else {
            return ResponseEntity.badRequest().build();
        }
        Post saved = postRepo.save(post);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!postRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        postRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relevancia")
    public ResponseEntity<List<Post>> getPostsPorRelevancia() {
        List<Post> posts = postRepo.findPostsOrderByCurtidasDesc();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/curtir/{userId}")
    public ResponseEntity<Void> curtirPost(@PathVariable Long postId, @PathVariable Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Curtida novaCurtida = new Curtida();
        novaCurtida.setPost(post);
        novaCurtida.setUsuario(usuario);

        curtidaRepo.save(novaCurtida);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}/descurtir/{userId}")
    public ResponseEntity<Void> descurtirPost(@PathVariable Long postId, @PathVariable Long userId) {
        // Usa o método que criamos no repositório para encontrar a curtida
        Curtida curtida = curtidaRepo.findByUsuarioIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curtida não encontrada"));

        // Se encontrou a curtida, deleta
        curtidaRepo.delete(curtida);

        return ResponseEntity.noContent().build();
    }
}