package com.Back;

import com.Back.domain.post.document.Post;
import com.Back.domain.post.repository.PostRepository;
import com.Back.domain.post.service.PostService;
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

	@Autowired
	private PostService postService;

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

	@Test
	@Order(3)
	@DisplayName("PostService 테스트")
	void t3(){
		// 1. 게시글 생성 테스트
		Post createdPost = postService.create("서비스 테스트 제목", "서비스 테스트 내용", "테스트작성자");
		assertNotEquals(null, createdPost.getId());
		assertEquals("서비스 테스트 제목", createdPost.getTitle());
		assertEquals("서비스 테스트 내용", createdPost.getContent());
		assertEquals("테스트작성자", createdPost.getAuthor());

		// 2. ID로 조회 테스트
		Post foundPost = postService.findById(createdPost.getId());
		assertEquals(createdPost.getId(), foundPost.getId());
		assertEquals(createdPost.getTitle(), foundPost.getTitle());

		// 3. 전체 조회 테스트 (페이징)
		postService.create("두 번째 게시글", "두 번째 내용", "작성자2");
		postService.create("세 번째 게시글", "세 번째 내용", "작성자3");
		Page<Post> allPosts = postService.findAll(null,null,0, 10);
		assertEquals(3, allPosts.getTotalElements());

		// 4. 검색 테스트 - title
		Page<Post> searchByTitle = postService.findAll("서비스", "title", 0, 10);
		assertEquals(1, searchByTitle.getTotalElements());

		// 5. 검색 테스트 - content
		Page<Post> searchByContent = postService.findAll("두 번째", "content", 0, 10);
		assertEquals(1, searchByContent.getTotalElements());

		// 6. 검색 테스트 - author
		Page<Post> searchByAuthor = postService.findAll("테스트작성자", "author", 0, 10);
		assertEquals(1, searchByAuthor.getTotalElements());

		// 7. 검색 테스트 - title_content
		Page<Post> searchByTitleOrContent = postService.findAll("게시글", "title_content", 0, 10);
		assertEquals(2, searchByTitleOrContent.getTotalElements());

		// 8. 게시글 수정 테스트
		Post updatedPost = postService.update(createdPost.getId(), "수정된 제목", "수정된 내용");
		assertEquals("수정된 제목", updatedPost.getTitle());
		assertEquals("수정된 내용", updatedPost.getContent());

		// 9. 게시글 삭제 테스트
		postService.delete(createdPost.getId());
		Page<Post> afterDelete = postService.findAll(null,null,0, 10);
		assertEquals(2, afterDelete.getTotalElements());
	}

}
