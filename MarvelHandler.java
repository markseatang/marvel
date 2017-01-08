import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.HttpURLConnection;
import java.net.URL;

public class MarvelHandler implements HttpHandler {
   private HashMap<Integer, String> cache;
   public MarvelHandler () {
      cache = new HashMap<Integer, String>();
   }    

   @Override
   public void handle(HttpExchange t) throws IOException {
      String query = t.getRequestURI().getQuery();
      System.out.println("Request query: "  + query);
      
      String[] split = query.split("=");
      String argString = split[1];
      String[] args = argString.split(",");

      ArrayList<Integer> queue = new ArrayList<Integer>();

      for (String arg : args) {
         Integer id = Integer.valueOf(arg);
         System.out.println(id);
         queue.add(id);
      }

      ArrayList<String> responseQueue = new ArrayList<String>();
      
      for (Integer q : queue) {
         String response;
         if (cache.containsKey(q)) {
            response = cache.get(q); // retrieve value from the map if it exists already
            System.out.println("Served a cached request for id no. " + q);
         } else {
            response = getHTML(q); // else do a HTTP request from marvel api and store it
            cache.put(q, response);
            System.out.println("Requested from Marvel API for id no. " + q);
         }
         responseQueue.add(response);
      }

      t.sendResponseHeaders(200, 0); // 2nd arg length set to 0 to allow arbitrary length resp.
      OutputStream os = t.getResponseBody();
      for (String response : responseQueue) {
         os.write(response.getBytes());
      }
      os.close();
   }
   /*
    * This function given a comic ID as an int makes a marvel API call and fetches the 
    * details of the comic and returns it as a string
    * Code inspired by stack exchange post :p
    * http://stackoverflow.com/a/1485730
    */ 
   private String getHTML(int comicId) throws IOException {
      // OK, probably a bit insecure here regarding API keys - please don't steal my keys
      String urlToRead = "http://gateway.marvel.com/v1/public/comics/" + comicId + 
         "?ts=1&apikey=46ec917e23a4130cf16fd110c47f0e8e&hash=e3b9d91f70a83e60f3d91bf2230b84ca";
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
