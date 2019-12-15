package lu.uni.avatov.guichetetudiant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpBackend implements GuichetEtudiant.NetworkBackend  {
	private static final String CLASS_TAG = "OkHttpBackend";
	private CookieJar cookieJar;
	private OkHttpClient client;
	private OkHttpAuthenticator authenticator;

	public OkHttpBackend() {
		cookieJar = new OkHttpCookieJar();
		authenticator = new OkHttpAuthenticator();
	}

	private OkHttpClient getClient() {
		if(client != null) return client;

		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			//.followRedirects(true)
			//.followSslRedirects(true)
			.cookieJar(cookieJar);

		httpClientBuilder.addNetworkInterceptor(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException
			{
				Request request = chain.request();

				Response response = chain.proceed(request);
				//Log.v(CLASS_TAG, String.format("(%s) %s", response.code(), request.url()));

				return response;
			}
		});

//		httpClientBuilder.addInterceptor(new Interceptor() {
//			@Override
//			public Response intercept(Chain chain) throws IOException
//			{
//				Request request = chain.request();
//
//				Request requestWithUserAgent = request.newBuilder()
//					.header("User-Agent", System.getProperty("http.agent"))
//					.build();
//
//				return chain.proceed(requestWithUserAgent);
//			}
//		});



		httpClientBuilder.authenticator(authenticator);

		client = httpClientBuilder.build();
		return client;
	}

	protected static URL generateURL(String urlSuffix) throws GEError {
		try {
			return new URL(GuichetEtudiant.urlPrefix + urlSuffix);
		} catch(MalformedURLException e) {
			throw new GEError("Guichet Étudiant Error: Malformed URL");
		}
	}

	@Override
	public String get(String urlSuffix) throws GEError {
		OkHttpClient client = getClient();
		Request request = new Request.Builder().url(generateURL(urlSuffix)).build();

		System.err.println("OkHttpBackend: GET " + request.url());

		try {
			Response response = client.newCall(request).execute();

			if (response.code() == 401)
				throw new GEAuthenticationError("Authentication to Guichet Étudiant server was unsuccessful!");

			else if (!response.isSuccessful())
				throw new GEError("Guichet Étudiant Error: " + response.message());

			else if (response.body() == null)
				throw new GEError("Guichet Étudiant Error: got empty response!");

			else
				return response.body().string();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new GEError("Guichet Étudiant: An IOException occurred: " + e.toString());
		}
	}

	@Override
	public String post(String urlSuffix, ParametersMultimap parameters) throws GEError {
		OkHttpClient client = getClient();

		FormBody.Builder body = new FormBody.Builder();
		for (ParametersMultimap.Entry entry : parameters)
			body.add(entry.getKey(), entry.getValue());

		FormBody b = body.build();

		Request request = new Request.Builder()
				.url(generateURL(urlSuffix))
				.post(b)
				.build();

		System.err.println("OkHttpBackend: POST " + request.url());

		try {
			Response response = client.newCall(request).execute();

			if (response.code() == 401)
				throw new GEAuthenticationError("Authentication to Guichet Étudiant server was unsuccessful!");

			else if (!response.isSuccessful())
				throw new GEError("Guichet Étudiant Error: " + response.message());

			else if (response.body() == null)
				throw new GEError("Guichet Étudiant Error: got empty response!");

			else
				return response.body().string();
		}
		catch (IOException e) {
			throw new GEError("Guichet Étudiant: An IOException occurred: " + e.toString());
		}
	}

	@Override
	public void setCredentials(String username, String password) {
		authenticator.set(username, password);
	}
}
