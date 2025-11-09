package kuke.board.comment.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "comment")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId; // shard key
    private Long writerId;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public Comment(Long commentId, String content, Long parentCommentId, Long articleId, Long writerId) {
        this.commentId = commentId;
        this.content = content;
        this.parentCommentId = parentCommentId==null?commentId:parentCommentId;
        this.articleId = articleId;
        this.writerId = writerId;
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }

    public static Comment create(Long commentId, String content, Long parentCommentId, Long articleId, Long writerId) {

        return new Comment(
                commentId,
                content,
                parentCommentId,articleId,writerId
        );

    }

    public boolean isRoot(){
        return parentCommentId.longValue() == commentId;
    }

    public void delete(){
        this.deleted = true;
    }
}
