# Отчёт по РГР №3 «Тестирование и отладка мобильных приложений в Android»

*Шаблон с полями для реальных результатов, а не акт выполненных проверок.*

**Дисциплина:** Технологии разработки мобильных приложений  
**Тема:** Тестирование и отладка мобильных приложений в Android  
**Приложение:** TaskBar 1.1.0-rgr3  
**Автор:** заполнить  
**Группа:** заполнить  
**Год:** 2026  
**GitHub:** https://github.com/LuckyMen000/Android_project

## Цель

Изучить средства диагностики Android Studio, реализовать unit/integration/UI тесты для TaskBar, проверить хранение данных, сеть, качество и производительность, автоматизировать CI.

## Описание приложения и архитектуры

TaskBar — офлайн-менеджер задач: главный экран, задачи, аналитика, настройки, debug-lab. Стек: Kotlin, Jetpack Compose, MVVM, Room 2.7.1, DataStore, Retrofit/OkHttp. Слои: `ui` (экран/ViewModel) → `domain` (валидация, проценты, статистика, Workflow) → `data` (Room, API, preferences). Debug-only лаборатория отделена от release APK.

## Ход выполнения

Для **каждого из 15 заданий** создать подраздел со следующей структурой:  
**Цель** → **Код / инструмент** → **Действия** → **Фактический результат** → **Скриншот / лог** → **Вывод**.  
Пошаговая инструкция в `RGR3_FULL_GUIDE.md`.

| № | Название | Фактически выполнено? | Реальное доказательство |
|---|---|---|---|
| 1 | Logcat | NOT RUN | `01_*.png` |
| 2 | Debugger | NOT RUN | `02_*.png` |
| 3 | Exceptions | NOT RUN | `03_*.png` |
| 4 | Inspectors | NOT RUN | `04_*.png` |
| 5 | JUnit | NOT RUN | HTML JUnit |
| 6 | Mockito / Stub / Fake / Flow | NOT RUN | JUnit logs |
| 7 | Robolectric | NOT RUN | JVM report / speed |
| 8 | Room, migration, MockWebServer | NOT RUN | AndroidTest + JVM reports |
| 9 | Compose UI / Espresso Intents / IdlingResource | NOT RUN | AndroidTest report |
| 10 | Page Object / flaky | NOT RUN | screenshots + 3 runs |
| 11 | 10 manual cases / device matrix / Monkey | NOT RUN | test plan, logs |
| 12 | JaCoCo ≥70% domain | NOT RUN | `app/build/reports/jacoco/rgr3/index.html` |
| 13 | Profiler / LeakCanary / StrictMode | NOT RUN | before/after charts |
| 14 | Macrobenchmark / Firebase | NOT RUN | JSON + Firebase video |
| 15 | PR CI / quality report | NOT RUN | link Actions + artifacts |

## Метрики (заполнять из результатов)

| Метрика | До | После | Среда / способ |
|---|---|---|---|
| JUnit Passed/Failed | — | __/__ | `testDebugUnitTest` |
| Instrumented Passed/Failed | — | __/__ | `connectedDebugAndroidTest` |
| Coverage domain / Line | — | __% | JaCoCo; порог 70% |
| Cold start P50 / P90 | __ ms | __ ms | Macrobenchmark, физический смартфон |
| FrameTime P50 / P90 | __ ms | __ ms | `FrameTimingMetric` |
| Heap retained | __ MB | __ MB | Memory Profiler / LeakCanary |
| CPU / main thread | __ ms | __ ms | CPU Profiler / System Trace |
| Energy | __ | __ | Energy / Power Profiler (если поддерживается) |
| Firebase Test Lab | — | __ из __ | ссылка на тестовый прогон и видео |
| PR workflow | — | __ checks | GitHub Actions run URL |

## Дефекты

См. `DEFECT_REGISTER.md`. Учебные ошибки LAB-01..LAB-04 и реальные дефекты фиксировать раздельно, с приоритетом, статусом, инструментом обнаружения и подтверждением исправления.

## Контрольные вопросы и ответы

**1. Чем unit отличаются от instrumented, интеграционные от UI?** Unit выполняются быстро на JVM и проверяют изолированную бизнес-логику; instrumented запускаются на Android-устройстве/эмуляторе и могут обращаться к Android API, SQLite, реальному UI. Интеграционные тестируют взаимодействие компонентов (например, DAO+Room или Retrofit+MockWebServer), UI-тесты проверяют действия пользователя и отображение результата.

**2. Что такое пирамида тестирования?** Много быстрых unit-тестов в основании, меньше интеграционных посередине, ещё меньше дорогих и потенциально нестабильных UI/end-to-end тестов наверху. Такой баланс обеспечивает скорость обратной связи и защиту критических сценариев.

**3. Mock / stub / fake и изоляция.** Stub возвращает заранее заданный результат; mock также позволяет проверять взаимодействия через `verify`; fake — облегчённая, но работающая реализация (например, in-memory репозиторий). Изоляция позволяет проверять бизнес-правила без реальных БД, сети и случайных факторов.

**4. IdlingResource и flaky.** IdlingResource сообщает тестовому фреймворку, когда фоновые операции завершены. Flaky-тесты иногда падают, иногда проходят при неизменном коде из-за гонок, анимаций, сети, общего состояния и таймингов. Помогают idling, TestDispatcher, изоляция данных и Test Orchestrator.

**5. Утечки памяти.** Основные причины: статическая ссылка на Activity/Context, незакрытые listeners/callbacks, coroutines вне lifecycle, незакрытые ресурсы. Диагностика — LeakCanary, heap dump, анализ retained references; исправление — lifecycle-aware владение ссылками, освобождение ресурсов, использование applicationContext там, где допустимо.

**6. ANR.** Application Not Responding — приложение не обслуживает важные события в установленные Android сроки, например из-за долгого блокирования main thread. Признаки: зависание UI, ANR-диалог/трассы. Диагностика: Android vitals, StrictMode, CPU Profiler/System Trace, traces с блокировками и долгими операциями; решения: вынос IO/вычислений, уменьшение блокировок, правильная работа с корутинами.

**7. Отладка против тестирования.** Тестирование обнаруживает несоответствие ожидаемому поведению и предотвращает регрессии; отладка локализует причину уже обнаруженной ошибки (breakpoints, стек, watch, logs) и помогает исправить её.

## Итог

ЗАПОЛНИТЬ **после реальных проверок**: сколько успешно прошло unit, integration, UI, ручных тестов; процент JaCoCo; сколько дефектов и какие исправлены; метрики CPU/memory/start/frames; статус CI и Firebase. Не выдавать программную заготовку за проведённые измерения.
