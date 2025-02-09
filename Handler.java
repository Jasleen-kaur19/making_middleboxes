

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class Handler implements HttpHandler {
    LoadBalancer loadBalancer = new LoadBalancer();
    IPFilter ipFilter = new IPFilter();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        System.out.println("Request come for URL: " + path);
        String ipAddr = exchange.getRemoteAddress().getAddress().getHostAddress();
        System.out.println("Cleint IP Address: " + ipAddr);

        // check if request coming from allowd ip adress
        if (ipFilter.isRequestIPAllowed(ipAddr)) {
            if ("POST".equals(exchange.getRequestMethod())) {
                // get reuest body
                String requestBodyString = getDataFromInputStream(exchange.getRequestBody());

                // Forward request
                forwardRequest(exchange, path, requestBodyString);
            } else if ("GET".equals(exchange.getRequestMethod())) {
                // Forward request
                forwardRequest(exchange, path, null);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, "OK", 200);
            } else {
                System.out.println(exchange.getRequestMethod());
            }
        } else {
            sendResponse(exchange, "Forbidden", 403);
        }
    }

    // forward request to actual service
    private void forwardRequest(HttpExchange exchange, String path, String requestBody) throws IOException {
        String method = exchange.getRequestMethod();
        String serverURL = loadBalancer.getServerURL();

        URL url = URI.create(serverURL + path).toURL();

        // establist connection to service
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method
        connection.setRequestMethod(method);
        connection.setDoOutput(true);

        // if its the post method pass request body as well
        if ("POST".equals(method)) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
            }
        }

        // Get the response from the target service
        int responseCode = connection.getResponseCode();

        String responseData = getDataFromInputStream(connection.getInputStream());

        sendResponse(exchange, responseData, responseCode);
    }

    // This function is used to extract data from input stream
    private String getDataFromInputStream(InputStream rawBody) throws IOException {
        InputStreamReader isr = new InputStreamReader(rawBody);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            requestBodyBuilder.append(line);
        }

        br.close();
        isr.close();
        rawBody.close();

        return requestBodyBuilder.toString();
    }

    // Common method send response back to who calls the api
    private void sendResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
        OutputStream os = exchange.getResponseBody();
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(responseCode, response.length());

        os.write(response.getBytes());

        // Close the output stream and complete the exchange
        os.close();
    }
}
