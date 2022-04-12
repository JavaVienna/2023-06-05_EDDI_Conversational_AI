package ai.labs.eddi.engine.lifecycle;

import java.util.Map;

public interface IComponentCache {
    Map<String, Object> getComponent(String type);

    void put(String type, String key, Object component);
}
