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
# Kit de développement logiciel API Outlook pour Android

**Important :** Ce kit de développement logiciel est devenu obsolète et ne fait plus l’objet de mise à jour. Nous vous recommandons d’utiliser [Microsoft Graph](https://graph.microsoft.com/) ainsi que les [kits de développement de Microsoft Graph associés](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks) à la place.

Créez des applications pour les utilisateurs d’Office 365, Outlook.com et Outlook avec un ensemble d’API.

---

: point d’exclamation :**NOTE** : Vous pouvez utiliser ce code et cette bibliothèque conformément aux termes de la [LICENCE](/LICENSE) de inclus et pour ouvrir les problèmes dans le cadre de ce repo.

Des informations sur le support Microsoft officiel sont disponibles [ici][support-placeholder].

[support-placeholder]: https://support.microsoft.com/

---

Ces bibliothèques sont générées à partir des métadonnées de l’API à l’aide de [Vipr] et [Vipr-T4TemplateWriter] et utilisent une pile client partagée fournie par [ORC-for-Android].

Pour plus d’informations sur la cadence des publications et sur l’accès aux fichiers binaires générés avant la publication, consultez [Publier](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases).

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## Démarrage rapide

Pour utiliser cette bibliothèque dans votre projet, suivez ces étapes générales, comme décrit ci-dessous :

1. Configuration des dépendances dans build.gradle.
2. Mettre en place une authentification.
3. Construire un client API.
4. Appelez des méthodes pour passer des appels REST et recevoir des résultats.

### Configuration

1. Sur l’écran d’accueil d’Android Studio, cliquez sur « Démarrer un nouveau projet Android Studio ». Donnez un nom à votre application comme vous le souhaitez.

2. Sélectionnez « Téléphone et tablette », puis choisissez l’API 18, puis cliquez sur Suivant. Sélectionnez « Activité vide », puis cliquez sur Suivant. Les valeurs par défaut conviennent pour le nom de l’activité. cliquez sur Terminer.

3. Ouvrez l’affichage projet dans la colonne de gauche, s’il n’est pas ouvert. Dans la liste des scripts Gradle, recherchez le titre « build.gradle (Module : application) », puis double-cliquez dessus pour l’ouvrir.

4. Dans la fermeture des `dépendances`, ajoutez les dépendances suivantes à la configuration de `compilation` :
si vous utilisez le portail d'inscription actuel (Azure) :

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   ou si vous utilisez le nouveau Portail d’inscription de l’application : 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```
	vous pouvez cliquer sur le bouton « Synchroniser le projet avec des fichiers Gradle » dans la barre d’outils. Les dépendances sont téléchargées, de telle sorte que Android Studio puisse aider à les coder.

5. Recherchez AndroidManifest.xml et ajoutez la ligne suivante dans la section manifeste :

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### Authentifier et construire le client
Une fois votre projet préparé, l’étape suivante consiste à initialiser le gestionnaire de dépendances et un client API.

: point d’exclamation : Si vous n’avez pas encore inscrit votre application dans Azure AD, vous devez le faire avant d’effectuer cette étape en suivant [ces instructions][MSDN Add Common Consent].

: point d’exclamation : Si vous n’avez pas encore inscrit le Portail d’inscription de l’application, vous devez le faire avant d’effectuer cette étape en suivant [ces instructions][App Registration].

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. Dans la vue Projet dans Android Studio, recherchez app/src/main/res/values, cliquez avec le bouton droit sur celui-ci, puis choisissez *Nouveau* > *Fichier de ressources des valeurs*. Donner un nom à votre fichier adal_settings.

2. Renseignez le fichier avec les valeurs de l’inscription de l’application, comme dans l’exemple suivant. **Veillez à coller les valeurs d’inscription de l’application pour l’ID client et la redirection de l’URL.**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. Ajoutez un ID au TextView « Hello World ». Ouvrez app/src/main/res/layout/activity_main.xml. Utilisez la balise suivante.

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

4. Configurez le DependencyResolver

    Ouvrez la classe MainActivity et ajoutez les importations suivantes :

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

    Ajoutez ensuite ces champs d’instance à la classe MainActivity :

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    Ajoutez la méthode suivante à la classe MainActivity. La méthode logon() construit et initialise le AuthenticationContext d'ADAL, effectue une connexion interactive et construit le DependencyResolver à l'aide du AuthenticationContext prêt à l'emploi.

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

    Vous devez également configurer MainActivity pour renvoyer le résultat de l’authentification vers la AuthenticationContext en ajoutant cette méthode à sa classe :

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    À partir de MainActivity.onCreate, mettez en cache les messages TextView, puis appelez logon () et reconnectez-vous à son achèvement à l’aide du code suivant :

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
	
4. À présent, ajoutez le code nécessaire pour créer un client API.

    Ajoutez une variable statique privée avec l'URL de base d'Outlook :

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```

    Ajoutez une variable d’instance privée pour le client :

    ```java
    private OutlookClient mClient;
    ```

    Enfin, terminez la méthode onSuccess en construisant un client et en l’utilisant. Nous allons définir la méthode getMessages () à l’étape suivante.

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. Créez une méthode pour utiliser le client afin de recevoir tous les messages de votre boîte de réception.

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

En cas d’aboutissement, le nombre de messages récupérés de votre boîte de réception s’affiche dans le TextView. :)

## FAQ


## Contribution
Vous devrez signer un [Contrat de licence de contributeur](https://cla.microsoft.com/) avant d’envoyer votre requête de tirage. Pour compléter le contrat de licence de contributeur (CLA), vous devez envoyer une requête en remplissant le formulaire, puis signer électroniquement le contrat de licence de contributeur quand vous recevrez le courrier électronique contenant le lien vers le document. Cette opération ne doit être effectuée qu’une seule fois pour les projets OSS Microsoft Open Technologies.

Ce projet a adopté le [code de conduite Open Source de Microsoft](https://opensource.microsoft.com/codeofconduct/). Pour en savoir plus, reportez-vous à la [FAQ relative au code de conduite](https://opensource.microsoft.com/codeofconduct/faq/) ou contactez [opencode@microsoft.com](mailto:opencode@microsoft.com) pour toute question ou tout commentaire.

## Licence
Copyright (c) Microsoft Corporation. Tous droits réservés. Soumis à la licence MIT.
