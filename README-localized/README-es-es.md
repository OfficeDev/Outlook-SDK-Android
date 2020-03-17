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
# SDK de la API de Outlook para Android

**Importante:** Este SDK ha quedado en desuso y ya no se mantiene. Le recomendamos que use [Microsoft Graph](https://graph.microsoft.com/) y los [SDK de Microsoft Graph](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks) asociados en su lugar.

Cree aplicaciones para los usuarios de Office 365, Outlook.com y Outlook con un conjunto de API.

---

: exclamación:**NOTA**: Puede usar este código según los términos de su [LICENCIA](/LICENSE) incluida y abrir incidencias en este repositorio para obtener soporte técnico no oficial.

La información sobre el soporte técnico oficial de Microsoft está disponible [aquí][support-placeholder].

[support-placeholder]: https://support.microsoft.com/

---

Estas bibliotecas se han generado a partir de los metadatos de la API usando [Vipr] y [Vipr-T4TemplateWriter] y utilizan una pila de clientes compartida proporcionada por [orc-for-android].

Para obtener información sobre la cadencia de publicación de versiones y sobre cómo obtener acceso a los archivos binarios integrados antes de la publicación, vea [Versiones](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases).

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## Inicio rápido

Para utilizar esta biblioteca en su proyecto, siga los pasos generales que se describen más adelante:

1. Configure dependencias en build.gradle.
2. Configure la autenticación.
3. Cree un cliente API.
4. Llame a métodos para hacer llamadas de REST y recibir resultados.

### Instalación

1. En la pantalla de bienvenida de Android Studio, haga clic en "Iniciar un nuevo proyecto de Android Studio". Asigne a la aplicación el nombre que quiera.

2. Seleccione "Teléfono y tableta", establezca SDK mínimo como API 18 y luego haga clic en Siguiente. Elija "Actividad en blanco" y luego haga clic en Siguiente. Los valores predeterminados son adecuados para el nombre de la actividad, por lo puede hacer clic en finalizar.

3. Abra la vista Proyecto en la columna izquierda si no está abierto. En la lista de scripts de Gradle, busque el que se llama "build.gradle (Module: app)" y haga doble clic para abrirlo.

4. En el cierre `dependencies`, agregue las siguientes dependencias a la configuración compile`.
Si usa el portal de registro actual (Azure):

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   Si usa el nuevo Portal de registro de aplicaciones: 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```
	Es recomendable que haga clic en el botón "Sync Project with Gradle Files" de la barra de herramientas. Así se descargarán las dependencias para que Android Studio pueda ayudar con ellas en la codificación.

5. Busque AndroidManifest.xml y añada la siguiente línea a la sección de manifiesto:

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### Autentificar y construir el cliente
Con el proyecto preparado, el siguiente paso es inicializar el administrador de dependencias y un cliente API.

exclamación: Si aún no ha registrado su aplicación en Azure AD, tendrá que hacerlo antes de completar este paso siguiendo [estas instrucciones][MSDN Add Common Consent].

:exclamación: Si aún no se ha registrado en el Portal de registro de aplicaciones, tendrá que hacerlo antes de completar este paso siguiendo [estas instrucciones][App Registration].

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. Desde la vista Proyecto en Android Studio, busque app/src/main/res/values, haga clic en él con el botón secundario y seleccione *Nuevo* > *Archivo de recursos de valores*. Asigne al archivo el nombre adal_settings.

2. Rellene el archivo con los valores del registro de la aplicación, tal como se muestra en el ejemplo siguiente. **Asegúrese de pegar los valores de registro de la aplicación para el Id. de cliente y la URL de redireccionamiento.**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. Agregue un Id. al TextView "Hola mundo". Abra app/src/main/res/layout/activity_main.xml. Use la etiqueta siguiente.

    ```xml
	android:id="@+id/messages"
    ```

4. Configure el DependencyResolver

    Abra la clase MainActivity y agregue las siguientes importaciones:

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

    Después, agregue estos campos de instancia a la clase MainActivity:

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    Agregue el método siguiente a la clase MainActivity. El método logon () crea e inicializa AuthenticationContext de ADAL, lleva a cabo un inicio de sesión interactivo y crea DependencyResolver con AuthenticationContext, que está listo para usar.

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

    También tiene que configurar MainActivity para volver a pasar el resultado de la autenticación a AuthenticationContext agregando este método a su clase:

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    Desde MainActivity.onCreate, almacene en caché el TextView de mensajes y, después, llame al método logon() y conecte su finalización con el siguiente código:

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

4. Ahora, agregue el código necesario para crear un cliente de API.

    Agregue una variable estática privada con la dirección URL base de Outlook:

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```

    Agregue una variable de instancia privada para el cliente:

    ```java
    private OutlookClient mClient;
    ```

    Y, por último, complete el método onSuccess creando un cliente y usándolo. En el siguiente paso, definiremos el método getMessages().

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. Cree un nuevo método para usar el cliente y obtener todos los mensajes de la bandeja de entrada.

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

Si se ejecuta correctamente, el número de mensajes recuperados de la bandeja de entrada se mostrará en el TextView. :)

## Preguntas frecuentes


## Colaboradores
Deberá firmar un [Contrato de licencia de colaborador](https://cla.microsoft.com/) antes de enviar la solicitud de incorporación de cambios. Para completar el Acuerdo de Licencia de Colaborador (CLA), deberá presentar una solicitud a través del formulario y luego firmar electrónicamente el Acuerdo de Licencia de Contribuyente cuando reciba el correo electrónico que contiene el enlace al documento. Esto sólo tiene que hacerse una vez para cualquier proyecto de OSS de Microsoft Open Technologies.

Este proyecto ha adoptado el [Código de conducta de código abierto de Microsoft](https://opensource.microsoft.com/codeofconduct/). Para obtener más información, vea [Preguntas frecuentes sobre el código de conducta](https://opensource.microsoft.com/codeofconduct/faq/) o póngase en contacto con [opencode@microsoft.com](mailto:opencode@microsoft.com) si tiene otras preguntas o comentarios.

## Licencia
Copyright (c) Microsoft Corporation. Todos los derechos reservados. Publicado bajo la licencia MIT.
