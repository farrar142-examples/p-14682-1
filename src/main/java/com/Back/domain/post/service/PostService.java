package com.Back.domain.post.service;

import com.Back.domain.post.document.Post;
import com.Back.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post create(String title, String content, String author){
        return postRepository.save(new Post(title, content, author));
    }

    public Post findById(String id){
        return postRepository.findById(id).orElseThrow();
    }


    public Page<Post> findAll(String query,String type, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        if (type != null && query == null) {
            query = "";
        }
        return switch (type) {
            case null -> postRepository.findAll(pageRequest);
            case "title" -> postRepository.findByTitle(query, pageRequest);
            case "content" -> postRepository.findByContent(query, pageRequest);
            case "author" -> postRepository.findByAuthor(query, pageRequest);
            case "title_content" -> postRepository.findByTitleOrContent(query, query, pageRequest);
            default -> Page.empty();
        };
    }

    public Post update(String id, String title, String content){
        Post post = postRepository.findById(id).orElseThrow();
        if (title != null) {
            post.setTitle(title);
        }
        if (content != null) {
            post.setContent(content);
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void delete(String id){
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
    }

}
