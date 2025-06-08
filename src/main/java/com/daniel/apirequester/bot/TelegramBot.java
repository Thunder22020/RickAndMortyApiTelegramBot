package com.daniel.apirequester.bot;


import com.daniel.apirequester.exceptions.UserDoesNotExistException;
import com.daniel.apirequester.model.db.User;
import com.daniel.apirequester.model.db.UserMessage;
import com.daniel.apirequester.service.UserMessageService;
import com.daniel.apirequester.service.UserService;
import lombok.SneakyThrows;
import com.daniel.apirequester.model.Person;
import com.daniel.apirequester.model.Location;
import com.daniel.apirequester.service.ApiService;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot {
    private final ApiService apiService;
    private final String botName;
    private final UserService userService;
    private final UserMessageService userMessageService;

    public TelegramBot(
            DefaultBotOptions options,
            String botToken,
            ApiService apiService,
            String botName, UserService userService, UserMessageService userMessageService
    ) {
        super(options, botToken);
        this.apiService = apiService;
        this.botName = botName;
        this.userService = userService;
        this.userMessageService = userMessageService;
    }

    private void sendSafeMessage(String chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkIfUserExists(Long chatId) throws UserDoesNotExistException {
        if (userService.read(chatId) == null) {
            sendSafeMessage(chatId.toString(), "Вы еще не авторизованы. Напишите команду '/start'.");
            throw new UserDoesNotExistException();
        }
    }

    private void handlePersonCommand(Update update) {
        try {
            checkIfUserExists(update.getMessage().getChatId());
        } catch (UserDoesNotExistException ex) {
            return;
        }

        User user = userService.read(update.getMessage().getChatId());
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        try {
            int id = Integer.parseInt(message.split(" ")[1]);

            Person person = apiService.readPerson(id);

            if (person == null) {
                throw new TelegramApiException();
            }

            SendPhoto photo = buildPersonPhotoMessage(chatId, person);

            execute(photo);

            UserMessage userMessage = new UserMessage();
            userMessage.setMessage(message);
            userMessage.setTimestamp(LocalDateTime.now());
            userMessage.setUser(user);
            userService.createMessage(user, userMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            sendSafeMessage(chatId, "No id found");
        }
    }

    private void handleLocationCommand(Update update) {
        try {
            checkIfUserExists(update.getMessage().getChatId());
        } catch (UserDoesNotExistException ex) {
            return;
        }

        User user = userService.read(update.getMessage().getChatId());
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        try {
            int id = Integer.parseInt(message.split(" ")[1]);

            Location location = apiService.readLocation(id);
            if (location == null) {
                throw new TelegramApiException();
            }

            SendMessage sm = buildLocationMessage(chatId, location);

            sendApiMethod(sm);

            UserMessage userMessage = new UserMessage();
            userMessage.setMessage(message);
            userMessage.setTimestamp(LocalDateTime.now());
            userMessage.setUser(user);
            userService.createMessage(user, userMessage);
        } catch (Exception ex) {
            sendSafeMessage(chatId, "No id found");
        }
    }

    private void handleInitialCommand(Long chatId) {
        String initialText = "Добро пожаловать в бота!";

        if (userService.read(chatId) != null) {
            initialText += " Вы уже были добавлены ранее.";
        } else {
            userService.create(new User(chatId));
            initialText += " Вы успешно добавлены.";
        }

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(initialText);

        sendSafeMessage(chatId.toString(), initialText);
    }

    private void handleHistoryCommand(Update update) {
        StringBuilder result = new StringBuilder();
        result.append("История вызванных команд:\n");
        List<UserMessage> userMessages = userMessageService.findAll();
        for (int i = 0; i < (Math.min(userMessages.size(), 30)); i++) {
            LocalDateTime ts = userMessages.get(i).getTimestamp();
            result.append(i+1).append(") ").append(userMessages.get(i).getMessage())
                    .append(", time: ").append(ts.format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")))
                    .append("\n");
        }

        sendSafeMessage(update.getMessage().getChatId().toString(), result.toString());
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

    private void handleCommand(Update update) {
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        new Thread(() -> {
            if (message.startsWith("/start")) {
                handleInitialCommand(update.getMessage().getChatId());
            } else if (message.startsWith("/person")) {
                handlePersonCommand(update);
            } else if (message.startsWith("/location")) {
                handleLocationCommand(update);
            } else if (message.equals("/get_command_history")) {
                handleHistoryCommand(update);
            } else {
                sendSafeMessage(chatId, "No command found");
            }
        }).start();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
