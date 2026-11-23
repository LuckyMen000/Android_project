# TaskBar RGR3: стабилизация UI-тестов

Последний известный запуск GitHub Actions #13 (23.11.2026) завершился с 5/7 пройденными Android-тестами. Две ошибки имели одинаковый симптом `RootViewWithoutFocusException`:

- `TaskBarComposeUiTest.createTaskSearchAndNavigate`: вызов `Espresso.closeSoftKeyboard()` из Compose `ModalBottomSheet` работал с потерявшим фокус окном `MainActivity`.
- `TaskBarEspressoIntentsTest.openingDebugLabIssuesExpectedExplicitIntent`: после реального запуска `DebugLabActivity` проверка `Intents.intended` обращалась к нефокусированному окну `MainActivity`.

Изменения:

1. Page Object работает с клавиатурой средствами Compose (`performImeAction`), а перед нажатием «Создать задачу» прокручивает форму (`performScrollTo`).
2. Текстовые поля названия и поиска получили явные действия IME (Done/Search), закрывающие клавиатуру. Это полезно и обычным пользователям.
3. UI-тест по-прежнему проверяет создание, сохранение в Room, поиск и навигацию; после фильтрации он ждёт «Найдено: 1» и прокручивает `LazyColumn` к карточке.
4. Espresso-Intents перехватывает ожидаемый запуск `DebugLabActivity` с помощью `intending(...).respondWith(...)`, сохраняя фокус `MainActivity`. Затем `intended(...)` проверяет, что правильный Intent отправлен.

Не отключены ни проверки, ни тесты, ни порог JaCoCo 70%.

Команды Windows (корень проекта):

```powershell
.\gradlew.bat :app:compileDebugAndroidTestKotlin
.\gradlew.bat :app:testDebugUnitTest :app:rgr3CoverageVerify
.\gradlew.bat :app:connectedDebugAndroidTest
```

**Статус:** исправления внесены на основании реального отчёта GitHub Actions #13. В среде подготовки архива нет Android SDK, поэтому полный повторный запуск не выполнен. Итоговые результаты нужно подтвердить в Android Studio или GitHub Actions после отправки файлов.
