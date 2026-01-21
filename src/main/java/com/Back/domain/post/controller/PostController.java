package com.Back.domain.post.controller;

import com.Back.domain.post.document.Post;
import com.Back.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody CreatePostRequest request) {
        Post post = postService.create(request.title(), request.content(), request.author());
        return ResponseEntity.created(null).body(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable String id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.findAll(query, type, page, size);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable String id, @RequestBody UpdatePostRequest request) {
        Post post = postService.update(id, request.title(), request.content());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreatePostRequest(String title, String content, String author) {}
    public record UpdatePostRequest(String title, String content) {}
}
