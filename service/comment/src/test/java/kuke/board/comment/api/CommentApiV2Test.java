package kuke.board.comment.api;

import kuke.board.comment.service.request.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create(){
        CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());



    }

    CommentResponse create(CommentCreateRequestV2 request){
        return restClient.post().uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read(){
        /*
        response1.getPath() = 00002
        response1.getCommentId() = 246652625382105088
	    response2.getPath() = 0000200000
	    response2.getCommentId() = 246652625604403200
		response3.getPath() = 000020000000000
		response3.getCommentId() = 246652625663123456
         */
        CommentResponse response = restClient.get()
                .uri("/v2/comments/{commentId}", 246652625382105088L)
                .retrieve()
                .body(CommentResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void readAll(){
        CommentPageResponse response = restClient.get()
                .uri("/v2/comments?articleId=1&page=50000&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for(CommentResponse comment : response.getCommentResponses()){
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         * response.getCommentCount() = 101
         * comment.getCommentId() = 246652175501058048
         * comment.getCommentId() = 246652176008568832
         * comment.getCommentId() = 246652176075677696
         * comment.getCommentId() = 246652341129928704
         * comment.getCommentId() = 246652341377392640
         * comment.getCommentId() = 246652341431918592
         * comment.getCommentId() = 246652625382105088
         * comment.getCommentId() = 246652625604403200
         * comment.getCommentId() = 246652625663123456
         * comment.getCommentId() = 246654347764662272
         *
         */
    }

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> response = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>(){});

        System.out.println("first page");
        for (CommentResponse commentResponse : response) {
            System.out.println("commentResponse.getCommentId() = " + commentResponse.getCommentId());
        }

        String lastPath = response.getLast().getPath();

        List<CommentResponse> response2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>(){});


        System.out.println("second page");
        for (CommentResponse commentResponse : response2) {
            System.out.println("commentResponse.getCommentId() = " + commentResponse.getCommentId());
        }
    }

    @Test
    void delete(){
        restClient.delete()
                .uri("/v2/comments/{commentId}", 246652625382105088L)
                .retrieve()
        ;
    }


    @Test
    void countTest(){
        CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my comment", null, 1L));

        Long count1 = restClient.get().uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1);


        restClient.delete()
                .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
                .retrieve()
        ;

        Long count2 = restClient.get().uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2);


    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }

}
