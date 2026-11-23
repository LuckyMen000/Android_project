# TASKBAR / ТаскБар

**TaskBar — Kotlin / Jetpack Compose Android-менеджер задач, учебный проект для РГР №3 «Тестирование и отладка мобильных приложений в Android».**

Приложение содержит четыре экрана («Сегодня», «Задачи», «Итоги», «Ещё»), категории, поиск, фильтры, приоритеты, CRUD задач, статистику, тёмную/светлую тему, Room и DataStore. Сеть необязательна: Retrofit/OkHttp загружают цитату дня, при ошибке показывается локальный текст.

## РГР №3 — все 15 заданий

В репозитории находятся исходники практической части **каждого задания**:

| Задание | Реализация |
|---|---|
| 1–4 | Debug Lab: Logcat, debugger, NPE/Index/NetworkOnMainThread, Inspector |
| 5 | JUnit4, включая параметризованные граничные тесты |
| 6 | Mockito (`verify`), stub, fake, `runTest`, `StandardTestDispatcher`, Flow |
| 7 | Robolectric: SharedPreferences, ресурсы, Intent |
| 8 | Room in-memory DAO + миграция v1→v2; MockWebServer: 200, 404, 500, timeout, invalid JSON |
| 9 | Compose UI + Espresso Intents + IdlingResource |
| 10 | Page Object, Test Orchestrator, отключение анимаций, изоляция тестов |
| 11 | Матрица устройств, 10 ручных кейсов и Monkey-команда |
| 12 | JaCoCo HTML/XML: проверка **строкового** покрытия domain ≥70%, unit+instrumented |
| 13 | StrictMode, LeakCanary (debug), controlled Activity leak demo, Profiler инструкция |
| 14 | Отдельный Macrobenchmark-модуль + конфигурация Firebase Test Lab на двух устройствах |
| 15 | GitHub Actions: Android Lint, Detekt, unit, JaCoCo и instrumented тесты на эмуляторе, публикация артефактов |

**Руководство по шагам 1–15:** [docs/RGR3_FULL_GUIDE.md](docs/RGR3_FULL_GUIDE.md).  
**10 ручных тест-кейсов:** [docs/TEST_PLAN_10_CASES.md](docs/TEST_PLAN_10_CASES.md).  
**Реестр дефектов:** [docs/DEFECT_REGISTER.md](docs/DEFECT_REGISTER.md).  
**Шаблон отчёта и ответы на контрольные вопросы:** [docs/QUALITY_REPORT_TEMPLATE.md](docs/QUALITY_REPORT_TEMPLATE.md).

> **Важное разграничение:** код/тестовые сценарии подготовлены, но фактические значения покрытия, результаты эмулятора, Profiler, Firebase Test Lab и CI подтверждаются только после реального запуска. В среде подготовки Android SDK недоступен: полный `assembleDebug` ещё нужно запустить в Android Studio или Actions. Ранее пользователь успешно запускал оригинальный TaskBar 1.0, не эту обновлённую версию.

## Запуск в Android Studio

1. Откройте **папку `TaskBar`**.
2. Синхронизируйте Gradle; требуется SDK 35 и JDK 17.
3. Запустите `app` с build variant `debug` на эмуляторе API 26+.
4. Проверьте создание/удаление/редактирование задачи и **Ещё → Лаборатория отладки**.

### Команды

macOS/Linux:

```bash
./gradlew :app:assembleDebug
./gradlew :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageVerify
./gradlew :app:connectedDebugAndroidTest
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

Windows: `./gradlew` → `.\gradlew.bat`.

Скрипты `gradlew`/`gradlew.bat` являются прозрачной заменой официального Gradle wrapper, поскольку исходный проект создавался без wrapper JAR. Они скачивают оригинальный Gradle 8.11.1 при первом запуске. При желании после первого запуска выполните `gradlew wrapper --gradle-version 8.11.1` для стандартного wrapper.

### Конфигурация

- Kotlin 2.1.20; AGP 8.9.2; Gradle 8.11.1; Java 17.
- minSdk 26, targetSdk/compileSdk 35.
- Room 2.7.1 (version 2, явная миграция с version 1), Retrofit 2.11.0, DataStore.
- Debug Lab/LeakCanary/имитация дефектов отсутствуют в `release` APK.
- Бизнес-логика без Android-зависимостей расположена в `domain`, UI — Compose/ViewModel, хранилище и HTTP — `data`.

## GitHub Actions

Автоматический workflow `.github/workflows/android.yml` активируется для каждого **Pull Request** и `push main`, сборки/проверки сохраняют HTML/XML reports как Actions Artifacts. Отдельный ручной `.github/workflows/firebase-test-lab.yml` потребует секреты `GCP_SA_KEY` (сервисный аккаунт) и `GCP_PROJECT_ID`; не добавляйте значения в репозиторий. Конкретные модели выбирайте из актуального `gcloud firebase test android models list`.

**Учебный отчёт:** реальные доказательства Logcat, debugger, inspector и Profiler снимайте на своём устройстве, не подменяя их фиктивными изображениями.
