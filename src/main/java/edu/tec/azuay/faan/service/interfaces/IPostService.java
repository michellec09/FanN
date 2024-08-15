package edu.tec.azuay.faan.service.interfaces;

import edu.tec.azuay.faan.persistence.dto.primary.LikedPost;
import edu.tec.azuay.faan.persistence.dto.primary.SavePost;
import edu.tec.azuay.faan.persistence.entity.Post;
import edu.tec.azuay.faan.persistence.utils.PostState;
import edu.tec.azuay.faan.persistence.utils.PostType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface IPostService {

    SavePost save(SavePost newPost, MultipartFile file) throws IOException;

    SavePost updatePostReference(String id, MultipartFile file) throws IOException;

    SavePost update(SavePost newPost);

    Page<SavePost> getPosts(int pageNumber, int pageSize);

    Post getPostById(String id);

    void deletePost(String id, String token);

    void updatePost(SavePost post);

    void updateStatePost(String id, PostState state);

    List<String> likePost(LikedPost likedPost);

    Page<SavePost> getPostsByTitle(String title, int pageNumber, int pageSize);

    Page<SavePost> getLikedPostsByAuthorId(String id, int pageNumber, int pageSize);

    Page<SavePost> getPostsByType(PostType type, int pageNumber, int pageSize);

    Page<SavePost> getPostsByState(PostState state, int pageNumber, int pageSize);

    Page<SavePost> getPostsByTypeAndState(PostType type, PostState state, int pageNumber, int pageSize);

    Page<SavePost> getPostsByUser(String user, int pageNumber, int pageSize);

    Page<SavePost> getPostsByUserId(String userId, int pageNumber, int pageSize);

    Page<SavePost> getPostsByTypeAndUser(PostType type, String user, int pageNumber, int pageSize);

    Page<SavePost> getPostsByStateAndUser(PostState state, String user, int pageNumber, int pageSize);

    Page<SavePost> getPostsByTypeAndStateAndUser(PostType type, PostState state, String user, int pageNumber, int pageSize);

    Page<SavePost> getPostsByTypeAndStateAndDate(PostType type, PostState state, LocalDateTime date, int pageNumber, int pageSize);

    SavePost getSavePostById(String id);

     Page<SavePost> getAllPostsToSavePost(int pageNumber, int pageSize);
}
