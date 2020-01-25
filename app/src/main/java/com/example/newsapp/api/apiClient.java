package com.example.newsapp.api;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class apiClient {

public static  final   String Base_Url = "https://newsapi.org/v2/";
public static Retrofit retrofit;






// here we make sure that we have only one instance from retrofit to make connection to the internet

public static Retrofit getApiClient()
{
    if (retrofit == null)
    {
        // it will take the base url and convert the data if it was found
        retrofit  = new Retrofit.Builder().baseUrl(Base_Url)
                .client(getUnsafeokHttpRequest().build())

                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    return retrofit;
}

public static OkHttpClient.Builder getUnsafeokHttpRequest()
{
    try {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });


        // avoid creating several instances, should be singleon
        OkHttpClient okHttpClient = builder.build();
        return builder;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
}







