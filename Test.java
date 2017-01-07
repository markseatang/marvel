import java.util.Scanner;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static void main(String[] args) throws Exception {
        // access via localhost:8000, backlog of 0 requests (only one request at a time)
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
      
        // first arg specifies how to access this thing, i.e. localhost:8000/test
        // second arg specifies the handler behavior
        server.createContext("/test", new MyHandler());
      
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Press any key then return to quit");
        
        Scanner sc = new Scanner(System.in);
        sc.next();
        System.out.println("Server going down in 1 second");
        server.stop(1);
        System.out.println("Server closed");
        System.exit(0);

    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
