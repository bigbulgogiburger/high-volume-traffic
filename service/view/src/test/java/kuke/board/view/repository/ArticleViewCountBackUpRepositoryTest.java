package kuke.board.view.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.view.entity.ArticleViewCount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleViewCountBackUpRepositoryTest {


    @Autowired
    ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void updateViewCount() {
        ArticleViewCount articleViewCount  = articleViewCountBackUpRepository.save(
                ArticleViewCount.init(1L,0L)
        );

        em.flush();
        em.clear();

        int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
        int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
        int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

        Assertions.assertEquals(1, result1);
        Assertions.assertEquals(1, result2);
        Assertions.assertEquals(0, result3);

        ArticleViewCount articleViewCount1 = articleViewCountBackUpRepository.findById(1L).get();
        assertEquals(300L, articleViewCount1.getViewCount());
    }

}