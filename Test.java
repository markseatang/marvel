import java.util.Scanner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

import java.net.HttpURLConnection;
import java.net.URL;
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
           System.out.println("My path was: " + this.getPath());
//            String response = "http://gateway.marvel.com/v1/public/comics/2?ts=1&apikey=46ec917e23a4130cf16fd110c47f0e8e&hash=e3b9d91f70a83e60f3d91bf2230b84ca";
            String response = getHTML("http://gateway.marvel.com/v1/public/comics/2?ts=1&apikey=46ec917e23a4130cf16fd110c47f0e8e&hash=e3b9d91f70a83e60f3d91bf2230b84ca");
            t.sendResponseHeaders(200, 0); // 2nd arg length set to 0 to allow arbitrary length resp.
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Code sourced from stack exchange :p
    // http://stackoverflow.com/a/1485730
    public static String getHTML(String urlToRead) throws IOException {
       StringBuilder result = new StringBuilder();
       URL url = new URL(urlToRead);
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();
       conn.setRequestMethod("GET");
       BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
       String line;
       while ((line = rd.readLine()) != null) {
          result.append(line);
       }
       rd.close();
       return result.toString();
    }

}
