# TASKBAR / ТаскБар

**TaskBar — стильный нативный Android-планировщик на Kotlin + Jetpack Compose.**

Подготовлен как **основное приложение** для РГР №3 «Тестирование и отладка мобильных приложений в Android». Полный набор из 15 практических упражнений (JaCoCo, UI/интеграционные тесты, профилирование, Firebase Test Lab, CI) — следующий этап работы, а не заявленная готовая часть этого архива.

## Возможности версии 1.0

- Четыре экрана: Сегодня, Задачи, Итоги, Настройки.
- Добавление, изменение, удаление и завершение задач.
- Категории: Работа, Учёба, Личное, Здоровье.
- Приоритеты: низкий, средний, высокий.
- Дата и время задачи, недельная лента, поиск, фильтры и сортировка.
- Общий прогресс, статистика по категориям, график завершения задач по дням.
- Тёмная/светлая тема (настройка сохраняется в DataStore).
- Room (SQLite), Flow, ViewModel и Coroutines — задачи сохраняются после закрытия приложения.
- Необязательная «Мысль дня» через публичный HTTPS API DummyJSON с безопасной офлайн-заглушкой.
- При первом запуске добавляется 6 демонстрационных задач, их можно отредактировать/удалить. Повторно сами собой они не появятся.
- Подтверждение перед удалением, сообщения о пустом списке, валидация формы.

## Запуск

1. Установите **Android Studio** и SDK Platform **API 35**. Рекомендуется JDK 17 (Android Studio обычно содержит встроенный JDK).
2. Распакуйте архив и откройте **папку `TaskBar`**: File → Open.
3. Дождитесь Gradle Sync (первое открытие требует интернета, чтобы скачать Gradle/зависимости).
4. Запустите модуль `app` на эмуляторе API 26+ или Android-устройстве с Android 8+.
5. При первом запуске приложение содержит демонстрационные задачи; можно сразу проверять интерфейс.

### Зависимости и система сборки

- Android Gradle Plugin 8.9.2 / Gradle 8.11.1, Kotlin 2.1.20 + Compose Compiler plugin.
- Compose BOM 2025.04.01, Room 2.7.1, DataStore 1.1.4, Retrofit 2.11.0.
- `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26`, JDK 17.
- **В данной среде нет Android SDK и доступа к Gradle Distribution**, поэтому APK и Gradle-wrapper.jar не приложены, а фактическая Android-сборка здесь не могла быть проведена. В комплекте находятся прозрачные скрипты `gradlew` / `gradlew.bat`, которые при первом запуске скачивают официальную дистрибуцию Gradle 8.11.1; для этого нужен интернет. Никакой APK не прилагается.

Важное замечание: это не официальный Gradle Wrapper (JAR отсутствует в архиве). На Windows `gradlew.bat` запускает PowerShell-скрипт `gradle-bootstrap.ps1`, который скачивает официальный Gradle 8.11.1 и запускает нужную команду; `gradlew` делает то же в Linux/macOS. Если Android Studio требует **именно стандартный** wrapper, после загрузки Gradle выполните `.\gradlew.bat wrapper --gradle-version 8.11.1` в терминале Android Studio, и Gradle создаст официальный `gradle-wrapper.jar` и wrapper-скрипты.

## Архитектура

```text
app/src/main/java/kz/taskbar/app/
├── MainActivity.kt              # Compose root, 4 вкладки, нижняя панель, диалоги
├── TaskBarApplication.kt       # Инициализация зависимостей
├── data/
│   ├── TaskEntity.kt            # Room Entity
│   ├── TaskDao.kt               # SQL и Flow
│   ├── TaskDatabase.kt          # Room database
│   ├── TaskRepository.kt        # Репозиторий через интерфейс
│   ├── PreferencesRepository.kt # DataStore: тема, onboarding
│   ├── QuoteRepository.kt       # Retrofit + офлайн-fallback
│   └── DemoTasks.kt             # 6 демонстрационных задач
├── domain/
│   └── TaskModels.kt            # Категории, приоритеты, валидация, даты
└── ui/
    ├── TaskViewModel.kt         # ViewModel и StateFlow
    ├── theme/TaskBarTheme.kt    # Две цветовые темы
    ├── screens/                  # Today, Tasks, Insights, Settings
    └── components/              # TaskItem, TaskEditorSheet, графические элементы
```

## Подготовка к РГР №3

Для отладки и проверки уже имеются тестируемые модули и сценарии:

| Задание | Основа в проекте |
|---|---|
| 1–3: Logcat, debugger, ошибки | ViewModel, валидаторы, сеть и Room |
| 4: Inspectors | Compose Layout + Room + HTTPS |
| 5–7: unit, mock, Robolectric | TaskValidator, интерфейс TaskRepository, DataStore |
| 8: Room + MockWebServer | TaskDao, QuoteApi, Retrofit |
| 9–10: UI тесты | Сценарии ввода, фильтра, завершения, переходов |
| 11: ручная матрица | 4 экрана, разные темы, даты, ориентация, офлайн |
| 12–15: качество и CI | Следующий этап: JaCoCo, Profiler, benchmarks, GitHub Actions |

> Для лабораторной недостаточно только запустить этот проект. Скриншоты реальных инструментов, найденные дефекты, метрики и полная тестовая инфраструктура должны быть собраны в процессе следующего этапа.

## Быстрые команды в Android Studio Terminal

В терминале Android Studio (при первом запуске Gradle скачивается автоматически):

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

Путь к собранному APK (после успешной сборки): `app/build/outputs/apk/debug/app-debug.apk`.

## Разрешения

`INTERNET` — только для карточки случайной цитаты. Задачи не передаются на сервер приложением; резервное копирование Android для приложения отключено.

Подробная пошаговая инструкция для Windows: **`START_HERE_WINDOWS.md`**.
