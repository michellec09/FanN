package edu.tec.azuay.faan.persistence.repository;

import edu.tec.azuay.faan.persistence.entity.Post;
import edu.tec.azuay.faan.persistence.utils.PostState;
import edu.tec.azuay.faan.persistence.utils.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IPostRepository extends MongoRepository<Post, String> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleIsContainingIgnoreCase(String title, Pageable pageable);

    Page<Post> findByTitleAndCreateAtBetween(String title, LocalDateTime createAt, LocalDateTime createAtUp, Pageable pageable);

    Page<Post> findByType(PostType Type, Pageable pageable);

    Page<Post> findByState(PostState state, Pageable pageable);

    Page<Post> findByTypeAndState(PostType type, PostState state, Pageable pageable);

    Page<Post> findByAuthorId(String authorId, Pageable pageable);

    @Query("{'likes': { $in: [?0] }}")
    Page<Post> findPostsLikedByAuthor(String authorId, Pageable pageable);

    Page<Post> findByAuthorUsername(String author, Pageable pageable);

    Page<Post> findByTypeAndAuthorId(PostType type, String authorId, Pageable pageable);

    Page<Post> findByStateAndAuthorId(PostState state, String authorId, Pageable pageable);

    Page<Post> findByTypeAndStateAndAuthorId(PostType type, PostState state, String authorId, Pageable pageable);

    Page<Post> findByTypeAndStateAndCreateAt(PostType type, PostState state, LocalDateTime date, Pageable pageable);

}