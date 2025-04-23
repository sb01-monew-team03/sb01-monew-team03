package team03.monew.service.comments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import team03.monew.dto.comments.CommentCreateRequest;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.dto.comments.CommentUpdateRequest;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.article.Article;
import team03.monew.entity.user.User;
import team03.monew.entity.user.User.Role;
import team03.monew.mapper.comments.CommentMapper;
import team03.monew.repository.article.ArticleRepository;
import team03.monew.repository.comments.CommentLikeRepository;
import team03.monew.repository.comments.CommentRepository;
import team03.monew.repository.user.UserRepository;
import team03.monew.util.exception.article.ArticleNotFoundException;
import team03.monew.util.exception.comments.AlreadyLikedException;
import team03.monew.util.exception.comments.CommentNotFoundException;
import team03.monew.util.exception.comments.LikeNotFoundException;
import team03.monew.util.exception.user.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CommentMapper commentMapper;

    @Nested
    @DisplayName("댓글 등록")
    class CreateCommentTest {

        @Test
        @DisplayName("댓글 등록 성공")
        void create_success() {
            // given
            UUID userId = UUID.randomUUID();
            UUID articleId = UUID.randomUUID();
            CommentCreateRequest request = new CommentCreateRequest("Nice article!", articleId, userId);
            User user = new User("nick", "nick@example.com", "pass", Role.USER);
            Article article = mock(Article.class);
            Comment savedComment = new Comment(request.content(), user, article);
            CommentDto dto = new CommentDto(
                    savedComment.getId(), articleId, userId,
                    user.getNickname(), request.content(), 0L, false,
                    LocalDateTime.now()
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
            given(commentRepository.save(any(Comment.class))).willReturn(savedComment);
            given(commentMapper.toDto(savedComment)).willReturn(dto);

            // when
            CommentDto result = commentService.create(request);

            // then
            assertNotNull(result);
            assertEquals(dto.content(), result.content());
            assertEquals(dto.userNickname(), result.userNickname());
            then(commentRepository).should().save(any(Comment.class));
            then(commentMapper).should().toDto(savedComment);
        }

        @Test
        @DisplayName("사용자 없음 - 등록 실패")
        void create_userNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            CommentCreateRequest request = new CommentCreateRequest("Hi", UUID.randomUUID(), userId);
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThrows(UserNotFoundException.class, () -> commentService.create(request));
        }

        @Test
        @DisplayName("기사 없음 - 등록 실패")
        void create_articleNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            UUID articleId = UUID.randomUUID();
            CommentCreateRequest request = new CommentCreateRequest("Hi", articleId, userId);
            User user = new User("nick", "nick@example.com", "pass", Role.USER);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(articleRepository.findById(articleId)).willReturn(Optional.empty());

            // when & then
            assertThrows(ArticleNotFoundException.class, () -> commentService.create(request));
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회")
    class ListCommentsTest {

        @Test
        @DisplayName("댓글 목록 조회 성공")
        void listByArticle_success() {
            // given
            UUID articleId = UUID.randomUUID();
            UUID requesterId = UUID.randomUUID();
            Comment comment = mock(Comment.class);
            List<Comment> comments = List.of(comment);
            Page<Comment> page = new PageImpl<>(comments);
            CommentDto dto = new CommentDto(
                    comment.getId(), articleId, UUID.randomUUID(),
                    "nick", "content", 5L, true,
                    LocalDateTime.now()
            );

            given(commentRepository.findByArticleIdAndDeletedAtIsNull(
                    articleId,
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
            )).willReturn(page);
            given(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), requesterId)).willReturn(true);
            given(commentMapper.toDto(comment, true)).willReturn(dto);

            // when
            Page<CommentDto> result = commentService.listByArticle(
                    articleId, "createdAt", Sort.Direction.DESC, 10, requesterId
            );

            // then
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(dto.userNickname(), result.getContent().get(0).userNickname());
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateCommentTest {

        @Test
        @DisplayName("댓글 수정 성공")
        void update_success() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            CommentUpdateRequest request = new CommentUpdateRequest("Updated");
            Comment comment = mock(Comment.class);
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.of(comment));
            CommentDto dto = new CommentDto(
                    commentId, UUID.randomUUID(), userId,
                    "nick", request.content(), 0L, false,
                    LocalDateTime.now()
            );
            given(commentMapper.toDto(comment, false)).willReturn(dto);

            // when
            CommentDto result = commentService.update(commentId, userId, request);

            // then
            assertNotNull(result);
            assertEquals(request.content(), result.content());
            then(commentRepository).should().findByIdAndUserId(commentId, userId);
            then(commentMapper).should().toDto(comment, false);
        }

        @Test
        @DisplayName("댓글 없음 - 수정 실패")
        void update_notFound() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.empty());

            // when & then
            assertThrows(CommentNotFoundException.class,
                    () -> commentService.update(commentId, userId, new CommentUpdateRequest("x")));
        }
    }

    @Nested
    @DisplayName("댓글 논리 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("논리 삭제 성공")
        void Delete_success() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Comment comment = mock(Comment.class);
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.of(comment));

            // when
            commentService.softDelete(commentId, userId);

            // then
            then(commentRepository).should().findByIdAndUserId(commentId, userId);
            then(commentRepository).should(never()).delete(comment);
        }

        @Test
        @DisplayName("댓글 없음 - 논리 삭제 실패")
        void softDelete_notFound() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.empty());

            // when & then
            assertThrows(CommentNotFoundException.class,
                    () -> commentService.softDelete(commentId, userId));
        }
    }

    @Nested
    @DisplayName("댓글 물리 삭제")
    class HardDeleteTest {

        @Test
        @DisplayName("물리 삭제 성공")
        void hardDelete_success() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Comment comment = mock(Comment.class);
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.of(comment));

            // when
            commentService.hardDelete(commentId, userId);

            // then
            then(commentRepository).should().delete(comment);
        }

        @Test
        @DisplayName("댓글 없음 - 물리 삭제 실패")
        void hardDelete_notFound() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            given(commentRepository.findByIdAndUserId(commentId, userId)).willReturn(Optional.empty());

            // when & then
            assertThrows(CommentNotFoundException.class,
                    () -> commentService.hardDelete(commentId, userId));
        }
    }

    @Nested
    @DisplayName("댓글 좋아요")
    class LikeCommentTest {

        @Test
        @DisplayName("좋아요 등록 성공")
        void like_success() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Comment comment = mock(Comment.class);
            User user = new User("nick", "nick@example.com", "pass", Role.USER);
            CommentLike like = mock(CommentLike.class);
            CommentLikeDto likeDto = new CommentLikeDto(
                    like.getId(), userId, LocalDateTime.now(),
                    commentId, UUID.randomUUID(), user.getId(), user.getNickname(),
                    "content", 0L, LocalDateTime.now()
            );

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(commentLikeRepository.existsByCommentAndUser(comment, user)).willReturn(false);
            given(commentLikeRepository.save(any(CommentLike.class))).willReturn(like);
            given(commentMapper.toLikeDto(like)).willReturn(likeDto);

            // when
            CommentLikeDto result = commentService.likeComment(commentId, userId);

            // then
            assertNotNull(result);
            assertEquals(likeDto.commentId(), result.commentId());
            then(commentLikeRepository).should().save(any(CommentLike.class));
        }

        @Test
        @DisplayName("이미 좋아요된 댓글 - 등록 실패")
        void like_already() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Comment comment = mock(Comment.class);
            User user = new User("nick", "nick@example.com", "pass", Role.USER);

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(commentLikeRepository.existsByCommentAndUser(comment, user)).willReturn(true);

            // when & then
            assertThrows(AlreadyLikedException.class, () -> commentService.likeComment(commentId, userId));
            then(commentLikeRepository).should(never()).save(any(CommentLike.class));
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 취소")
    class UnlikeCommentTest {

        @Test
        @DisplayName("좋아요 취소 성공")
        void unlike_success() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            CommentLike like = mock(CommentLike.class);
            given(commentLikeRepository.findByCommentIdAndUserId(commentId, userId)).willReturn(Optional.of(like));

            // when
            commentService.unlikeComment(commentId, userId);

            // then
            then(commentLikeRepository).should().delete(like);
        }

        @Test
        @DisplayName("좋아요 정보 없음 - 취소 실패")
        void unlike_notFound() {
            // given
            UUID commentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            given(commentLikeRepository.findByCommentIdAndUserId(commentId, userId)).willReturn(Optional.empty());

            // when & then
            assertThrows(LikeNotFoundException.class, () -> commentService.unlikeComment(commentId, userId));
        }
    }
}
