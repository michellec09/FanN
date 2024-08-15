package edu.tec.azuay.faan.service.implementation;

import edu.tec.azuay.faan.event.PostCreatedEvent;
import edu.tec.azuay.faan.exceptions.DuplicatedObjectFoundException;
import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.primary.Animal;
import edu.tec.azuay.faan.persistence.dto.primary.Author;
import edu.tec.azuay.faan.persistence.dto.primary.LikedPost;
import edu.tec.azuay.faan.persistence.dto.primary.SavePost;
import edu.tec.azuay.faan.persistence.entity.Image;
import edu.tec.azuay.faan.persistence.entity.Post;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.persistence.repository.IPostRepository;
import edu.tec.azuay.faan.persistence.repository.IUserRepository;
import edu.tec.azuay.faan.persistence.repository.ImageRepository;
import edu.tec.azuay.faan.persistence.utils.PostState;
import edu.tec.azuay.faan.persistence.utils.PostType;
import edu.tec.azuay.faan.persistence.utils.Role;
import edu.tec.azuay.faan.service.auth.JwtService;
import edu.tec.azuay.faan.service.interfaces.IPostService;
import edu.tec.azuay.faan.service.interfaces.IUploadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements IPostService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final IPostRepository postRepository;

    private final ModelMapper modelMapper;

    private final IUploadService uploadService;

    private final IUserRepository userRepository;

    private static final String FOLDER = "images";

    private final ImageRepository imageRepository;

    private final JwtService jwtService;

    public Post savePostToEntity(SavePost newPost) {
        return modelMapper.map(newPost, Post.class);
    }

    public Animal saveAnimalToEntity(Animal animal) {
        return modelMapper.map(animal, Animal.class);
    }

    @Transactional
    @Override
    public SavePost save(SavePost newPost, MultipartFile file) throws IOException {
        Post post = convertSavePost(newPost);

        if (existsDuplicatePost(post)) {
            throw new DuplicatedObjectFoundException("In the last 60 seconds, a post with the same title already exists");
        }

        if (post.getType().name().equals(PostType.ADOPTION.name())) {
            checkUserAllowedToActionPost(post.getAuthor());
        }

        String imageResponse = uploadService.saveFile(file, FOLDER);
        String imageUrl = uploadService.getUrlFile(imageResponse, FOLDER);

        post.setImagePath(imageResponse);
        post.setImageUrl(imageUrl);

        Post savedPost = postRepository.insert(post);

        newPost.setId(savedPost.getId());
        newPost.setImageUrl(imageUrl);

        applicationEventPublisher.publishEvent(new PostCreatedEvent(newPost));

        return newPost;
    }

    @Transactional
    @Override
    public SavePost updatePostReference(String id, MultipartFile file) throws IOException {
        String photoResponse = uploadService.saveFile(file, FOLDER);
        String imageUrl = !photoResponse.isEmpty() ? uploadService.getUrlFile(photoResponse, FOLDER) : "";

        if (ObjectUtils.isEmpty(photoResponse)) {
            throw new RuntimeException("Image not saved");
        }

        Post post = postRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Post not found"));

        if (existsDuplicatePost(post)) {
            throw new DuplicatedObjectFoundException("In the last 60 seconds, a post with the same title already exists");
        }

        post.setImagePath(photoResponse);
        post.setImageUrl(imageUrl);

        postRepository.save(post);

        return convertPostToSavePost(post);
    }

    @Override
    public SavePost update(SavePost newPost) {
        Post currentPost = postRepository.findById(newPost.getId()).orElseThrow(() -> new ObjectNotFoundException(
            "Post with " + newPost.getId() + " not found.")
        );

        currentPost.setTitle(newPost.getTitle().isEmpty() ? currentPost.getTitle() : newPost.getTitle());
        currentPost.setAdditionalComment(newPost.getAdditionalComment().isEmpty() ? currentPost.getAdditionalComment() : newPost.getAdditionalComment());
        currentPost.setType(newPost.getTypePost().isEmpty() ? currentPost.getType() : PostType.valueOf(newPost.getTypePost()));
        currentPost.setAnimal(ObjectUtils.isEmpty(newPost.getAnimal()) ? currentPost.getAnimal() : newPost.getAnimal());
        currentPost.setLocation(ObjectUtils.isEmpty(newPost.getLocation()) ? currentPost.getLocation() : newPost.getLocation());
        currentPost.setState(newPost.getState().isEmpty() ? currentPost.getState() : PostState.valueOf(newPost.getState()));


        return convertPostToSavePost(postRepository.save(currentPost));
    }

    /**
     * This method retrieves a paginated list of posts with a desc order by creation date.
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects.
     */
    @Override
    public Page<SavePost> getPosts(int pageNumber, int pageSize) {
        return postRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a post by its id.
     *
     * @param id the id of the post to retrieve.
     * @return the Post object with the specified id.
     */
    @Override
    public Post getPostById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("No se encontró la publicación con el id: " + id));
    }

    /**
     * This method deletes a post by its id.
     *
     * @param id the id of the post to delete.
     */
    @Override
    public void deletePost(String id, String token) {

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Post post = getPostById(id);

        checkUserAllowedToActionPost(user, "delete", post);

        postRepository.delete(post);
    }

    /**
     * This method updates an existing post.
     *
     * @param post the SavePost object containing updated post details.
     */
    @Override
    public void updatePost(SavePost post) {
        Post postToUpdate = getPostById(post.getId());

        if (!post.getAuthor().getUsername().equals(postToUpdate.getAuthor().getUsername()) && !postToUpdate.getAuthor().getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("This post isn't your owner, you don't have permissions.");
        }

        postToUpdate.setTitle(post.getTitle().isEmpty() ? postToUpdate.getTitle() : post.getTitle());

        postToUpdate.setAdditionalComment(post.getAdditionalComment().isEmpty() ? postToUpdate.getAdditionalComment() : post.getAdditionalComment());

        postToUpdate.setType(post.getTypePost().isEmpty() ? postToUpdate.getType() : PostType.valueOf(post.getTypePost()));
        postToUpdate.setAnimal(ObjectUtils.isEmpty(post.getAnimal()) ? postToUpdate.getAnimal() : post.getAnimal());
        postToUpdate.setLocation(ObjectUtils.isEmpty(post.getLocation()) ? postToUpdate.getLocation() : post.getLocation());
        postToUpdate.setState(post.getState().isEmpty() ? postToUpdate.getState() : PostState.valueOf(post.getState()));

        postRepository.save(postToUpdate);
    }

    /**
     * This method updates the state of a post.
     *
     * @param id the id of the post to update.
     * @param state the new state to set for the post.
     */
    @Override
    public void updateStatePost(String id, PostState state) {
        Post post = getPostById(id);
        post.setState(state);
        postRepository.save(post);
    }

    /**
     * This method toggles the like status of a post for a user.
     *
     * @param postToLike the LikedPost object containing the post id and user information.
     * @return a string message indicating the result of the operation.
     */
    @Override
    public List<String> likePost(LikedPost postToLike) {
        Post post = getPostById(postToLike.getPostId());
        String status; //Removed "" value From Here - Maybe can cause and error - Or maybe not XD - IDK

        User user = userRepository.findByUsername(postToLike.getAuthor().getUsername())
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        if (post.getLikes() == null || post.getLikes().isEmpty()) {
            post.setLikes(new ArrayList<>());
            post.getLikes().add(user.getId());
            status = "Post liked successfully";
        } else {
            boolean isLiked = post.getLikes().stream()
                    .anyMatch(like -> like.equals(user.getId()));
            if (isLiked) {
                post.getLikes().remove(user.getId());
                status = "Post disliked successfully";
            } else {
                post.getLikes().add(user.getId());
                status = "Post liked successfully";
            }
        }

        Post savedPost =  postRepository.save(post);

        return StringUtils.hasText(status) ? savedPost.getLikes() : new ArrayList<>();
    }

    /**
     * This method retrieves a paginated list of posts by their title.
     * @param title the title of the posts to retrieve.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects with the specified title.
     */
    @Override
    public Page<SavePost> getPostsByTitle(String title, int pageNumber, int pageSize) {
        return postRepository.findByTitleIsContainingIgnoreCase(title,
                        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts liked by a specific author.
     *
     * @param id the id of the author whose liked posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects liked by the specified author.
     */
    @Override
    public Page<SavePost> getLikedPostsByAuthorId(String id, int pageNumber, int pageSize) {
        return postRepository.findPostsLikedByAuthor(id, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt"))).map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their type.
     *
     * @param type the type of the posts to retrieve.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified type.
     */
    @Override
    public Page<SavePost> getPostsByType(PostType type, int pageNumber, int pageSize) {
        return postRepository.findByType(type, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their state.
     *
     * @param state the state of the posts to retrieve.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified state.
     */
    @Override
    public Page<SavePost> getPostsByState(PostState state, int pageNumber, int pageSize) {
        return postRepository.findByState(state, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their type and state.
     *
     * @param type the type of the posts to retrieve.
     * @param state the state of the posts to retrieve.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified type and state.
     */
    @Override
    public Page<SavePost> getPostsByTypeAndState(PostType type, PostState state, int pageNumber, int pageSize) {
        return postRepository.findByTypeAndState(type, state, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by a specific user.
     *
     * @param user the username of the user whose posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects created by the specified user.
     */
    @Override
    public Page<SavePost> getPostsByUser(String user, int pageNumber, int pageSize) {
        return postRepository.findByAuthorUsername(user, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by a specific user id.
     *
     * @param userId the id of the user whose posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects created by the specified user.
     */
    @Override
    public Page<SavePost> getPostsByUserId(String userId, int pageNumber, int pageSize) {
        return postRepository.findByAuthorId(userId, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their type and a specific user.
     *
     * @param type the type of the posts to retrieve.
     * @param user the username of the user whose posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified type created by the specified user.
     */
    @Override
    public Page<SavePost> getPostsByTypeAndUser(PostType type, String user, int pageNumber, int pageSize) {
        return postRepository.findByTypeAndAuthorId(type, user, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their state and a specific user.
     *
     * @param state the state of the posts to retrieve.
     * @param user the username of the user whose posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified state created by the specified user.
     */
    @Override
    public Page<SavePost> getPostsByStateAndUser(PostState state, String user, int pageNumber, int pageSize) {
        return postRepository.findByStateAndAuthorId(state, user, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their type, state, and a specific user.
     *
     * @param type the type of the posts to retrieve.
     * @param state the state of the posts to retrieve.
     * @param user the username of the user whose posts are to be retrieved.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified type and state created by the specified user.
     */
    @Override
    public Page<SavePost> getPostsByTypeAndStateAndUser(PostType type, PostState state, String user, int pageNumber, int pageSize) {
        return postRepository.findByTypeAndStateAndAuthorId(type, state, user, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a paginated list of posts by their type, state, and creation date.
     *
     * @param type the type of the posts to retrieve.
     * @param state the state of the posts to retrieve.
     * @param date the creation date of the posts to retrieve.
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects of the specified type, state, and creation date.
     */
    @Override
    public Page<SavePost> getPostsByTypeAndStateAndDate(PostType type, PostState state, LocalDateTime date, int pageNumber, int pageSize) {
        return postRepository.findByTypeAndStateAndCreateAt(type, state, date, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createAt")))
                .map(this::convertPostToSavePost);
    }

    /**
     * This method retrieves a SavePost object by its id.
     *
     * @param id the id of the SavePost to retrieve.
     * @return the SavePost object with the specified id.
     */
    @Override
    public SavePost getSavePostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Post with id: " + id + " not found")
        );

        return convertPostToSavePost(post);
    }

    /**
     * This method retrieves a paginated list of all posts.
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize the size of the page to retrieve.
     * @return a paginated list of SavePost objects.
     */
    @Override
    public Page<SavePost> getAllPostsToSavePost(int pageNumber, int pageSize) {
        return getPosts(pageNumber, pageSize);
    }

    private void checkUserAllowedToActionPost(User user) {
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("User not allowed to create adoption post");
        }
    }

    private void checkUserAllowedToActionPost(User user, String message, Post post) {
        if (!user.getRole().equals(Role.ADMIN) && !post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException(String.format("User not allowed to %s another post", message));
        }
    }

    private Post convertSavePost(SavePost newPost){
        User user = userRepository.findByUsername(newPost.getAuthor().getUsername()).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Post post = savePostToEntity(newPost);
        post.setAnimal(saveAnimalToEntity(newPost.getAnimal()));
        post.setAuthor(!ObjectUtils.isEmpty(user) ? user : null);

        return post;
    }

    private SavePost convertPostToSavePost(Post post) {
        SavePost savePost = modelMapper.map(post, SavePost.class);
        savePost.setAuthor(modelMapper.map(post.getAuthor(), Author.class));
        savePost.setAnimal(modelMapper.map(post.getAnimal(), Animal.class));
        savePost.setImageUrl(getImageFromPath(post.getImagePath()));

        return savePost;
    }

    @Deprecated(forRemoval = true)
    private String getImageToAuthor(String username)  {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        return getImageFromPath(user.getImagePath());
    }

    private String getImageFromPath(String path) {
        Image image = imageRepository.findByImagePath(path);

        return image.getImageUrl();
    }

    private boolean existsDuplicatePost(Post newPost) {
        String newPostTitle = newPost.getTitle();
        LocalDateTime newPostCreateAt = newPost.getCreateAt();

        long timeDuplicated = 60;

        LocalDateTime limitDateInf = newPostCreateAt.minusSeconds(timeDuplicated);

        Page<Post> duplicatedPosts = postRepository.findByTitleAndCreateAtBetween(newPostTitle, limitDateInf, newPostCreateAt, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createAt")));

        return !duplicatedPosts.isEmpty();
    }

}
