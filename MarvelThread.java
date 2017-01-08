import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import java.util.HashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.HttpURLConnection;
import java.net.URL;
public class MarvelThread implements Runnable {

    private LinkedList<Integer> sharedQueue;
    private ArrayList<String> sharedResponses;
    private ArrayList<String> myResponses;
    private HashMap<Integer,String> cache;
    private ReentrantLock queueMutex;
    private ReentrantLock responseMutex;
    private ReentrantLock cacheMutex; 


    public MarvelThread(LinkedList<Integer> queue, ArrayList<String> responseQueue,
          HashMap<Integer, String> cache, ReentrantLock queueMutex,
          ReentrantLock responseMutex, ReentrantLock cacheMutex) {

       this.queueMutex = queueMutex;
       this.responseMutex = responseMutex;
       this.cacheMutex = cacheMutex;
       this.cache = cache;

       sharedQueue = queue;
       sharedResponses = responseQueue;
       myResponses = new ArrayList<String>();
    }   
   @Override
   public void run() {
      while (sharedQueue.isEmpty() == false) {
         // Lock before accessing shared list of Comic ID numbers.
         queueMutex.lock();
         Integer currVal = null;
         try {
             currVal = sharedQueue.pollFirst();
         } finally {
            queueMutex.unlock();
         }

         if (currVal == null) break; // Exit this loop if list is empty and we fail to retrieve a Comic ID

         String response = null;

         // Check the cache. Cache only requires mutex when adding to map
         if (cache.containsKey(currVal)) {
            response = cache.get(currVal); // retrieve value from the map if it exists already
            System.out.println("Served a cached request for id no. " + currVal);
         } else {

            try {
               response = getHTML(currVal); // else do a HTTP request from marvel api and store it
            } catch (IOException e) {
               System.err.println("Caught IOException: " + e.getMessage());
            }
            if (response == null) {
               System.out.println("Queried comicID " + currVal + " and no response. Continuing.");
               continue;
            }

            cacheMutex.lock();
            try {
               cache.put(currVal, response);
            } finally {
               cacheMutex.unlock();
            }
            System.out.println("Requested from Marvel API for id no. " + currVal);
         }
         myResponses.add(response);
      }
      // When this loop terminates, the list of inputs should be empty,
      // and we should have a local list of responses. 
      // We should append this list to the shared list
      responseMutex.lock();
      try {
         sharedResponses.addAll(myResponses);
      } finally {
         responseMutex.unlock();
      }
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
