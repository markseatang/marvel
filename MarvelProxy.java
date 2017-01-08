import java.util.Scanner;

import java.io.IOException;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MarvelProxy {
   private HttpServer server;

   public MarvelProxy (int port, int backlog) throws IOException {
      server = HttpServer.create(new InetSocketAddress(port), backlog);
   }

   // Bootstrap function that sets up and runs the server
   public void run() {

        server.createContext("/marvelous", new MarvelHandler());
      
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Press any key then return to quit");
        System.out.println();
        
        
        Scanner sc = new Scanner(System.in);
        sc.next(); // buffer and wait for keypress for safe shutdown
        shutdown(1);
   }

   // Shut down the system after 1 second
   private void shutdown(int time) {

        System.out.println("Server going down in " + time + " second(s)");
        server.stop(time);
        System.out.println("Server closed successfully");
        System.exit(0);
   }
   public static void main(String[] args) throws IOException {
        // access via localhost:8000, backlog of 0 requests (only one request at a time)
        MarvelProxy bootstrap = new MarvelProxy(8000, 0);
        bootstrap.run();
    }
}

