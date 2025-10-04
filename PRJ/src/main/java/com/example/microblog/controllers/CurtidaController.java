package com.example.microblog.controllers;

import com.example.microblog.entities.Curtida;
import com.example.microblog.entities.Post;
import com.example.microblog.entities.Usuario;
import com.example.microblog.repositories.CurtidaRepository;
import com.example.microblog.repositories.PostRepository;
import com.example.microblog.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/curtidas")
public class CurtidaController {
    private final CurtidaRepository curtidaRepo;
    private final UsuarioRepository usuarioRepo;
    private final PostRepository postRepo;

    public CurtidaController(CurtidaRepository curtidaRepo, UsuarioRepository usuarioRepo, PostRepository postRepo) {
        this.curtidaRepo = curtidaRepo;
        this.usuarioRepo = usuarioRepo;
        this.postRepo = postRepo;
    }

    @PostMapping
    public ResponseEntity<?> like(@RequestBody Curtida curtida) {
        if (curtida.getUsuario() == null || curtida.getPost() == null
                || curtida.getUsuario().getId() == null || curtida.getPost().getId() == null) {
            return ResponseEntity.badRequest().body("usuario.id e post.id são obrigatórios");
        }

        Usuario u = usuarioRepo.findById(curtida.getUsuario().getId()).orElse(null);
        Post p = postRepo.findById(curtida.getPost().getId()).orElse(null);
        if (u == null || p == null) return ResponseEntity.badRequest().body("usuario ou post não encontrado");

        // avoid duplicate likes
        if (curtidaRepo.findByUsuarioAndPost(u, p).isPresent()) {
            return ResponseEntity.status(409).body("Já curtiu");
        }

        curtida.setUsuario(u);
        curtida.setPost(p);
        Curtida saved = curtidaRepo.save(curtida);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@RequestParam Long usuarioId, @RequestParam Long postId) {
        Usuario u = usuarioRepo.findById(usuarioId).orElse(null);
        Post p = postRepo.findById(postId).orElse(null);
        if (u == null || p == null) return ResponseEntity.badRequest().body("usuario ou post não encontrado");

        return curtidaRepo.findByUsuarioAndPost(u, p)
                .map(c -> { curtidaRepo.delete(c); return ResponseEntity.noContent().build(); })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@RequestParam Long postId) {
        Post p = postRepo.findById(postId).orElse(null);
        if (p == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(curtidaRepo.countByPost(p));
    }
}
