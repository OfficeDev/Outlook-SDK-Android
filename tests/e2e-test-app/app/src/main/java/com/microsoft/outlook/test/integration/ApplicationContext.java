package com.microsoft.outlook.test.integration;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;
import com.microsoft.outlook.test.integration.android.Constants;
import com.microsoft.outlook.test.integration.framework.TestCase;
import com.microsoft.outlook.test.integration.framework.TestExecutionCallback;
import com.microsoft.outlook.test.integration.framework.TestResult;
import com.microsoft.services.orc.auth.AuthenticationCredentials;
import com.microsoft.services.orc.core.DependencyResolver;
import com.microsoft.services.orc.http.Credentials;
import com.microsoft.services.orc.http.impl.OAuthCredentials;
import com.microsoft.services.orc.http.impl.OkHttpTransport;
import com.microsoft.services.orc.serialization.impl.GsonSerializer;
import com.microsoft.services.outlook.fetchers.OutlookClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ApplicationContext {

    static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private static Activity mActivity;
    public static AuthenticationContext mAADAuthContext = null;

    public static void initialize(Activity activity) {
        mActivity = activity;
        try {
            mAADAuthContext = new AuthenticationContext(mActivity, Constants.AUTHORITY_URL, true);
        } catch (Throwable e) {
            logger.error("E2ETestApp", "Error creating AuthenticationContext: " + e.getMessage(), e);
        }
    }


    public static String getExchangeServerUrl() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getString(Constants.PREFERENCE_EXCHANGE_RESOURCE_URL,
                "");
    }


    public static String getClientId() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getString(Constants.PREFERENCE_AAD_CLIENT_ID,
                "");
    }

    public static String getRedirectUrl() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
                Constants.PREFERENCE_AAD_REDIRECT_URL, "");
    }

    public static String getExchangeEndpointUrl() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
                Constants.PREFERENCE_EXCHANGE_ENDPOINT_URL, "");
    }


    public static String getTestMail() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getString(
                Constants.PREFERENCE_TEST_MAIL, "");
    }


    public static AuthenticationContext getAuthenticationContext() {
        return mAADAuthContext;
    }


    public static void executeTest(final TestCase testCase, final TestExecutionCallback callback) {
        AsyncTask<Void, Void, TestResult> task = new AsyncTask<Void, Void, TestResult>() {


            protected TestResult doInBackground(Void... params) {
                return testCase.executeTest();
            }


            protected void onPostExecute(TestResult result) {
                callback.onTestComplete(testCase, result);
            }
        };

        task.execute();
    }


    public static OutlookClient getOutlookClient() {
        return getTClientAAD(getExchangeServerUrl(), getExchangeEndpointUrl(), OutlookClient.class);
    }


    public static InputStream getResource(int id) {
        return mActivity.getResources().openRawResource(id);
    }


    public static long getResourceSize(int id) {
        InputStream stream = mActivity.getResources().openRawResource(id);
        try {
            byte[] bytes = ByteStreams.toByteArray(stream);
            return bytes.length;
        } catch (IOException e) {
            return 0;
        }
    }

    private static <TClient> TClient getTClientAAD(String serverUrl, final String endpointUrl, final Class<TClient> clientClass) {
        final SettableFuture<TClient> future = SettableFuture.create();

        try {
            getAuthenticationContext().acquireToken(
                    mActivity, serverUrl,
                    getClientId(), getRedirectUrl(), PromptBehavior.Auto,
                    new AuthenticationCallback<AuthenticationResult>() {


                        public void onError(Exception exc) {
                            future.setException(exc);
                        }


                        public void onSuccess(AuthenticationResult result) {
                            TClient client;
                            try {
                                client = clientClass.getDeclaredConstructor(String.class, DependencyResolver.class)
                                        .newInstance(endpointUrl, getDependencyResolver(result.getAccessToken()));
                                future.set(client);
                            } catch (Throwable t) {
                                onError(new Exception(t));
                            }
                        }
                    });


        } catch (Throwable t) {
            future.setException(t);
        }
        try {
            return future.get();
        } catch (Throwable t) {
            logger.error(Constants.TAG, t.getMessage(), t);
            return null;
        }
    }

    private static DependencyResolver getDependencyResolver(final String token) {

        return new DependencyResolver.Builder(
                new OkHttpTransport(), new GsonSerializer(),
                new AuthenticationCredentials() {
                    @Override
                    public Credentials getCredentials() {
                        return new OAuthCredentials(token);
                    }
                }).build();
    }

    public static File createTempFile(long sizeInKb) throws IOException {

        File tempFile = File.createTempFile("Office", "Test");
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);

        for (int i = 0; i < sizeInKb; i++) {
            byte[] buffer = new byte[1024];
            out.write(buffer);
        }

        out.close();

        return tempFile;
    }
}
