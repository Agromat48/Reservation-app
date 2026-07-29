# Reservation System

REST-приложение для управления бронированиями, разработанное на **Spring Boot** с использованием **Spring Data JPA** и **PostgreSQL**. Проект предоставляет API для создания, поиска, изменения и проверки доступности бронирований.

## Возможности

- Создание бронирований
- Получение списка бронирований
- Поиск бронирований по параметрам
- Изменение статуса бронирования
- Проверка доступности объекта на выбранные даты
- Валидация входящих данных
- Глобальная обработка ошибок
- Работа с PostgreSQL через Spring Data JPA

## Используемые технологии

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Hibernate ORM
- PostgreSQL
- Maven
- Jakarta Validation

## Структура проекта

```
reservation-system
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.reservation
│   │   │       ├── ReservationSystemApplication.java
│   │   │       ├── reservations
│   │   │       │   ├── Reservation.java
│   │   │       │   ├── ReservationEntity.java
│   │   │       │   ├── ReservationRepository.java
│   │   │       │   ├── ReservationService.java
│   │   │       │   ├── ReservationController.java
│   │   │       │   ├── ReservationMapper.java
│   │   │       │   ├── ReservationSearchFilter.java
│   │   │       │   ├── ReservationStatus.java
│   │   │       │   └── availability
│   │   │       │       ├── ReservationAvailabilityController.java
│   │   │       │       ├── ReservationAvailabilityService.java
│   │   │       │       ├── CheckAvailabilityRequest.java
│   │   │       │       ├── CheckAvailabilityResponse.java
│   │   │       │       └── AvailabilityStatus.java
│   │   │       └── web
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── ErrorResponseDto.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│
└── pom.xml
```

## Архитектура

Проект реализован по классической многослойной архитектуре.

- **Controller** — обработка HTTP-запросов.
- **Service** — бизнес-логика приложения.
- **Repository** — взаимодействие с базой данных.
- **Entity** — описание таблиц базы данных.
- **Mapper** — преобразование Entity в DTO и обратно.
- **Validation** — проверка корректности входящих данных.
- **Exception Handler** — централизованная обработка ошибок.

## Основной функционал

### Бронирования

Приложение позволяет:

- создавать новое бронирование;
- получать список всех бронирований;
- искать бронирования по фильтрам;
- изменять статус бронирования;
- получать информацию о конкретном бронировании.

### Проверка доступности

Отдельный сервис позволяет определить возможность бронирования объекта на указанный период.

Возвращается один из возможных статусов доступности.

## Запуск проекта

### 1. Клонировать репозиторий

```bash
git clone https://github.com/Agromat48/Reservation-app.git
```

### 2. Настроить PostgreSQL

Создать базу данных и указать параметры подключения в файле

```
src/main/resources/application.properties
```

Например:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reservation_db
spring.datasource.username=postgres
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
```

### 3. Собрать проект

```bash
mvn clean install
```

### 4. Запустить приложение

```bash
mvn spring-boot:run
```

или выполнить запуск класса

```
ReservationSystemApplication.java
```

из среды разработки.

## REST API

Основные конечные точки приложения:

### Reservation API

- создание бронирования;
- получение списка бронирований;
- получение бронирования по идентификатору;
- изменение статуса;
- поиск бронирований по фильтрам.

### Availability API

- проверка доступности объекта на выбранные даты.

## Обработка ошибок

В проекте реализован глобальный обработчик исключений, который возвращает единый формат ошибок при возникновении исключительных ситуаций или некорректных запросов.

## Цель проекта

Проект создан в учебных целях для изучения:

- разработки REST API на Spring Boot;
- работы с PostgreSQL;
- использования Spring Data JPA и Hibernate;
- реализации многослойной архитектуры;
- обработки исключений;
- валидации данных;
- построения системы управления бронированиями.

## Автор

GitHub: https://github.com/Agromat48
