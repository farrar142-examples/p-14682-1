package com.Back;

import com.Back.domain.post.document.Post;
import com.Back.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackApplicationTests {
	@Autowired
	private PostRepository postRepository;

	@BeforeEach
	void cleanUp(){
		postRepository.deleteAll();
	}

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

	@Test
	@Order(2)
	@DisplayName("BiGram 분석기 테스트")
	void t2(){
		postRepository.save(new Post(
				"Spring Data Elasticsearch",
				"Elasticsearch와 Spring Data Elasticsearch를 사용한 예제입니다.",
				"작성자2"
		));
		postRepository.save(new Post(
				"Spring Data JPA",
				"JPA와 Spring Data JPA를 사용한 예제입니다.",
				"작성자3"
		));
		// Case 1. 부분검색 - 성공
		Page<Post> searchResult = postRepository.findByTitle("search", PageRequest.of(1,10));
		assertEquals(1, searchResult.getTotalElements());
		// Case 2. 띄어쓰기 제외 검색 - 성공
		searchResult = postRepository.findByTitle("Elastic search", PageRequest.of(1,10));
		assertEquals(1, searchResult.getTotalElements());
		// Case 3. 띄어쓰기 포함 검색 - 성공
		searchResult = postRepository.findByTitle("SpringData", PageRequest.of(1,10));
		assertEquals(2, searchResult.getTotalElements());
		// Case 4. 1글자 검색 - 실패 - BiGram 분석기는 2글자 단위로 토큰화 하기 때문에 1글자 검색은 불가능
		searchResult = postRepository.findByTitle("S", PageRequest.of(1,10));
		assertEquals(0, searchResult.getTotalElements());
		// Case 5. 한영 혼용 테스트 - 성공
		searchResult = postRepository.findByContent("Elasticsearch와 Spring", PageRequest.of(1,10));
		assertEquals(1, searchResult.getTotalElements());
	}

}
