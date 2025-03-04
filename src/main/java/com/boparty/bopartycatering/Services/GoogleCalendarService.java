package com.boparty.bopartycatering.Services;

import com.boparty.bopartycatering.Models.Order.OrderAdditionalInfo;
import com.boparty.bopartycatering.Models.Order.Orders;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class GoogleCalendarService {
    private final GoogleOAuthService service;

    public GoogleCalendarService(GoogleOAuthService service) {
       this.service = service;
    }

    public boolean createEvent(String userId, Orders order) {
        try{

            Calendar calendar = service.getCalendarService(userId);


            String title = order.getClient();
            OrderAdditionalInfo deliver = order.getAdditionalInfo().stream().filter(x->x.getTitle().contains("Доставка")).findFirst().orElse(null);
            if(deliver!=null){
                title = title.concat(deliver.getDescription());
            }

            Event event = new Event()
                    .setSummary(title)
                    .setDescription(order.getFormat());
            if(order.getDate()!=null){


                ZoneId zoneId = ZoneId.systemDefault();
                EventDateTime start = convertToEventDateTime(order.getDate(), zoneId);
                EventDateTime end = convertToEventDateTime(order.getDate().plusHours(1), zoneId);
                event.setStart(start);
                event.setEnd(end);
            }

            String calendarId = service.getCalendarId(userId, "Алиса");
            calendar.events().insert(calendarId, event).execute();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public EventDateTime convertToEventDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Convert to Google's DateTime (which uses milliseconds from epoch)
        DateTime googleDateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());

        // Create EventDateTime object
        return new EventDateTime()
                .setDateTime(googleDateTime)
                .setTimeZone(zoneId.toString());
    }

}
