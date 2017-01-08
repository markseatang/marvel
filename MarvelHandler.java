import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.locks.ReentrantLock;
import java.net.HttpURLConnection;
import java.net.URL;

public class MarvelHandler implements HttpHandler {
   private HashMap<Integer, String> cache;
   private ReentrantLock queueMutex;
   private ReentrantLock responseMutex;
   private ReentrantLock cacheMutex; 

   public MarvelHandler () {
      cache = new HashMap<Integer, String>();
      queueMutex = new ReentrantLock();
      responseMutex = new ReentrantLock();
      cacheMutex = new ReentrantLock();

   }    

   @Override
   public void handle(HttpExchange t) throws IOException {

      // Parse the request queries and get the comic IDs
      String query = t.getRequestURI().getQuery();
      System.out.println("Request query: "  + query);
      
      String[] split = query.split("=");
      String argString = split[1];
      String[] args = argString.split(",");
      
      LinkedList<Integer> comicIdQueue = new LinkedList<Integer>();
      ArrayList<String> responseQueue = new ArrayList<String>();
      // Put the Comic Ids in a list
      for (String arg : args) {
         Integer id = Integer.valueOf(arg);
         comicIdQueue.add(id);
      }

      // Create my threads to do the requests
      int numCores = Runtime.getRuntime().availableProcessors();
      ArrayList <Thread> threadCollection = new ArrayList<Thread> ();
      for (int i = 1; i < numCores; i++) {
         MarvelThread thread = new MarvelThread(comicIdQueue, responseQueue, cache,
               queueMutex, responseMutex, cacheMutex);
         threadCollection.add(new Thread(thread));
      }
      for (Thread th : threadCollection) {
         th.start();
      }
      try {
         for (Thread th : threadCollection) {
            th.join();
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }


      t.sendResponseHeaders(200, 0); // 2nd arg length set to 0 to allow arbitrary length resp.
      OutputStream os = t.getResponseBody();
      for (String response : responseQueue) {
         os.write(response.getBytes());
      }
      os.close();
   }
}
