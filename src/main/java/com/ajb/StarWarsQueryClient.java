package com.ajb;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Scanner;

import static java.util.Collections.singletonMap;

/** Class for calling the Star Wars query proxy using Socket.IO
 *  Asks a user for input from the console and sends that search to the service
 */
public class StarWarsQueryClient {
    // Socket for connection to server
    private final Socket socket;
    // Ready state used to allow user to input if ready, else awaiting connection/running another query
    private boolean ready = false;

    public StarWarsQueryClient(String url) {
        URI uri = URI.create(url);
        System.out.println("Will connect to server on: " + url);
        IO.Options options = IO.Options.builder().build();
        socket = IO.socket(uri, options);
    }

    public void start() {
        setupEventHandlers();
        try {
            while (true) {
                while (!ready) {
                    Thread.sleep(500L);
                }
                socket.emit("search", new JSONObject(singletonMap("query", awaitQuery())));
                ready = false;
            }
        }
        catch (InterruptedException ioe) {
            System.err.println("Thread interrupted:" + ioe);
        }
    }

    /**
     * All event handlers set here
     */
    private void setupEventHandlers() {
        socket.on("connect", (resp) -> {
            System.out.println("Connected to server");
            ready = true;
        });
        socket.on("disconnect", (resp) -> {
            System.out.println("Disconnected from server... awaiting reconnection");
            ready = false;
        });
        socket.on("search", this::handleSearchEvent);
        socket.connect();
    }

    /**
     * Deal with search event and paging, do not allow another search to be sent until current one completed.
     * @param resp The search result event
     */
    private void handleSearchEvent(Object[] resp) {
            try {
                JSONObject json = (JSONObject) resp[0];
                SearchResult result = new SearchResult(json);
                System.out.println(result);
                // GE used defensively in case server mis-calculates result count/pages. Shouldn't happen
                if (result.page >= result.resultCount) {
                    ready = true;
                }
            }
            catch (JSONException jse) {
                System.err.println("Unexpected response from service: " + jse);
                ready = true;
            }
    }

    /**
     * Get input from user
     */
    private String awaitQuery() {
        System.out.print("Please enter your query: ");
        Scanner scan= new Scanner(System.in);
        return scan.nextLine();
    }
}

/**
 * POJO for mapping JSON response from server
 */
class SearchResult {
    public SearchResult(JSONObject json) throws JSONException {
            this.resultCount = json.getInt("resultCount");
            this.page = json.getInt("page");
            // If we have results, grab the name and films, else put the error from the response in the error field.
            if (resultCount > 0 && page > 0) {
                this.name = json.getString("name");
                this.films = json.getString("films");
                this.error = null;
            } else {
                this.name = null;
                this.films = null;
                this.error = json.getString("error");
            }
    }

    final String name;
    final String films;
    final int page;
    final int resultCount;
    final String error;

    // Getters removed as not used

    // Print pretty-formatted result or error.
    public String toString() {
        if(error != null) {
            return error;
        }
        else {
            return String.format("Search result %d/%d - Name: %s / Films: %s", page, resultCount, name, films);
        }

    }

}

