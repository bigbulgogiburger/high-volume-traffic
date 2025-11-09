package kuke.board.comment.service.request;

import kuke.board.comment.service.response.CommentResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPageResponse {

    private List<CommentResponse> commentResponses;
    private Long commentCount;

    public static CommentPageResponse of(List<CommentResponse> commentResponses, Long commentCount) {
        CommentPageResponse response = new CommentPageResponse();
        response.commentResponses = commentResponses;
        response.commentCount = commentCount;
        return response;
    }
}
