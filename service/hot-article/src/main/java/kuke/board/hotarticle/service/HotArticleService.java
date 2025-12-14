package kuke.board.hotarticle.service;

import kuke.board.common.event.Event;
import kuke.board.common.event.EventPayload;
import kuke.board.common.event.EventType;
import kuke.board.hotarticle.repository.HotArticleListRepository;
import kuke.board.hotarticle.service.eventhandler.EventHandler;
import kuke.board.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import kuke.board.hotarticle.client.ArticleClient;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {

    private final ArticleClient articleClient;
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    private final HotArticleListRepository hotArticleListRepository;

    public void handleEvent(Event<EventPayload> event){

        final EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if(event == null){
            return;
        }

        if(isArticleCreatedOrDeleted(event)){
            eventHandler.handle(event);
        }else{
            hotArticleScoreUpdater.update(event,eventHandler);
        }
    }

    private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.ARTICLE_CREATED.equals(event.getType()) || EventType.ARTICLE_DELETED.equals(event.getType());
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event){
        return eventHandlers.stream()
                .filter(handler -> handler.supports(event))
                .findAny()
                .orElse(null);
    }

    public List<HotArticleResponse> readAll(String dateStr){
        //yyyyMMdd
        return hotArticleListRepository.readAll(dateStr).stream()
                .map(articleClient::read)
                .filter(Objects::nonNull)
                .map(HotArticleResponse::from)
                .toList();
    }



}
