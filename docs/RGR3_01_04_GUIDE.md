# TaskBar — РГР №3, задания 1–4

**Среда:** Android Studio, сборка `debug`, эмулятор Pixel 9 (или другое устройство API 26+).  
**Статус:** инструменты и воспроизводимые сценарии встроены в исходники; реальные проверки и скриншоты необходимо выполнить на эмуляторе в Android Studio.

## Запуск лаборатории

1. В Android Studio выберите `app` и вариант сборки `debug`.
2. Запустите TaskBar; откройте **Ещё → Лаборатория отладки**.
3. В релизной сборке этот пункт не показывается, `DebugLabActivity` и её сценарии не включаются в APK.
4. Для фиксации результатов создайте каталог `evidence/rgr3/` локально. Скриншоты **не подменять** изображениями из README и не утверждать, что проверки прошли, пока их не выполнили.

## Задание 1. Logcat и логирование

- Основной файл: `app/src/main/java/kz/taskbar/app/diagnostics/TaskBarLog.kt`.
- Теги: `TB_APP`, `TB_TASK`, `TB_DB`, `TB_NETWORK`, `TB_LAB`.
- Нажмите в лаборатории кнопки `DEBUG`, `INFO`, `WARN`, `ERROR`.
- Откройте **View → Tool Windows → Logcat**, выберите процесс `kz.taskbar.app`.
- В строке поиска задайте `tag:TB_LAB`; затем фильтруйте по уровню (`level:DEBUG`, `level:INFO`, `level:WARN`, `level:ERROR`) по очереди. При необходимости используйте фильтры UI Logcat.
- Создайте задачу, измените её статус и удалите; посмотрите события с тегом `TB_TASK`.
- Проверьте код `TaskBarLog`: перед каждым вызовом `Log.*` проверяется `BuildConfig.DEBUG`; логирование HTTP (`HttpLoggingInterceptor`) выключается в release.
- Снимки: `01_logcat_levels.png`, `01_logcat_tag_and_package.png`, `01_release_logging_code.png`.

**Безопасность:** в логах сохраняются только тип операции и ID; название, описание задач, секреты и HTTP-тело не выводятся.

## Задание 2. Debugger и логическая ошибка

Используются два метода:
- `app/src/debug/java/kz/taskbar/app/debug/DebugLabActivity.kt`: `calculateBuggyPercent(2, 6)` — специально неправильный результат **0%** из-за целочисленного деления;
- `app/src/main/java/kz/taskbar/app/domain/ProgressCalculator.kt`: правильный результат **33%**.

1. Откройте `DebugLabActivity.kt`; поставьте обычный **breakpoint** на строку `val result = (completed / safeTotal) * 100`.
2. В лаборатории нажмите **Запустить расчёт** под DEBUGGER; откройте приложение через **Debug 'app'**, не просто Run.
3. Выполните **Step Over**, **Step Into**, **Step Out**, добавьте Watches: `completed`, `safeTotal`, `result`.
4. В **Evaluate Expression** проверьте `completed / safeTotal` и `completed.toDouble() / safeTotal`.
5. Сделайте условный breakpoint с выражением `total == 6`; повторите запуск.
6. Добавьте logpoint на строку с `return result`: `"buggy percent=" + result`; убедитесь, что остановка не обязательна.
7. Сопоставьте с исправленной функцией, где сначала вычисляется `Double`, значение ограничивается диапазоном 0–100 и округляется.

**Важно:** ошибка оставлена только в *debug-only* учебной функции для демонстрации; бизнес-логика исправлена и имеет unit-тесты.  
Снимки: `02_breakpoint_watches.png`, `02_evaluate_expression.png`, `02_conditional_logpoint.png`, `02_broken_and_fixed.png`.

## Задание 3. Разбор крэшей

В лаборатории раздел **EXCEPTIONS** намеренно воспроизводит:

| Действие | Ошибка | Причина | Исправление |
|---|---|---|---|
| NullPointerException | `NullPointerException` | Оператор `!!` над null | Проверка `?.let { }`, `?:` или валидатор |
| IndexOutOfBoundsException | `IndexOutOfBoundsException` | Индекс 5 у списка из 2 элементов | `getOrNull(index)` / `indices` |
| NetworkOnMainThreadException | `NetworkOnMainThreadException` | Сеть синхронно выполняется в UI-потоке | Retrofit suspend-API, `Dispatchers.IO` |

1. В Android Studio включите **Break on exceptions** (включая **Caught exceptions**, потому что лаборатория намеренно ловит их, чтобы приложение не падало).
2. Нажмите каждую кнопку. Проверьте класс ошибки, причину и строку кода по stack trace в Logcat (тег `TB_LAB`).
3. Нажмите **Правильный сетевой запрос (корутина)**; приложение продолжает работать, при отсутствии сети используется резервная цитата.
4. Для NetworkOnMainThreadException требуется активная сетевая попытка; при заблокированной сети возможна другая ошибка. Обязательно зафиксируйте фактический класс исключения, а не ожидаемый.
5. Предлагаемые исправления применены к реальному приложению; намеренно неправильные методы находятся только в `src/debug`.

Снимки: `03_npe_trace.png`, `03_index_trace.png`, `03_network_trace.png`, `03_fixed_network.png`.

## Задание 4. Inspectors

**Layout Inspector**
1. В лаборатории переключатель «Дефект вёрстки» оставьте включённым.
2. Выберите **Tools → Layout Inspector**, выберите процесс TaskBar и найдите элемент с тегом `layout_defect`. Два текста намеренно наложены в `Box`.
3. Выключите переключатель; теперь использован `Row` и `layout_fixed`, перекрытия нет.
4. Снимки: `04_layout_before.png`, `04_layout_after.png`.

**Database Inspector**
1. В TaskBar создайте 1–2 задачи, чтобы появились заметные записи в БД.
2. Выберите **View → Tool Windows → App Inspection → Database Inspector** (название пункта может отличаться по версии Android Studio).
3. Выберите процесс `kz.taskbar.app`, базу `taskbar.db`, таблицу `tasks`.
4. Выполните запрос `SELECT id, title, completed, priority FROM tasks ORDER BY id DESC LIMIT 10;`.
5. В лаборатории нажмите **Проверить таблицу Room** — появится количество строк; сравните с SQL.
6. Снимки: `04_database_table.png`, `04_database_query.png`.

**Network Inspector**
1. Откройте **App Inspection → Network Inspector** и процесс приложения.
2. Нажмите **Выполнить HTTP-запрос**; проверьте запрос `GET https://dummyjson.com/quotes/random` и детали HTTP-запроса/ответа. Соединение должно быть доступно; если нет, лаборатория честно покажет офлайн-резерв.
3. Снимки: `04_network_timeline.png`, `04_network_detail.png`.

## Команды и проверки

Встроенные unit-тесты бизнес-логики (включая новые тесты `ProgressCalculatorTest`):

```bash
./gradlew :app:testDebugUnitTest
```

На Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Локальная сборка:

```powershell
.\gradlew.bat :app:assembleDebug
```

**Требования к отчёту:** краткая цель, описание TaskBar, ход выполнения каждого пункта, реальные скриншоты, код до/после, фактически обнаруженные ошибки и их исправления, вывод. Разделы 5–15 будут реализованы следующим этапом; пока не отмечать их как выполненные.

## Статус проверки

- [x] Реализованы точки входа, логирование, debug-only стенд и демонстрации ошибок.
- [x] Добавлены unit-тесты математического расчёта.
- [ ] Сборка нового коммита в Android Studio (в текущем окружении Android SDK недоступен).
- [ ] Выполнение тестов на эмуляторе.
- [ ] Фактические скриншоты Logcat, Debugger, Inspectors.
- [ ] Отчёт о реальных результатах и найденных дефектах.
