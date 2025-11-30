package kuke.board.hotarticle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import kuke.board.articleread.client.ArticleClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {

    private final ArticleClient articleClient;


}
