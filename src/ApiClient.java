import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.StringJoiner;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public Object get(String path) {
        return send("GET", path, null);
    }

    public Object get(String path, Map<String, String> queryParams) {
        return send("GET", withQuery(path, queryParams), null);
    }

    public Object post(String path, Object body) {
        return send("POST", path, SimpleJson.stringify(body));
    }

    public Object put(String path, Object body) {
        return send("PUT", path, SimpleJson.stringify(body));
    }

    public Object delete(String path, Map<String, String> queryParams) {
        return send("DELETE", withQuery(path, queryParams), null);
    }

    public void postNoContent(String path, Object body) {
        sendRaw("POST", path, SimpleJson.stringify(body));
    }

    private Object send(String method, String path, String body) {
        String response = sendRaw(method, path, body);
        if (response == null || response.isBlank()) {
            return null;
        }
        return SimpleJson.parse(response);
    }

    private String sendRaw(String method, String path, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json");

            if (body != null) {
                builder.header("Content-Type", "application/json");
            }

            HttpRequest request = switch (method) {
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body)).build();
                case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(body == null ? "" : body)).build();
                case "DELETE" -> builder.DELETE().build();
                default -> builder.GET().build();
            };

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new ApiException(response.statusCode(), response.body());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("HTTP request failed: " + method + " " + path, e);
        }
    }

    private String withQuery(String path, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return path;
        }
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (entry.getValue() != null) {
                joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }
        String qs = joiner.toString();
        return qs.isEmpty() ? path : path + "?" + qs;
    }

    public static class ApiException extends RuntimeException {
        public final int statusCode;
        public final String responseBody;

        public ApiException(int statusCode, String responseBody) {
            super("API request failed with status " + statusCode + ": " + responseBody);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }
    }
}
