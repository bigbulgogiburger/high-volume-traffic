package kuke.board.common.event;

import kuke.board.common.dataserializer.DataSerializer;
import lombok.Getter;

@Getter
public class Event< T extends EventPayload>{

    private Long eventId;
    private EventType type;
    private T payload;

    public static Event<EventPayload> of (Long eventId, EventType type, EventPayload payload){
        Event<EventPayload> event = new Event<>();
        event.eventId = eventId;
        event.type = type;
        event.payload = payload;
        return event;
    }

    public String toJson(){
        return DataSerializer.serialize(this);
    }

    public static Event<EventPayload> fromJson(String json){
        EventRaw raw = DataSerializer.deserialize(json, EventRaw.class);

        if(raw == null){
            return null;
        }

        Event<EventPayload> event = new Event<>();
        event.eventId = raw.getEventId();
        event.type  = EventType.from(raw.getType());
        event.payload = DataSerializer.deserialize(raw.getPayload(),event.type.getPayloadClass());
        return event;
    }


    @Getter
    private static class EventRaw{
        private Long eventId;
        private String type;
        private Object payload;
    }

}
