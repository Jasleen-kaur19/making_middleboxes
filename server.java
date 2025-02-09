

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
// import middlebox.Handler;

public class server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3010), 0);

        server.createContext("/", new Handler());

        server.setExecutor(null);

        server.start();

        System.out.println("Server is running on port 3010");
    }
}
