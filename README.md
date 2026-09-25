# Цветочный магазин — консольная информационная система

Консольное Java-приложение для управления заказами цветочного магазина.
Реализовано в соответствии с требованиями контрольной работы №1: многослойная
архитектура, JDBC, PostgreSQL, Java Collections Framework, ООП, обработка
исключений.

## Описание предметной области

Система моделирует работу цветочного магазина: ведение базы клиентов,
каталога букетов и обработку заказов на их покупку и доставку.

### Сущности

- **Customer (Клиент)** — участник системы, оформляющий заказы.
- **Bouquet (Букет)** — товар из каталога магазина.
- **BouquetOrder (Заказ букета)** — основная сущность варианта, связывает
  клиента и букет, отслеживает статус выполнения.

### Enum OrderStatus

- **NEW** — заказ создан
- **CONFIRMED** — подтверждён менеджером
- **IN_DELIVERY** — передан в доставку
- **DELIVERED** — доставлен
- **CANCELLED** — отменён


## Архитектура

Console UI → Service → Repository / JDBC → PostgreSQL


| Слой | Пакет | Назначение |
|---|---|---|
| UI | `flowershop.ui` | Консольное меню, ввод/вывод |
| Service | `flowershop.service` | Бизнес-логика и проверки |
| Repository | `flowershop.repository` | JDBC-доступ к данным |
| Model | `flowershop.model` | Сущности предметной области |
| Exception | `flowershop.exception` | Собственные исключения |
| Util | `flowershop.util` | Подключение к БД, экспорт в Excel |

## Структура проекта

```
flower-shop/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── flowershop/
        │       ├── Main.java
        │       ├── model/
        │       │   ├── Customer.java
        │       │   ├── Bouquet.java
        │       │   ├── BouquetOrder.java
        │       │   └── OrderStatus.java
        │       ├── repository/
        │       │   ├── Repository.java
        │       │   ├── CustomerRepository.java
        │       │   ├── CustomerRepositoryJdbc.java
        │       │   ├── BouquetRepository.java
        │       │   ├── BouquetRepositoryJdbc.java
        │       │   ├── OrderRepository.java
        │       │   └── OrderRepositoryJdbc.java
        │       ├── service/
        │       │   ├── CustomerService.java
        │       │   ├── BouquetService.java
        │       │   └── OrderService.java
        │       ├── exception/
        │       │   ├── BusinessException.java
        │       │   ├── EntityNotFoundException.java
        │       │   ├── ValidationException.java
        │       │   └── DatabaseConnectionException.java
        │       ├── ui/
        │       │   ├── ConsoleMenu.java
        │       │   ├── CustomerMenu.java
        │       │   ├── BouquetMenu.java
        │       │   ├── OrderMenu.java
        │       │   └── InputHelper.java
        │       └── util/
        │           ├── DatabaseManager.java
        │           └── ExcelExporter.java
        └── resources/
            ├── config.properties
            ├── schema.sql
            └── seed.sql
```

## Требования для запуска

- **Java 21** (JDK)
- **Maven** 3.9+
- **PostgreSQL** 14+ (используется Postgres.app либо любая другая установка)

Проверка версий:
```bash
java --version
mvn --version
psql --version
```

## Установка и настройка

### 1. Создать базу данных

```bash
createdb flowershop
```

### 2. Настроить подключение

Отредактировать `src/main/resources/config.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/flowershop
db.user=ваш_пользователь_postgres
db.password=ваш_пароль
```

Узнать имя текущего пользователя macOS (если PostgreSQL настроен без отдельного
пароля):
```bash
whoami
```

### 3. Создать структуру таблиц

```bash
psql -d flowershop -f src/main/resources/schema.sql
```

Создаст три связанные таблицы: `customers`, `bouquets`, `orders` — с
`PRIMARY KEY`, `FOREIGN KEY`, `NOT NULL`, `UNIQUE` и `CHECK`-ограничениями.

### 4. Загрузить тестовые данные

```bash
psql -d flowershop -f src/main/resources/seed.sql
```

Добавит 6 клиентов, 6 букетов и 12 заказов во всех пяти статусах.

### 5. Проверить структуру (опционально)

```bash
psql -d flowershop -c "\dt"
psql -d flowershop -c "\d orders"
```

## Сборка и запуск

### Компиляция

```bash
mvn compile
```

### Запуск приложения

```bash
mvn compile exec:java
```

### Сборка самодостаточного .jar (со всеми зависимостями)

```bash
mvn package
java -jar target/flower-shop.jar
```

## Главное меню
========================================
ЦВЕТОЧНЫЙ МАГАЗИН
Клиенты
Букеты
Заказы
Поиск заказов
Фильтрация заказов
Статистика
Экспорт данных
Выход

## Функциональность

### Клиенты / Букеты / Заказы
Полный CRUD: создание, список всех, поиск по ID, изменение, удаление.
Для заказов дополнительно — изменение статуса и сортировка.

### Поиск
- по имени клиента;
- по названию букета.

### Фильтрация
- по статусу заказа;
- по диапазону дат заказа.

### Сортировка
- по дате заказа;
- по сумме заказа.

### Статистика
Всего клиентов, всего букетов, всего заказов, количество заказов по каждому
статусу, общая выручка по доставленным заказам.

### Экспорт
Выгрузка всех данных (клиенты, букеты, заказы) в один Excel-файл
(`.xlsx`) с тремя листами. Файл сохраняется в корне проекта с именем
`flower_shop_export_<дата>.xlsx`.

## Бизнес-правила

1. Нельзя создать клиента без имени, телефона или с некорректным email.
2. Нельзя создать букет без названия или с ценой ≤ 0.
3. Нельзя оформить заказ на несуществующего клиента или букет.
4. Количество букетов в заказе должно быть положительным.
5. Дата доставки не может быть раньше даты заказа.
6. Сумма заказа рассчитывается автоматически (цена букета × количество),
   а не вводится вручную.
7. Запрещены произвольные переходы статуса заказа — допустимая схема:
   `NEW → CONFIRMED → IN_DELIVERY → DELIVERED`, а также переход в
   `CANCELLED` из `NEW`, `CONFIRMED` или `IN_DELIVERY`. Из `DELIVERED`
   и `CANCELLED` статус изменить нельзя.
8. Нельзя удалить букет из каталога, если по нему есть незавершённые
   заказы (статус не `DELIVERED` и не `CANCELLED`).

## Обработка ошибок

Приложение не завершается аварийно при:
- вводе текста вместо числа (`ValidationException`);
- обращении к несуществующей записи (`EntityNotFoundException`);
- нарушении бизнес-правила (`BusinessException`);
- ошибках подключения к БД или выполнения SQL-запроса
  (`DatabaseConnectionException`).

Все ошибки перехватываются в `ConsoleMenu` и выводятся пользователю
понятным сообщением, после чего работа продолжается.

## Технологии

- Java 21
- Maven
- PostgreSQL + JDBC (драйвер `org.postgresql:postgresql`)
- Apache POI (экспорт в `.xlsx`)

## Авторы

Команда: Брюханова С. И., Берг К. В., Бельская В. С.
Группа: ЭФБО-02-24
Вариант: Цветочный магазин — Заказ букета