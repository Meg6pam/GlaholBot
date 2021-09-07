package com.github.meg6pam.glaholbot.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ActivityListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

public class YoutubeAdapter {
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "GlaholBot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CLIENT_ID = System.getenv("YOUTUBE_ID");
    private static final String SECRET = System.getenv("YOUTUBE_SECRET");

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, CLIENT_ID, SECRET, SCOPES)
                        .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() {
        NetHttpTransport httpTransport = null;
        Credential credential = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            credential = authorize(httpTransport);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        assert httpTransport != null;
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Activities.List request = youtubeService.activities()
                .list("");
        ActivityListResponse response = request.execute();
        System.out.println(response);
    }
}
