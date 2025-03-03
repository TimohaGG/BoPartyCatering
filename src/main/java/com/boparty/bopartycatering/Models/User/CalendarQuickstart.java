package com.boparty.bopartycatering.Models.User;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class CalendarQuickstart {

    public static final String APPLICATION_NAME = "BoPartyCatering";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";


    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

//        // Generate authorization URL
//        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri("urn:ietf:wg:oauth:2.0:oob").build();
//
//        System.out.println("1. Open the following URL in your browser:");
//        System.out.println(authorizationUrl);
//        System.out.println("2. Authorize the application and copy the authorization code.");
//
//        // Read authorization code from user input
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter the authorization code: ");
//        String code = scanner.nextLine();
//
//        // Exchange the authorization code for an access token
//        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
//                .setRedirectUri("urn:ietf:wg:oauth:2.0:oob")
//                .execute();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }


}