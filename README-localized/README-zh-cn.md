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
# Android 版 Outlook API SDK

**重要说明：**预览版 SDK 已弃用，不再对其进行维护。建议改用 [Microsoft Graph](https://graph.microsoft.com/) 和关联的 [Microsoft Graph Sdk](https://developer.microsoft.com/en-us/graph/code-samples-and-sdks)。

使用一组 API 为 Outlook、Outlook.com 和 Office 365 用户生成应用。

---

：感叹号：**备注**：可根据所包含[许可证](/LICENSE)的条款，免费试用此代码和库，并在非官方支持存储库中打开问题。

[此处][support-placeholder]提供有关 Microsoft 官方支持的信息。

[support-placeholder]: https://support.microsoft.com/

---

这些库是使用 [Vipr] 和 [Vipr-T4TemplateWriter] 根据 API 元数据生成，并使用 [orc-android] 提供的共享客户端堆栈。

有关发布节奏和如何相在发布前访问构建二进制文件的信息，参见“[发布](https://github.com/OfficeDev/Outlook-SDK-Android/wiki/Releases)”。

[Vipr]: https://github.com/microsoft/vipr
[Vipr-T4TemplateWriter]: https://github.com/msopentech/vipr-t4templatewriter
[orc-for-android]: https://github.com/msopentech/orc-for-android

## 快速入门

若要在项目中使用此库，按照如下所述的常规步骤进行操作：

1. 在 build.gradle 中配置依赖项。
2. 设置身份验证。
3. 构建 API 客户端。
4. 调用方法以进行 REST 调用并检索结果。

### 设置

1. 从 Android Studio 初始屏幕中，单击“开始新的 Android Studio 项目 ”。根据自己的期望命名应用程序。

2. 选择“手机和平板电脑”并将最小 SDK 设置为 API 18，然后单击 “下一步”。选择“空白活动”，然后单击“下一步”。默认项适用于活动名称，因此点击“完成”。

3. 如果未打开，打开左列中的项目视图。从 Gradle 脚本列表中，找到标记“"build.gradle (Module: app)”，然后双击打开。

4. 在“`依赖项`”闭包中，添加依赖项至“`编译`”配置：
如果使用当前注册门户（Azure）：

    ```groovy
    compile('com.microsoft.services:outlook-services:2.0.0'){
        transitive = true
    }
    ```

   或如果使用新的应用程序注册门户： 

    ```groovy
    compile('com.microsoft.services:outlook-services:2.1.0'){
        transitive = true
    }
    ```
	可能需要点击工具栏中的“将项目与 Gradle 文件同步”按钮。这将下载依赖项，以便通过它们协助进行编码。

5. 查找 AndroidManifest，并在清单区内添加下列行：

     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```

### 验证身份并构建客户端
随着项目的准备完毕，下一步是初始化依赖关系管理器和 API 客户端。

：感叹号：如果尚未注册应用程序至 Azure AD，需要按照“[这些说明][MSDN Add Common Consent]”在完成此步骤前进行注册。

：感叹号：如果尚未注册应用程序注册门户，需要按照“[这些说明][App Registration]在完成此步骤前进行注册。

[MSDN Add Common Consent]: https://msdn.microsoft.com/en-us/office/office365/howto/add-common-consent-manually
[App Registration]:https://dev.outlook.com/RestGettingStarted

1. 从 Android Studio 中的项目文件，查找 app/src/main/res/values，右击，并选择“*新建*” > “*数值资源文件*”。命名文件 adal_settings。

2. 按照下例所示，使用应用程序注册中的数值填充文件。**务必粘贴至客户端 ID 和重定向 URL 的应用程序注册值中。**

    ```xml
    <string name="AADAuthority">https://login.microsoftonline.com/common</string>
    <string name="AADResourceId">https://outlook.office.com</string>
    <string name="AADClientId">Paste your Client ID HERE</string>
    <string name="AADRedirectUrl">Paste your Redirect URI HERE</string>
    ```

3. 添加 ID 至 "Hello World" TextView。打开 app/src/main/res/layout/activity_main.xml。使用以下标记。

    ```xml
	android:id="@+id/messages"
    ```

4. 设置 DependencyResolver

    打开 MainActivity 类并添加以下导入：

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

    然后，将这些实例字段添加到 MainActivity 类：

    ```java
    private AuthenticationContext mAuthContext;
    private DependencyResolver mResolver;
    private TextView messagesTextView;
    private String[] scopes = new String[]{"http://outlook.office.com/Mail.Read"};
    ```

    添加下列方法至 MainActivity 类。logon() 方法构建并初始化 ADAL 的 AuthenticationContext，执行交互登录，并使用随时可用的 AuthenticationContext 构造 DependencyResolver。

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

    还需要通过添加此方法至其类来配置MainActivity，以将身份验证结果传递回 AuthenticationContext：

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }
    ```

    从 MainActivity.onCreate，缓存消息 TextView，随后调用 logon() 并使用下列代码挂接到完成：

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

4. 现在，添加必要的代码以创建 API 客户端。

    使用 Outlook base URL 添加私有静态变量：

    ```java
    private static final String outlookBaseUrl = "https://outlook.office.com/api/v2.0";
    ```

    为客户端添加私有实例变量：

    ```java
    private OutlookClient mClient;
    ```

    最后通过构建客户端并使用完成 onSuccess 方法。我们将在下一步中定义 getMessages() 方法。

    ```java
    @Override
    public void onSuccess(Boolean result) {
        mClient = new OutlookClient(outlookBaseUrl, mResolver);
        //call methods with the client.
    }
    ```


5. 创建新方法，以使用客户端从收件箱中获取所有邮件。

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

如果成功，收件箱中检索的邮件数量将在 TextView 中显示。:)

## 常见问题解答


## 参与
您需要在提交拉取请求之前签署[参与者许可协议](https://cla.microsoft.com/)。要完成参与者许可协议 (CLA)，你需要通过表格提交请求，并在收到包含文件链接的电子邮件时在 参与者许可协议上提交电子签名。只需针对任何 Microsoft Open Technologies OSS 项目执行一次此操作。

此项目已采用 [Microsoft 开放源代码行为准则](https://opensource.microsoft.com/codeofconduct/)。有关详细信息，请参阅[行为准则 FAQ](https://opensource.microsoft.com/codeofconduct/faq/)。如有其他任何问题或意见，也可联系 [opencode@microsoft.com](mailto:opencode@microsoft.com)。

## 许可证
版权所有 (c) Microsoft Corporation。保留所有权利。在 MIT 许可证下获得许可。
