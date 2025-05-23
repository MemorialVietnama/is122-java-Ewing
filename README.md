# Виртуальное моделирование работы банкомата на JavaFX  
# Virtual Simulation of ATM Operation on JavaFX

![JavaFX](https://img.shields.io/badge/JavaFX-17-blue) ![Maven](https://img.shields.io/badge/Maven-3.6-green)

Этот проект представляет собой виртуальную симуляцию работы банкомата, созданную с использованием JavaFX. Он позволяет пользователям взаимодействовать с интерфейсом банкомата, выполнять базовые операции (например, проверка баланса, снятие наличных и пополнение счета).

## Содержание
1. [Требования](#требования)
2. [Установка](#установка)
3. [Запуск проекта](#запуск-проекта)
4. [Лицензия](#лицензия)
5. [Помощь](#вклад-в-проект)

---

## Требования

Для успешной работы с этим проектом вам понадобится:
- **Java Development Kit (JDK)** версии 11 или выше.
- **Maven** версии 3.6 или выше.
- **Git** для клонирования репозитория.
- (Опционально) IDE, например IntelliJ IDEA или Eclipse.
- СУБД для чтения Firebird Файла Базы данных (Firebird 4x) - Например Red Expert

---

## Установка

1. **Клонирование репозитория**  
   Клонируйте проект с помощью Git:
   ```bash
   git clone https://github.com/username/repository.git
   cd repository
   ```
2. Установка зависимостей
Убедитесь, что у вас установлен Maven. Затем выполните команду для загрузки всех зависимостей:
  ```bash
  mvn clean install
  ```
  Эта команда соберет проект и загрузит все необходимые библиотеки из центрального репозитория Maven.

## Запуск проекта
1. Запуск через Maven
   ```bash
   mvn javafx:run
   ```
2. Запуск JAR-файла
   ```bash
   java -jar target/atm-simulation.jar
   ```

## Лицензия
Этот проект распространяется под лицензией [MIT License](#https://chat.qwen.ai/c/LICENSE?spm=a2ty_o01.29997173.0.0.6d43c92180qtmZ) .
Подробнее о лицензии можно узнать в файле LICENSE.

## Вклад в проект
Если вы хотите внести свой вклад в проект:

- Создайте fork репозитория.
- Внесите изменения в своей ветке.
- Отправьте pull request с описанием изменений.
