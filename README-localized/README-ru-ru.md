---
page_type: sample
products:
- office-365
- office-outlook
languages:
- java
extensions:
  contentType: samples
  platforms:
  - Android
  createdDate: 11/16/2015 1:47:50 PM
---
# API SDK Outlook для Android

**Важно!** Эта предварительная версия SDK устарела и больше не поддерживается. Рекомендуем использовать [Microsoft Graph](https://graph.microsoft.com/) и связанные с ним пакеты [Microsoft Graph SDK](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks).

Создавайте приложения для пользователей Outlook, Outlook.com и Office 365, используя один набор API.

---

:exclamation:**ПРИМЕЧАНИЕ**. Вы можете использовать этот код и библиотеку в соответствии с условиями включенной [ЛИЦЕНЗИИ](/LICENSE) и можете создавать проблемы в этом репозитории для неофициальной поддержки.

Сведения об официальной поддержке Майкрософт доступны [здесь][support-placeholder].

[support-placeholder]: https://support.microsoft.com/

---

Эти библиотеки формируются из метаданных API с помощью [Vipr] и [Vipr-T4TemplateWriter], они используют общий стек клиента, который предоставляется пакетом [orc-for-android].

Сведения о последовательности выпусков и о доступе к собранным двоичным файлам до выпуска см. в разделе [Выпуски](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases).

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## Быстрое начало работы

Чтобы использовать эту библиотеку в проекте, выполните общие действия, как описано ниже:

1. Настройте зависимости в build.gradle.
2. Настройте проверку подлинности.
3. Создайте клиент API.
4. Вызовите методы для осуществления вызовов REST и получения результатов.

### Настройка

1. На начальном экране Android Studio щелкните "Start a new Android Studio project". Укажите приложению желаемое имя.

2. Выберите "Phone and Tablet", в раскрывающемся списке "Minimum SDK" выберите значение "API 18", затем нажмите кнопку "Next". Выберите "Blank Activity", затем нажмите кнопку "Next". Для имени действия можно использовать значения по умолчанию, поэтому нажмите кнопку "Finish".

3. Откройте представление "Project" в столбце слева, если оно не открыто. В списке сценариев Gradle найдите "build.gradle (Module: app)" и дважды щелкните этот сценарий, чтобы открыть его.

4. В разделе `dependencies` добавьте следующие зависимости к конфигурации `compile`:
при использовании текущего портала регистрации (Azure):

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   при использовании нового портала регистрации приложений: 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```
	Рекомендуется нажать кнопку "Sync Project with Gradle Files" на панели инструментов. При этом будут скачаны все зависимости, чтобы можно было использовать Android Studio для создания кода с их помощью.

5. Найдите файл AndroidManifest.xml и добавьте следующую строку в раздел "manifest":

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### Проверка подлинности и создание клиента
После подготовки проекта, на следующем этапе нужно инициализировать диспетчер зависимостей и клиент API.

:восклицание: Если вы еще не зарегистрировали приложение в Azure AD, это потребуется сделать до завершения этого шага, выполнив [следующие инструкции][MSDN Add Common Consent].

:exclamation: Если вы еще не зарегистрировали портал регистрации приложений, это потребуется сделать до завершения этого шага, выполнив [следующие инструкции][App Registration].

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. В представлении Project в Android Studio найдите строку app/src/main/res/values,щелкните ее правой кнопкой мыши, и выберите *New* > *Values resource file*. Назовите файл adal_settings.

2. Заполните в этом файле значения из регистрации вашего приложения, как в следующем примере. **Не забудьте вставить значения регистрации приложения для параметров "Client ID" и "Redirect URL".**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. Добавьте идентификатор к представлению TextView "Hello World" . Откройте файл app/src/main/res/layout/activity_main.xml. Используйте следующий тег.

    ```xml
	android:id="@+id/messages"
    ```

4. Настройте DependencyResolver

    Откройте класс MainActivity и добавьте следующие операции импорта:

    ```java
    import com.google.common.util.concurrent.FutureCallback;
    import com.google.common.util.concurrent.Futures;
    import com.google.common.util.concurrent.SettableFuture;
    import com.microsoft.aad.adal.AuthenticationCallback;
    import com.microsoft.aad.adal.AuthenticationContext;
    import com.microsoft.aad.adal.AuthenticationResult;
    import com.microsoft.aad.adal.PromptBehavior;
    import com.microsoft.services.outlook.*;
    import com.microsoft.services.outlook.fetchers.OutlookClient;    
    import static com.microsoft.aad.adal.AuthenticationResult.AuthenticationStatus;

    ```

    Затем добавьте эти поля экземпляров в класс MainActivity:

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    Добавьте следующие метод в класс MainActivity. Метод logon() создает и инициализирует контекст AuthenticationContext ADAL, производит интерактивный вход и создает DependencyResolver с помощью готового к использованию контекста AuthenticationContext.

    ```java
    protected SettableFuture<Boolean> logon() {
        final SettableFuture<Boolean> result = SettableFuture.create();

        try {
            mAuthContext = new AuthenticationContext(this, getString(R.string.AADAuthority), true);
            mAuthContext.acquireToken(
                    this,
                    scopes,
                    null,
                    getString(R.string.AADClientId),
                    getString(R.string.AADRedirectUrl),
                    PromptBehavior.Auto,
                    new AuthenticationCallback<AuthenticationResult>() {

                        @Override
                        public void onSuccess(final AuthenticationResult authenticationResult) {
                            if (authenticationResult != null
                                    && authenticationResult.getStatus() == AuthenticationStatus.Succeeded) {
                                mResolver = new DependencyResolver.Builder(
                                                new OkHttpTransport(), new GsonSerializer(),
                                                new AuthenticationCredentials() {
                                                @Override
                                                public Credentials getCredentials() {
                                                    return new OAuthCredentials(token);
                                                }
                                            }).build();
                                result.set(true);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            result.setException(e);
                        }

                    });
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            result.setException(e);
        }
        return result;
    }
    ```

    Также необходимо настроить MainActivity для передачи результата проверки подлинности обратно в контекст AuthenticationContext, добавив этот метод в его класс:

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    В MainActivity.onCreate нужно кэшировать представление TextView сообщений, затем вызвать call logon() подключиться к его завершению, используя следующий код:

    ```java
       messagesTextView = (TextView) findViewById(R.id.messages);
       Futures.addCallback(logon(), new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("logon", t.getMessage());
            }
        });
    ```

4. Теперь добавьте необходимый код для создания клиента API.

    Добавьте частную статическую переменную с базовым URL-адресом Outlook:

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```
	
    Добавьте частную переменную экземпляра для клиента:

    ```java
    private OutlookClient mClient;
    ```

    Затем завершите метод onSuccess, создав и использовав клиент. Мы определим метод getMessages() на следующем шаге.

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. Создайте новый метод, чтобы использовать клиент для получения всех сообщений из папки "Входящие".

	```java
    protected void getMessages() {
        Futures.addCallback(
                mClient.getMe()
                .getMessages()
                .top(20)
                .read(),
                new FutureCallback<List<Message>>() {
                    @Override
                    public void onSuccess(final List<Message> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messagesTextView.setText("Messages: " + result.size());
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Throwable t) {
                        Log.e("getMessages", t.getMessage());
                    }
                });
    }
	```

В случае успеха количество сообщений, полученных из папки "Входящие», будет отображено в представлении TextView. :)

## Вопросы и ответы


## Участие
Прежде чем отправить запрос на включение внесенных изменений, необходимо подписать [Лицензионное Соглашение с Участником](https://cla.microsoft.com/). Чтобы заполнить лицензионное соглашение участника (CLA), вам нужно будет отправить запрос через форму, а затем подписать лицензионное соглашение участника в электронном виде, когда вы получите электронное письмо со ссылкой на документ. Это требуется сделать только один раз для любого проекта Microsoft Open Technologies OSS.

В этом проекте применяются [правила поведения при использовании открытого кода Майкрософт](https://opensource.microsoft.com/codeofconduct/). Дополнительные сведения см. на [странице вопросов и ответов по правилам поведения](https://opensource.microsoft.com/codeofconduct/faq/). Любые дополнительные вопросы и комментарии отправляйте по адресу [opencode@microsoft.com](mailto:opencode@microsoft.com).

## Лицензия
© Корпорация Майкрософт. Все права защищены. Предоставляется по лицензии MIT.
