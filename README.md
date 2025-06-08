# Rick and Morty Telegram Bot

A simple Spring Boot-based Telegram bot that interacts with the [Rick and Morty API](https://rickandmortyapi.com) to retrieve and display character and location data.

## 🚀 Features

- Get information and image of a character by ID
- Get information about a location by ID
- Asynchronous command handling
- Uses `RestClient` to fetch data from external API

## 📜 Commands

- `/person <id>` — Fetches character info (name, origin, status, species) and photo.
- `/location <id>` — Fetches location info (name, dimension, type).
- `/get_command_history` — Fetches history of commands that current user have written.

## 🛠️ Technologies Used

- Java 17+
- Spring Boot
- TelegramBots API
- RestClient (Spring)
- Lombok
- JPA & Hibernate, PostgreSQL

## 🧠 Project Structure

```
src/
└── java/
    └── com/
        └── daniel/
            └── apirequester/
                ├── bot/          # Telegram-бот
                ├── config/       # Конфигурации приложения
                ├── controller/   # REST-контроллер
                ├── model/        # Модели
                ├── service/      # Сервисный слой (взаимодействие с API)
                └── ApiRequesterApplication.java  # Главный класс Spring Boot

```
