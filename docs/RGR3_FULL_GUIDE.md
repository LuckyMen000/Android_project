# TaskBar — РГР №3. Инструкция по ВСЕМ 15 заданиям

Дисциплина: **Технологии разработки мобильных приложений**. Тема: **Тестирование и отладка мобильных приложений в Android**.

**Принцип:** все задания выполняются на одном Android-приложении `kz.taskbar.app`. Debug-only лаборатория доступна в **Ещё → Лаборатория отладки**. Сборка `release` не содержит лаборатории и намеренных ошибок. TaskBar хранит задачи в Room, настройки в DataStore; сеть используется для цитат, доступен fallback.

**Честный статус:** сценарии и автоматизация подготовлены в исходниках. Скриншоты Android Studio, фактические JUnit/AndroidTest/CI результаты, измерения CPU/Memory/Energy, Firebase video и результаты Macrobenchmark получаются только **после реального запуска**. Не выдавайте их за выполненные до запуска.

## Что требуется на компьютере

- Android Studio с установленными Android SDK 35, JDK 17, эмулятором API 34 или выше; проект поддерживает API 26+.
- Интернет для первого скачивания зависимостей Gradle (первый запуск старого проекта уже проходил).
- GitHub репозиторий и доступ в Actions. Firebase Test Lab требует GCP-проект, включённый биллинг (проверить стоимость), gcloud CLI и разрешения.

### Команды

На macOS/Linux в корне проекта:

```bash
chmod +x gradlew
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug :app:detekt
./gradlew :app:rgr3CoverageReport :app:rgr3CoverageVerify
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:rgr3CombinedCoverageReport
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

На Windows замените `./gradlew` на `.\gradlew.bat`. Последние три команды требуют Android-устройство/эмулятор. `:benchmark:connectedBenchmarkAndroidTest` лучше выполнять на **реальном устройстве** с неотладочной `benchmark` сборкой; возможны отличия имени задачи, проверьте `./gradlew :benchmark:tasks --all`.

Каталог автоматических результатов: `app/build/reports/`, `app/build/test-results/`, `benchmark/build/outputs/`. Явные `evidence/rgr3/*.png` нужно сохранить самостоятельно: изображения не генерируются программой.

---

## 1. Logcat и логирование

**Код:** `diagnostics/TaskBarLog.kt`, `data/TaskRepository.kt`, `data/QuoteRepository.kt`, `src/debug/.../DebugLabActivity.kt`.

1. Запустите `debug` и откройте лабораторию. Нажмите кнопки `DEBUG`, `INFO`, `WARN`, `ERROR`.
2. В Logcat по очереди примените `tag:TB_LAB`, `package:kz.taskbar.app`, `level:ERROR` / `level:WARN` / `level:DEBUG` (или фильтры соответствующей версии IDE).
3. Добавьте, завершите, удалите задачу; проверьте тег `TB_TASK`.
4. Покажите код, где `BuildConfig.DEBUG` предотвращает вывод debug логов в `release`, включая `HttpLoggingInterceptor.Level.NONE`.

**Скриншоты:** `01_tag.png`, `01_levels.png`, `01_package.png`, `01_release_code.png`.

## 2. Отладчик: обычный, условный breakpoint, logpoint

**Код:** debug-only `calculateBuggyPercent(2,6)` в `DebugLabActivity.kt`, исправленный `ProgressCalculator.completionPercent(2,6)`.

1. Начните **Debug 'app'**. Установите обычный breakpoint на `(completed / safeTotal) * 100`.
2. В лаборатории нажмите «Запустить расчёт». Изучите стек, `Step Into`, `Step Over`, `Step Out`; добавьте `Watches`: `completed`, `safeTotal`, `result`.
3. В `Evaluate Expression`: сравните `completed / safeTotal` (0) и `completed.toDouble() / safeTotal` (~0.3333).
4. Добавьте conditional breakpoint `total == 6`, затем logpoint c выражением `"buggy=" + result`.
5. Приложите скриншот **ошибка 0% → правильный расчёт 33%**. Ошибка намеренно остаётся только в debug-лаборатории; production-функция исправлена.

**Скриншоты:** `02_breakpoint.png`, `02_watch.png`, `02_evaluate.png`, `02_conditional.png`, `02_logpoint.png`.

## 3. NullPointer, IndexOutOfBounds, NetworkOnMainThread

**Код:** `DebugLabActivity.kt`, обработчики трёх исключений + `NetworkQuoteRepository`.

1. Включите **Break on Exceptions** (включая caught exceptions).
2. Нажмите последовательно NPE, IndexOutOfBounds и NetworkOnMainThread в лаборатории.
3. Зафиксируйте **реальный** stack trace с типом, строкой и причиной каждого исключения. Если сеть заблокирована, NetworkOnMainThread может проявиться иначе; не приписывайте результат без лога.
4. Покажите исправления: `getOrNull` / null-check; сетевое обращение через `suspend Retrofit` и `Dispatchers.IO`.

**Скриншоты:** `03_npe.png`, `03_index.png`, `03_network.png`, `03_fix.png`.

## 4. Layout Inspector / Database Inspector / Network Inspector

1. Layout Inspector: в лаборатории включите «Дефект вёрстки», найдите `layout_defect` (два перекрытых текста). Выключите, найдите `layout_fixed`.
2. Database Inspector: `App Inspection → Database Inspector`, база `taskbar.db`, таблица `tasks`. SQL: `SELECT id, title, completed, priority, archived FROM tasks ORDER BY id DESC LIMIT 10;`.
3. Network Inspector: при активном интернете вызовите `GET https://dummyjson.com/quotes/random` из лаборатории, изучите request/response, status и latency. Если ответ офлайн, повторите при доступной сети.

**Скриншоты:** `04_layout_before.png`, `04_layout_after.png`, `04_room.png`, `04_http.png`.

## 5. Unit / JUnit4 (минимум 10)

**Код:** `app/src/test/java/.../domain/` — `TaskValidatorTest`, `TaskValidatorParameterizedTest`, `ProgressCalculatorTest`, `TimeAndEnumTest`, `TaskStatisticsTest` и другие.

- Arrange → Act → Assert, проверки пустых, максимальных и превышающих длину значений.
- `@RunWith(Parameterized::class)` на 8 входных вариантах.
- `./gradlew :app:testDebugUnitTest` генерирует HTML и XML отчёты. Сохраните **фактическое число успешных/проваленных тестов** из Test Results.

**Скриншоты:** `05_run.png`, `05_parameterized.png`.

## 6. Моки, stub, fake, verify; корутины и Flow

**Код:** `TaskWorkflowMockitoTest`, `TaskWorkflowFakeTest`, `QuoteRepositoryMockitoTest`.

- Mockito mock `TaskRepository`, mock `QuoteApi` и `verify` для `save`, `setCompleted`, `delete`, `randomQuote`.
- Stub: `whenever(...).thenReturn(...)` задаёт предсказуемый ответ.
- Fake: `FakeTasks` ведёт настоящую `MutableStateFlow` в оперативной памяти.
- `runTest`, `StandardTestDispatcher(testScheduler)`, `advanceUntilIdle` используются для корутин; не допускаются произвольные `Thread.sleep`.

**Скриншоты:** `06_mocks.png`, `06_fakes.png`.

## 7. Robolectric (JVM, без эмулятора)

**Код:** `AndroidContextRobolectricTest`.

Тестирует `SharedPreferences` через `LegacySettings` (контекстная совместимость), `R.string.app_name` и `Intent` на `MainActivity`. Современные рабочие настройки хранятся в DataStore. Сравните реальное время запуска Robolectric/JVM и `connectedDebugAndroidTest` при одинаковых условиях, укажите устройство и число тестов. Не называйте времена из примеров «измеренными».

**Скриншоты:** `07_robolectric.png`, `07_speed_comparison.png`.

## 8. Room + миграция + MockWebServer

**Код:** `TaskDaoInstrumentedTest` (in-memory DAO CRUD, Flow, очистка), `RoomMigrationInstrumentedTest` (настоящая SQLite-БД схемы v1 → Room v2 без потери task), `QuoteRepositoryHttpTest` (HTTP 200, 404, 500, задержка больше таймаута, невалидный JSON).

Миграция `TaskDatabase.MIGRATION_1_2` добавляет `archived INTEGER NOT NULL DEFAULT 0`. Это **не разрушительная миграция**. Предыдущая версия TaskBar имела `exportSchema=false`, поэтому тест создаёт честную v1 SQLite-схему, а не поддельный JSON с идентификатором Room.

**Скриншоты:** `08_dao.png`, `08_migration.png`, `08_http_scenarios.png`.

## 9. Compose UI + Espresso Intents + IdlingResource

**Код:** `TaskBarComposeUiTest`, `TaskBarEspressoIntentsTest`, `TaskBarIdlingResource`.

- Форма → ввод названия → кнопка «Создать» → поиск → проверка видимости задачи → навигация по вкладкам.
- Поскольку интерфейс написан на **Jetpack Compose**, вместо `onView/perform/check` Espresso используются аналоги `onNodeWithTag/perform/assert`, а Espresso **Intents** проверяет явный переход в debug-lab.
- `TaskOperationTracker` отслеживает асинхронные операции, `TaskBarIdlingResource` синхронизирует Espresso, `TaskBarComposeIdle` синхронизирует Compose, без `sleep`.

**Скриншоты:** `09_create_flow.png`, `09_intents.png`, `09_idling.png`.

## 10. Page Object, борьба с flaky-тестами

**Код:** `TaskBarPage.kt` (все селекторы и действия), `TaskBarComposeUiTest`.

1. UI сценарии используют Page Object и стабильные `Modifier.testTag`.
2. `testOptions.animationsDisabled=true`, `ANDROIDX_TEST_ORCHESTRATOR`, аргумент `clearPackageData=true` изолируют состояние между кейсами.
3. `IdlingResource` отслеживает реальные операции БД/API, тесты не используют фиксированных пауз.
4. Повторите `connectedDebugAndroidTest` минимум 3 раза, запишите результат. Реальные flaky-дефекты описывайте только после подтверждения.

**Скриншоты:** `10_page_object.png`, `10_orchestrator.png`, `10_repeat_runs.png`.

## 11. Матрица устройств, ручной тест-план, Monkey

**Полные 10 кейсов:** `docs/TEST_PLAN_10_CASES.md`. Минимально проверяем: API 26/30/34/35 или фактически доступные, узкий/широкий экран, portrait/landscape, dark/light, русский/системный язык, сворачивание, поворот, убийство процесса и отсутствие сети. Если какой-то API недоступен, явно укажите `не проверялось`.

Команда Monkey (на подключённом устройстве):

```bash
adb shell monkey -p kz.taskbar.app --throttle 200 -v 1000 > evidence/rgr3/11_monkey.txt 2>&1
```

В конце отчёта зафиксируйте `Events injected`, `crash/ANR` и найденные дефекты.

**Скриншоты:** `11_matrix.png`, `11_orientation.png`, `11_dark.png`, `11_offline.png`, `11_monkey.png`.

## 12. JaCoCo: ≥70% business/domain

`debug.enableUnitTestCoverage=true`, `enableAndroidTestCoverage=true`. Gradle tasks:

```bash
./gradlew :app:testDebugUnitTest :app:rgr3CoverageReport :app:rgr3CoverageVerify
./gradlew :app:connectedDebugAndroidTest :app:rgr3CombinedCoverageReport
```

HTML: `app/build/reports/jacoco/rgr3/index.html`; `rgr3CoverageVerify` **падает**, если доля покрытых **строк** в классе `kz.taskbar.app.domain` ниже 70%. Отдельный merged-отчёт учитывает `.exec` unit-тестов и `.ec` instrumented-тестов, если они запущены. В отчёт вставьте измеренный процент и скриншот HTML. Этот порог относится именно к **бизнес-логике в domain**, не ко всему Compose UI.

**Почему покрытие не равно качеству:** тест может исполнить строку, но не проверить результат; не охваченные входные комбинации, гонки и интерфейсные сценарии сохраняются.

## 13. Android Profiler, LeakCanary, StrictMode

1. В `TaskBarApplication.onCreate()` debug-вариант включает `StrictMode` для main-thread disk/network и VM-утечек.
2. `debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")` автоматически отслеживает уничтоженные Activity; при обнаружении настоящей утечки сохраните отчёт LeakCanary.
3. В лаборатории секция **MEMORY & STRICTMODE**: «Создать демонстрационную утечку Activity» удерживает `Activity` в singleton. Закройте экран; LeakCanary обнаруживает удержание после уничтожения Activity (может потребоваться GC). Затем повторите после «Исправить: освободить Activity» и убедитесь в отсутствии этой ссылки. В релизную сборку пример не входит.
4. В Profiler запишите **CPU**, **Memory** (heap dump, retained objects), **Network**, **Energy/Power** (если текущая IDE предоставляет соответствующий инструмент; иначе System Trace/Power Profiler). Запустите нагрузку (поиск задач, прокрутка, многократное создание).
5. Проверьте Logcat по `StrictMode`, исключите из реального слоя все нарушения, возникшие при работе TaskBar (нарушения, вызванные намеренной debug-демонстрацией, отдельно помечайте).
6. Запишите **фактические до/после**: CPU main-thread latency, retained Activity, heap MB, network requests, энергия при доступности прибора. До запуска измерений поле остаётся пустым.

**Скриншоты:** `13_leak_before.png`, `13_leak_fixed.png`, `13_cpu.png`, `13_heap.png`, `13_energy.png`, `13_strictmode.png`.

## 14. Macrobenchmark + Firebase Test Lab

Модуль `:benchmark` содержит `TaskBarMacrobenchmark`:
- `coldStart`: `StartupTimingMetric`, 5 итераций, `StartupMode.COLD`.
- `taskListScroll`: `FrameTimingMetric`, 5 итераций, открытие приложения в `setupBlock` (`startupMode=null`) и UI Automator.

`app` имеет отдельный **non-debuggable `benchmark`** build type, `profileable android:shell=true`, `androidx.profileinstaller` и Compose `testTagsAsResourceId` для UI Automator. Для реальных, значимых метрик использовать **физический смартфон**; на эмуляторе результат ориентировочный.

```bash
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

Отдельный `scripts/run-firebase-test-lab.sh` и **GitHub Actions → Firebase Test Lab (manual)** запускают instrumentation APK на **двух разных моделях**, заданных пользователем после проверки доступности `gcloud firebase test android models list`. Нужны GCP credentials и разрешённое использование Firebase. Сохраните **реальные** отчёт, логи и видео из Test Lab. Пока Firebase не запускался, фиксируем `не запускалось`, не `успешно`.

**Скриншоты:** `14_startup.png`, `14_frametime.png`, `14_firebase_matrix.png`, `14_firebase_video.png`.

## 15. GitHub Actions, отчёт качества

`/.github/workflows/android.yml` при **каждом PR и push в main** запускает:
- `:app:assembleDebug`;
- `:app:lintDebug` (**Android Lint**) и `:app:detekt` (Kotlin-анализ);
- `:app:testDebugUnitTest` + `:app:rgr3CoverageVerify` (порог 70%);
- `:app:connectedDebugAndroidTest` на эмуляторе Android API 34;
- публикацию artifacts HTML/XML, test-results, APK и `.ec` coverage, даже при ошибке.

`/.github/workflows/firebase-test-lab.yml` — **ручной**, поскольку облачные проверки требуют credentials, выбора реальных устройств и могут стоить денег.

1. После push откройте Actions и Pull Request, дождитесь реальных статусов; красные job нужно исправить, а не скрывать.
2. Скачайте artifacts с отчётами, добавьте ссылки и скриншоты в `docs/QUALITY_REPORT_TEMPLATE.md`.
3. В финальном отчёте укажите список дефектов с приоритетом, статусом, найденным инструментом и исправлением. Приложите ссылку на GitHub.

**Скриншоты:** `15_actions.png`, `15_junit_artifact.png`, `15_coverage_artifact.png`.

---

## Требования к защите

К каждому из 15 пунктов приложите: цель, что сделали, фрагмент исходников, какой командой/действием проверяли, **реальный результат** и подтверждение. Итог должен включать введение, сведения о TaskBar, схему архитектуры, 15 заданий с доказательствами, 10 ручных тест-кейсов, список багов, показатели JaCoCo / CPU / Memory / старт / FPS, CI, вывод и 7 ответов на контрольные вопросы.

**Недопустимо:** писать «все тесты зелёные», «покрытие 92%», «Firebase на 2 устройствах успешно пройден» или вставлять картинки-пустышки, пока этого не подтверждают запуски.
