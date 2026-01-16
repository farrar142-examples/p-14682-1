package com.Back.domain.post.repository;

import com.Back.domain.post.document.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostRepository extends ElasticsearchRepository<Post,String> {
}
