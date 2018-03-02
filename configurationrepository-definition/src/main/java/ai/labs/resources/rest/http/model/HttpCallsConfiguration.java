package ai.labs.resources.rest.http.model;


import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author ginccc
 */

@Getter
@Setter
public class HttpCallsConfiguration {
    private URI targetServer;
    private List<HttpCall> httpCalls;

    @Getter
    @Setter
    private class HttpCall {
        private String name;
        private List<String> actions;

        private URI path;
        private String method;
        private String contentType;
        private Map<String, String> headers;
        private String body;
    }
}
