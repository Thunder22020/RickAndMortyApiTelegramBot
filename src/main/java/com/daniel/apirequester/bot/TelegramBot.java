package com.daniel.apirequester.bot;


import lombok.SneakyThrows;
import com.daniel.apirequester.model.Person;
import com.daniel.apirequester.model.Location;
import com.daniel.apirequester.service.ApiService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class TelegramBot extends TelegramLongPollingBot {
    private final ApiService apiService;

    public TelegramBot(
            DefaultBotOptions options,
            String botToken,
            ApiService apiService
    ) {
        super(options, botToken);
        this.apiService = apiService;
    }

    private void sendSafeMessage(String chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePersonCommand(String chatId, String message) {
        try {
            int id = Integer.parseInt(message.split(" ")[1]);

            Person person = apiService.readPerson(id);

            if (person == null) {
                throw new TelegramApiException();
            }

            SendPhoto photo = buildPersonPhotoMessage(chatId, person);

            execute(photo);
        } catch (Exception ex) {
            sendSafeMessage(chatId, "No id found");
        }
    }

    private void handleLocationCommand(String chatId, String message) {
        try {
            int id = Integer.parseInt(message.split(" ")[1]);

            Location location = apiService.readLocation(id);
            if (location == null) {
                throw new TelegramApiException();
            }

            SendMessage sm = buildLocationMessage(chatId, location);

            sendApiMethod(sm);
        } catch (Exception ex) {
            sendSafeMessage(chatId, "No id found");
        }
    }

    private static SendPhoto buildPersonPhotoMessage(String chatId, Person person) {
        String personInfo = String.format(
                "Name: %s\nOrigin: %s\nStatus: %s\nSpecies: %s",
                person.getName(),
                person.getOrigin().getName(),
                person.getStatus(),
                person.getSpecies()
        );
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(person.getImage()));
        photo.setCaption(personInfo);
        return photo;
    }

    private static SendMessage buildLocationMessage(String chatId, Location location) {
        SendMessage sm = new SendMessage();
        String locationInfo = String.format(
                "Name: %s\nDimension: %s\nType: %s",
                location.getName(),
                location.getDimension(),
                location.getType()
        );
        sm.setText(locationInfo);
        sm.setChatId(chatId);
        return sm;
    }

    private void handleCommand(String message, String chatId) {
        new Thread(() -> {
            if (message.startsWith("/person")) {
                handlePersonCommand(chatId, message);
            } else if (message.startsWith("/location")) {
                handleLocationCommand(chatId, message);
            } else {
                sendSafeMessage(chatId, "No command found");
            }
        }).start();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            handleCommand(message, chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "javaspring12_bot";
    }
}
