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
# Android 用 Outlook API SDK

**重要:**このプレビュー SDK は廃止されており、保守は終了しています。代わりとして、[Microsoft Graph](https://graph.microsoft.com/) および関連する [Microsoft Graph SDK](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks) を使用することをお勧めします。

API のセットを使用して Outlook、Outlook.com、Office 365 ユーザー用のアプリをビルドします。

---

:exclamation:**注**:このコードとライブラリは、付属する[ライセンス](/LICENSE)の条件に従って自由にお使いいただけます。このレポジトリでの問題を報告すると、非公式のサポートを得られます。

公式の Microsoft サポートの詳細については、[こちら][support-placeholder]を参照してください。

[support-placeholder]: https://support.microsoft.com/

---

これらのライブラリは、Vipr および Vipr-T4TemplateWriter を使用して API メタデータから生成されており、orc-for-android によって提供される共有のクライアント スタックを使用しています。

リリースの間隔およびビルドされたバイナリにリリース前にアクセスする方法については、「[Releases (リリース)](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases)」を参照してください。

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## クイック スタート

このライブラリをプロジェクトで使用するには、次の一般的な手順を後述する詳細に従って実行します。

1. 依存関係を build.gradle で構成する。
2. 認証をセットアップする。
3. API クライアントを構築する。
4. メソッドを呼び出して REST 呼び出しを行い、結果を受信する。

### セットアップ

1. Android Studio のスプラッシュ画面で、[Start a new Android Studio project (新しい Android Studio プロジェクトの開始)] をクリックします。アプリケーションに任意の名前を付けます。

2. [Phone and Tablet (電話とタブレット)] を選択し、[Minimum SDK (最小 SDK)] を [API 18] に設定し、[Next (次へ)] をクリックします。[Blank Activity (空のアクティビティ)] を選択し、[Next (次へ)] をクリックします。アクティビティ名は既定のものを使用できるので、[Finish (完了)] をクリックします。

3. 左側の列にプロジェクト ビューが開いていない場合は、開きます。Gradle Scripts の一覧から、"build.gradle (Module: app)" という名前の項目を見つけ、ダブルクリックして開きます。

4. `dependencies` クロージャで、次の依存関係を `compile` 構成に追加します。現在の登録ポータル (Azure) を使用している場合は次のようにします。

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   または、新しいアプリケーション登録ポータルを使用している場合は次のようにします。 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```

	ツールバーにある [Sync Project with Gradle Files (プロジェクトを Gradle Files と同期する)] ボタンをクリックすることをお勧めします。これを行うと依存関係がダウンロードされるため、Android Studio が依存関係のコーディングを支援できます。

5. [AndroidManifest.xml] を見つけ、マニフェスト セクションに次の行を追加します。

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### クライアントを認証して構築する
プロジェクトが準備できたら、次の手順として、依存関係マネージャーと API クライアントを初期化します。

:exclamation:アプリを Azure AD でまだ登録していない場合、この手順を完了するには、[こちらの手順][MSDN Add Common Consent]に従って登録を行う必要があります。

:exclamation:アプリケーション登録ポータルへの登録がまだの場合は、この手順を完了するには、[こちらの手順][App Registration]に従って登録を行う必要があります。

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. Android Studio のプロジェクト ビューで、app/src/main/res/values を見つけて右クリックし、[*New (新規)*]、[*Values resource file (値リソース ファイル)*] の順に選択します。ファイルに "adal_settings" という名前を付けます。

2. 次の例に示すように、アプリ登録からの値をファイルに入力します。**必ず、クライアント ID とリダイレクト URL のアプリ登録値を貼り付けてください。**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. "Hello World" TextView に ID を追加します。app/src/main/res/layout/activity_main.xml を開きます。次のタグを使用します。

    ```xml
	android:id="@+id/messages"
    ```

4. DependencyResolver をセットアップする

    MainActivity クラスを開き、次のインポートを追加します。

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

    次に、これらのインスタンス フィールドを MainActivity クラスに追加します。

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    次のメソッドを MainActivity クラスに追加します。logon() メソッドは ADAL の AuthenticationContext を構築して初期化し、対話型ログオンを実行し、あらかじめ用意されている AuthenticationContext を使用して DependencyResolver を構築します。

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

    このメソッドを MainActivity クラスに追加することにより、認証結果を AuthenticationContext に渡すように MainActivity を構成する必要もあります。

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    MainActivity.onCreate からメッセージ TextView をキャッシュし、次のコードを使用して logon() を呼び出してその完了に接続します。

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

4. ここでは、必要なコードを追加して API クライアントを作成します。

    Outlook のベース URL を使用してプライベート静的変数を追加します。

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```

    クライアントのプライベート インスタンス変数を追加します。

    ```java
    private OutlookClient mClient;
    ```

    最後に、クライアントを構築してそれを使用することにより、onSuccess メソッドを完了します。getMessages() メソッドは次の手順で定義します。

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. クライアントを使用して受信トレイからすべてのメッセージを取得する新しいメソッドを作成します。

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

成功した場合、受信トレイから取得したメッセージの数が TextView に表示されます。

## FAQ


## 投稿
プル要求を送信する前に、[投稿者のライセンス契約](https://cla.microsoft.com/)に署名する必要があります。投稿者のライセンス契約 (CLA) を完了するには、ドキュメントへのリンクを含むメールを受信した際に、フォームから要求を送信し、CLA に電子的に署名する必要があります。これを行う必要があるのは、Microsoft Open Technologies のすべての OSS プロジェクトに対して 1 回のみです。

このプロジェクトでは、[Microsoft Open Source Code of Conduct (Microsoft オープン ソース倫理規定)](https://opensource.microsoft.com/codeofconduct/) が採用されています。詳細については、「[倫理規定の FAQ](https://opensource.microsoft.com/codeofconduct/faq/)」を参照してください。また、その他の質問やコメントがあれば、[opencode@microsoft.com](mailto:opencode@microsoft.com) までお問い合わせください。

## ライセンス
Copyright (c) Microsoft Corporation。All rights reserved.Licensed under the MIT License.
