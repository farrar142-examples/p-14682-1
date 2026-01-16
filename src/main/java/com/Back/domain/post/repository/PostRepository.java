package com.Back.domain.post.repository;

import com.Back.domain.post.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostRepository extends ElasticsearchRepository<Post,String> {
    Page<Post> findByTitle(String title, Pageable pageable);
    Page<Post> findByContent(String content, Pageable pageable);
    Page<Post> findByAuthor(String author, Pageable pageable);
    Page<Post> findByTitleOrContent(String title, String content, Pageable pageable);
}
