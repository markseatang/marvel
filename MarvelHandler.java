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
      int numIds = comicIdQueue.size();

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
      // After threads have joined, we'll know how many responses we got
      int numResponses = responseQueue.size();

      t.sendResponseHeaders(200, 0); // 2nd arg length set to 0 to allow arbitrary length resp.
      OutputStream os = t.getResponseBody();
      String head = header(numIds, numResponses);  

      String tail = footer();
      os.write(head.getBytes()); // Write header
      int i = 0;
      for (String currOutput : responseQueue) {
         currOutput = strip(currOutput);
         
         if (i != responseQueue.size() - 1) {
            currOutput = currOutput.concat(",");
         }
         os.write(currOutput.getBytes());
         i++;
      }
      os.write(tail.getBytes());
      os.close();
   }

   // This lets us generate something that looks like the header of the json file
   private String header (int total, int count) {
      return "{\"code\":200,\"status\":\"Ok\",\"copyright\":\" 2017 MARVEL\",\"attributionText\":\"Data provided by Marvel.  2017 MARVEL\",\"attributionHTML\":\"<a href=\\\"http://marvel.com\\\">Data provided by Marvel.  2017 MARVEL</a>\",\"etag\":\"9595ee96720036501b9bfc79c13824bed3ac96f7\",\"data\":{\"offset\":0,\"limit\":20,\"total\":" + total + ",\"count\":" + count + ",\"results\":[";
   }
   // This lets us generate something that looks like the footer of the json file
   private String footer () {
      return "]}}"; 
   }

   // Given a raw json input from the marvel API, strip out only the results section and return it
   private String strip (String input) {

      String[] header = input.split("results\"\\:\\["); // because i only want all the chars after results:[
      String tail = header[1];
      String output = tail.substring(0, tail.length() - 3); // strip off the trailing "]}}" sequence*/
      return output;
   }

}
