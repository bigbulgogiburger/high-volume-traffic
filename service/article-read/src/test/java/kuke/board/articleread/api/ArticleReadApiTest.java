package kuke.board.articleread.api;

import kuke.board.articleread.client.ArticleClient;
import kuke.board.articleread.service.response.ArticleReadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleReadApiTest {
    RestClient restClient = RestClient.create("http://localhost:9005");

    @Test
    void readTest(){
        ArticleReadResponse body = restClient.get().uri("/v1/articles/{articleId}", 263335757986525184L)
                .retrieve().body(ArticleReadResponse.class);

        System.out.println("body = " + body);
    }
}
