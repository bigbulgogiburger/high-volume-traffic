package kuke.board.article.api;

import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        ArticleResponse response = create(new ArticleCreateRequest("test title", "test content", 1L, 1L));
        System.out.println("response = " + response);

    }

    @Test
    void readTest(){
        Long articleId = 243354887782297600L;
        ArticleResponse response = read(articleId);
        System.out.println("response = " + response);
    }

    @Test
    void updateTest(){
        update(243354887782297600L);
        ArticleResponse read = read(243354887782297600L);
        System.out.println("read = " + read);
    }

    @Test
    void deleteTest(){
        delete(243354887782297600L);
    }

    void delete(Long articleId){
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
        ;
    }

    void update(Long articleId){
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2","hh22"))
                .retrieve()
        ;
    }

    private ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }


    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readAllTest(){
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&page=50000&pageSize=30")
                .retrieve()
                .body(ArticlePageResponse.class);
        for(ArticleResponse articleResponse : response.getArticles()){
            System.out.println("articleId = " + articleResponse.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<ArticleResponse> response = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });


        System.out.println("firstPage");
        for(ArticleResponse articleResponse : response){
            System.out.println("articleId = " + articleResponse.getArticleId());
        }
        Long lastArticleId = response.getLast().getArticleId();

        List<ArticleResponse> response2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("secondPage");
        for(ArticleResponse articleResponse : response2){
            System.out.println("articleId = " + articleResponse.getArticleId());
        }



    }

    @Test
    void countTest(){
        ArticleResponse response = create(new ArticleCreateRequest("test title", "test content", 2L, 1L));

        Long count1 = restClient.get().uri("/v1/articles/boards/{boardId}/count", 2L).retrieve().body(Long.class);

        System.out.println("count1 = " + count1);

        restClient.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve()
        ;
        Long count2= restClient.get().uri("/v1/articles/boards/{boardId}/count", 2L).retrieve().body(Long.class);



        System.out.println("count2 = " + count2);



    }


    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {

        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {

        private String title;
        private String content;
    }
}
