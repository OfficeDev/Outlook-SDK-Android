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
# SDK da API do Outlook para Android

**Importante:** Este SDK de visualização foi substituído e não está mais sendo mantido. Recomendamos que você use o Microsoft Graph e os [SDKs associados do Microsoft Graph](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks).

Crie aplicativos para usuários das plataformas Office 365, Outlook.com e Outlook com um conjunto de APIs.

---

:exclamação:**Observação**: Você tem liberdade para usar essa biblioteca e esse código de acordo com os termos da [LICENÇA](/LICENSE) incluída e para abrir questões neste repositório para obter suporte não oficial.

As informações sobre o suporte oficial da Microsoft estão disponíveis [aqui][support-placeholder].

[support-placeholder]: https://support.microsoft.com/

---

Essas bibliotecas são geradas a partir dos metadados da API usando [Vipr] e [Vipr-T4TemplateWriter] e usa uma pilha de cliente compartilhada fornecida por [orc-para-android].

Para saber mais sobre a versão cadência e como acessar os binários criados antes do lançamento, confira [Versões](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases).

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## Início Rápido

Para usar essa biblioteca em seu projeto, siga estas etapas gerais, conforme descrito abaixo:

1. Configure dependências no build.gradle.
2. Configure a autenticação.
3. Construir um cliente da API.
4. Chame os métodos para fazer chamadas REST e receber resultados.

### Configuração

1. Na tela inicial do Android Studio, clique em "Iniciar um novo projeto do Android Studio". Nomeie o aplicativo como desejar.

2. Selecione "Telefone e Tablet", defina o SDK mínimo como API 18 e clique em Avançar. Escolha "Atividade em branco", em seguida, clique em Avançar. Os padrões são ideais para o nome da atividade, portanto clique em Concluir.

3. Abra o modo de exibição do Projeto na coluna à esquerda se ele não estiver aberto. Na lista de scripts gradle, localize o título "build.gradle (Module: app)" e clique duas vezes para abri-lo.

4. No fechamento `dependências`, adicione as seguintes dependências à configuração `compilar`
 se usar o portal de registro atual (Azure):

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   ou, se estiver usando o novo Portal de registro de aplicativo: 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```
	Talvez seja necessário clicar no botão "Sincronizar o Projeto com os arquivos with gradle" na barra de ferramentas. Isso fará com que as dependências sejam baixadas para que o Android Studio possa auxiliá-lo na codificação com elas.

5. Encontre AndroidManifest.xml e adicione a seguinte linha na seção manifestar:

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### Autenticar e construir cliente
Com o projeto preparado, a próxima etapa é inicializar o gerenciador de dependências e um cliente API.

:exclamação: Se você ainda não registrou seu aplicativo no Azure AD, é necessário fazer isso antes de concluir esta etapa, seguindo [estas instruções][MSDN Add Common Consent].

:exclamação: Se você ainda não registrou seu aplicativo no Portal de registro de aplicativos, é necessário fazer isso antes de concluir esta etapa, seguindo [estas instruções][App Registration].

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. No modo de exibição do Projeto no Android Studio, localize aplicativo/src/principal/res/valores, clique com o botão direito do mouse e escolha *Novo* > *Arquivo de recurso de valores*. Nomeie o arquivo adal_settings.

2. Preencha o arquivo com valores do seu registro de aplicativo, como no exemplo a seguir. **Não deixe de colar os valores de registro do aplicativo para a ID do cliente e a URL de redirecionamento.**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. Adicione uma ID à TextView "Olá mundo". Abra app/src/main/res/layout/activity_main.xml. Use a seguinte marca.

    ```xml
	android:id="@+id/messages"
    ```

4. Configure o DependencyResolver

    Abra a classe MainActivity e adicione as seguintes importações:

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

    Em seguida, adicione esses campos da instância à classe MainActivity:

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    Adicione o método a seguir à classe MainActivity. O método logon () constrói e inicializa o AuthenticationContext do ADAL, executa o logon interativo e constrói o DependencyResolver usando a AuthenticationContext pronta para usar.

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

    Você também deve configurar o MainActivity para passar o resultado da autenticação de volta para o AuthenticationContext adicionando esse método à sua classe:

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    Em MainActivity.onCreate, armazene em cache as mensagens TextView, em seguida, chame logon () e conecte-se a sua conclusão usando o seguinte código:

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

4. Agora, adicione o código necessário para criar um cliente da API.

    Adicione uma variável estática particular com a URL base do Outlook:

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```

    Adicione uma variável de instância privada para o cliente:

    ```java
    private OutlookClient mClient;
    ```

    E, por fim, conclua o método onSucess construindo um cliente e usando-o. Vamos definir o método getMessages() na próxima etapa.

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. Crie um novo método para usar o cliente e obter todas as mensagens da caixa de entrada.

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
	
Caso tenha êxito, o número de mensagens recuperadas da sua caixa de entrada será exibido na TextView. :)

## Perguntas frequentes


## Colaboração
Assine o [Contrato de Licença de Colaborador](https://cla.microsoft.com/) antes de enviar a solicitação pull. Para concluir o Contributor License Agreement (Contrato de Licença do Colaborador), você deve enviar uma solicitação através do formulário e assinar eletronicamente o CLA quando receber o e-mail com o link para o documento. Isso só precisa ser feito uma vez em qualquer projeto da Microsoft Open Technologies OSS.

Este projeto adotou o [Código de Conduta de Código Aberto da Microsoft](https://opensource.microsoft.com/codeofconduct/).  Para saber mais, confira [Perguntas frequentes sobre o Código de Conduta](https://opensource.microsoft.com/codeofconduct/faq/) ou contate [opencode@microsoft.com](mailto:opencode@microsoft.com) se tiver outras dúvidas ou comentários.

## Licença
Copyright (c) Microsoft Corporation. Todos os direitos reservados. Licenciada sob a Licença do MIT.
