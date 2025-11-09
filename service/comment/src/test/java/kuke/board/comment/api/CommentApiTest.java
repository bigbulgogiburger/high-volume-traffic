package kuke.board.comment.api;

import kuke.board.comment.service.request.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void createTest(){

        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s  ".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s  ".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s  ".formatted(response3.getCommentId()));
//
//        commentId=245800909467877376 =
//                commentId=245800909891502080 =
//                        commentId=245800909929250816 =

    }

    CommentResponse createComment(CommentCreateRequest request){
        return restClient.post().uri("/v1/comments").body(request).retrieve().body(CommentResponse.class);
    }

    @Test
    void readTest(){
        CommentResponse body = restClient.get().uri("/v1/comments/{commentId}", 245800909467877376L).retrieve().body(CommentResponse.class);
        System.out.println("body = " + body);
    }

    @Test
    void deleteTest(){

//        commentId=245802428806717440
//        commentId=245802428949323776
//        commentId=245802428995461120

        restClient.delete().uri("/v1/comments/{commentId}", 245803706478391296L).retrieve();
    }


    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {

        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

    @Test
    void readAll(){
        CommentPageResponse response = restClient.get().uri("/v1/comments?articleId=1&page=50000&pageSize=10").retrieve().body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());

        for(CommentResponse comment : response.getCommentResponses()){
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**first page
         * comment.getCommentId() = 245804976050390592
         * 	comment.getCommentId() = 245804976050390593
         * comment.getCommentId() = 245804976050390594
         * 	comment.getCommentId() = 245804976050390595
         * comment.getCommentId() = 245804976050390596
         * 	comment.getCommentId() = 245804976050390597
         * comment.getCommentId() = 245804976050390598
         * 	comment.getCommentId() = 245804976050390599
         * comment.getCommentId() = 245804976050390600
         * 	comment.getCommentId() = 245804976050390601
         */
    }

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> response = restClient.get().uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve().body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("first page");

        for(CommentResponse comment : response){
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }


        Long parentCommentId = response.getLast().getParentCommentId();
        Long commentId = response.getLast().getCommentId();
        List<CommentResponse> response2 = restClient.get().uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s".formatted(parentCommentId, commentId))
                .retrieve().body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage ");

        for(CommentResponse comment : response2){
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

}
