package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import com.google.protobuf.MessageLite;
import de.dreipc.rabbitmq.ProtoModel;
import de.dreipc.rabbitmq.ProtoPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TestProtoPublisher implements ProtoPublisher {

    private Map<String, List<MessageLite>> sendEvents = new ConcurrentHashMap<>();

    @Override
    public void sendEvent(String routingKey, ProtoModel data) {
        sendEventMessage(routingKey, data.toProto());
    }

    public void sendEventMessage(String routingKey, MessageLite data) {
        if (sendEvents.containsKey(routingKey)) {
            sendEvents
                    .get(routingKey)
                    .add(data);
        } else {
            var newList = new ArrayList<MessageLite>();
            newList.add(data);
            sendEvents.put(routingKey, newList);
        }
    }

    @Override
    public void sendEvent(String routingKey, Object object) {
        if (object instanceof ProtoModel protoModel)
            sendEvent(routingKey, protoModel);
        else if (object instanceof MessageLite message) {
            sendEventMessage(routingKey, message);
        } else
            throw new IllegalArgumentException("Unsupported type " + object
                    .getClass()
                    .getSimpleName());
    }

    @Override
    public void sendEvent(String routingKey, Object object, String correlationId, String replyQueue) {
        log.warn("Test Publisher ignores correlationId and replyQueue for now.");
        sendEvent(routingKey, object);
    }

    public Map<String, List<MessageLite>> events() {
        return sendEvents;
    }

    public void reset() {
        this.sendEvents = new HashMap<>();
    }
}
