package com.Back;

import com.Back.domain.post.document.Post;
import com.Back.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackApplicationTests {
	@Autowired
	private PostRepository postRepository;

	@Test
	@Order(1)
	@DisplayName("PostRepository 생성, 조회, 수정, 삭제 테스트")
	void t1(){
		Post post = postRepository.save(new Post(
				"첫 번째 게시글",
				"첫 번째 게시글 내용",
				"작성자1"
		));
		assert post.getId() != null;
		Post fetchedPost = postRepository.findById(post.getId()).orElseThrow();
		assert fetchedPost.getTitle().equals(post.getTitle());
	}

}
