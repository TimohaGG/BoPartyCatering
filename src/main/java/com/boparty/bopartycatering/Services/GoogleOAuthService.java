package com.boparty.bopartycatering.Services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
//import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import org.apache.http.client.methods.HttpTrace;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@Component
public class GoogleOAuthService {
    private static final String APPLICATION_NAME = "BoPartyCatering";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String REDIRECT_URI = "https://boparty.website/oauth2/callback";
//    private static final String REDIRECT_URI = "http://localhost:8080/oauth2/callback";


    private AuthorizationCodeFlow flow;

    public GoogleOAuthService() throws IOException, GeneralSecurityException {

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(CREDENTIALS_FILE_PATH)))
        );

        flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline")
                .build();
    }

    public String getAuthorizationUrl(){
        AuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri(REDIRECT_URI).build();
    }

    public Credential getCredentials(String code, String user_id) throws IOException {
        AuthorizationCodeTokenRequest req = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI);
        return flow.createAndStoreCredential(req.execute(),user_id);
    }

    public Calendar getCalendarService(String userId) throws IOException {
        Credential credential= flow.loadCredential(userId);
        return new com.google.api.services.calendar.Calendar.Builder(flow.getTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public boolean isUserAuthorized(String user_id) throws IOException {
        Credential credential = flow.loadCredential(user_id);
        if (credential == null || credential.getAccessToken() == null) {
            return false;
        }

        // Check if token is expired
        if (credential.getExpirationTimeMilliseconds() != null &&
                credential.getExpirationTimeMilliseconds() < System.currentTimeMillis()) {

            // Try refreshing the token
            if (credential.getRefreshToken() != null) {
                credential.refreshToken();
                return true;
            }

            return false; // No valid refresh token, user needs to log in again
        }

        return true; // Token is still valid
    }

    public void logoutUser(String userId) throws IOException {
        Credential credential = flow.loadCredential(userId);

        if(credential != null && credential.getAccessToken() != null) {
            String revokeUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + credential.getAccessToken();
            HttpRequest req = flow.getTransport()
                    .createRequestFactory()
                    .buildGetRequest(new GenericUrl(revokeUrl));
            req.execute();
            flow.getCredentialDataStore().delete(userId);

        }


    }

    public Credential  getCredential(String userId) throws IOException {
        return flow.loadCredential(userId);
    }

    public List<CalendarListEntry> getAllCalendars(String userId) throws IOException {
        return getCalendarService(userId).calendarList().list().execute().getItems();
    }

    public String getCalendarId(String userId, String calendarName) throws IOException {
        List<CalendarListEntry> list = getCalendarService(userId).calendarList().list().execute().getItems();
        CalendarListEntry res= list.stream().filter(x->x.getSummary().equalsIgnoreCase(calendarName)).findFirst().orElse(null);
        return res==null ? "primary" : res.getId();

    }

    public Map<String, ColorDefinition> getColors(String userId){
        try{
            Calendar service = getCalendarService(userId);
            return service.colors().get().execute().getEvent();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;

    }

}
