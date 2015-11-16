#Outlook API SDK for Android

Build apps for Outlook, Outlook.com, and Office 365 users with one set of APIs.

---

:exclamation:**NOTE**: You are free to use this code and library according to the terms of its included [LICENSE](/LICENSE) and to open issues in this repo for unofficial support.

Information about official Microsoft support is available [here][support-placeholder].

[support-placeholder]: https://support.microsoft.com/

---

These libraries are generated from API metadata using [Vipr] and [Vipr-T4TemplateWriter] and use a shared client stack provided by [orc-for-android].

For information on release cadence and how to access built binaries before release, see [Releases](https://github.com/OfficeDev/Microsoft-Graph-SDK-Android/wiki/Releases).

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## Quick Start

To use these libraries in your project, follow these general steps, as described further below:

1. Configure dependencies in build.gradle.
2. Set up authentication.
3. Construct an API client.
4. Call methods to make REST calls and receive results.

### Setup

1. From the Android Studio splash screen, click "Start a new Android Studio project". Name your application as you wish.

2. Select "Phone and Tablet" and set Minimum SDK as API 18, then click Next. Choose "Blank Activity", then click Next. The defaults are fine for the activity name, so click Finish.

3. Open the Project view in the left-hand column if it's not open. From the list of Gradle Scripts, find the one title "build.gradle (Module: app)" and double-click to open it.

4. In the `dependencies` closure, add the following dependencies to the `compile` configuration:

    ```groovy
    compile 'com.microsoft.services:outlook-services:2.0.0'
    ```

    You may want to click the "Sync Project with Gradle Files" button in the toolbar. This will download the dependencies so Android Studio can assist in coding with them.

5. Find AndroidManifest.xml and add the following line within the manifest section:

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### Authenticate and construct client
With your project prepared, the next step is to initialize the dependency manager and an API client.

:exclamation: If you haven't yet registered your app in Azure AD, you'll need to do so before completing this step by following [these instructions][MSDN Add Common Consent].

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually

1. From the Project view in Android Studio, find app/src/main/res/values, right-click it, and choose *New* > *Values resource file*. Name your file adal_settings.

2. Fill in the file with values from your app registration, as in the following example. **Be sure to paste in your app registration values for the Client ID and Redirect URL.**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://graph.microsoft.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. Add an id to the "Hello World" TextView. Open app/src/main/res/layout/activity_main.xml.
4. Add the following id tag to the TextView element for "Hello World".

    ```xml
	android:id="@+id/messages"
    ```

4. Set up the DependencyResolver

    Open the MainActivity class and add the following imports:

    ```java
    import com.google.common.util.concurrent.FutureCallback;
    import com.google.common.util.concurrent.Futures;
    import com.google.common.util.concurrent.SettableFuture;
    import com.microsoft.aad.adal.AuthenticationCallback;
    import com.microsoft.aad.adal.AuthenticationContext;
    import com.microsoft.aad.adal.AuthenticationResult;
    import com.microsoft.aad.adal.PromptBehavior;
    import com.microsoft.services.graph.*;
    import com.microsoft.services.graph.fetchers.GraphServiceClient;    
    import static com.microsoft.aad.adal.AuthenticationResult.AuthenticationStatus;

    ```

    Then, add these instance fields to the MainActivity class:

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    ```

    Add the following method to the MainActivity class. The logon() method constructs and initializes ADAL's AuthenticationContext, carries out interactive logon, and constructs the DependencyResolver using the ready-to-use AuthenticationContext.

    ```java
    protected SettableFuture<Boolean> logon() {
        final SettableFuture<Boolean> result = SettableFuture.create();

        try {
            mAuthContext = new AuthenticationContext(this, getString(R.string.AADAuthority), true);
            mAuthContext.acquireToken(
                    this,
                    getString(R.string.AADResourceId),
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

    You also must configure MainActivity to pass the result of authentication back to the AuthenticationContext by adding this method to its class:

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    From MainActivity.onCreate, cache the messages TextView, then call logon() and hook up to its completion using the following code:

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

4. Now fill in the onSuccess method of the FutureCallback to create an API client.

    Add a private static variable with the Outlook base URL:

    ```java
    private static final String graphBaseUrl = "https://graph.microsoft.com/api/v1.0";
    ```

    Add a private instance variable for the client:

    ```java
    private GraphServiceClient mClient;
    ```

    And finally complete the onSuccess method by constructing a client and using it. We'll define the getMessages() method in the next step.

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new GraphServiceClient(graphBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. Create a new method to use the client to get all messages from your inbox.

	```java
    protected void getMessages() {
        Futures.addCallback(
                mClient.getMe()
                        .getMailFolders()
                        .getById("Inbox")
                        .getMessages()
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

If successful, the number of messages in your inbox will be displayed in the TextView. :)

## FAQ


## Contributing
You will need to sign a [Contributor License Agreement](https://cla.microsoft.com/) before submitting your pull request. To complete the Contributor License Agreement (CLA), you will need to submit a request via the form and then electronically sign the Contributor License Agreement when you receive the email containing the link to the document. This needs to only be done once for any Microsoft Open Technologies OSS project.

## License
Copyright (c) Microsoft Corporation. All rights reserved. Licensed under the MIT License.
