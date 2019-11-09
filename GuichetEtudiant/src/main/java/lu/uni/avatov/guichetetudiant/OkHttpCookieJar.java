package lu.uni.avatov.guichetetudiant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class OkHttpCookieJar implements CookieJar {
  private final HashMap<String, HashMap<String, Cookie>> cookieStore = new HashMap<>();

  @Override
  public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    HashMap<String, Cookie> hostCookies = cookieStore.get(url.host());
    if (hostCookies == null) {
      hostCookies = new HashMap<>();
      cookieStore.put(url.host(), hostCookies);
    }
    for (Cookie c: cookies) {
      hostCookies.put(c.name(), c);
    }
    System.err.println("Saving " + cookies.size() + " cookies for " + url);
  }

  @Override
  public List<Cookie> loadForRequest(HttpUrl url) {
    HashMap<String, Cookie> hostCookies = cookieStore.get(url.host());
    if(hostCookies == null) {
      System.err.println("Loading 0 cookies for " + url);
      return new ArrayList<Cookie>();
    }

    Collection<Cookie> cookieCollection = hostCookies.values();
    List<Cookie> cookies = new ArrayList<Cookie>(cookieCollection.size());
    for (Cookie c: cookieCollection) {
      cookies.add(c);
    }
    System.err.println("Loading " + cookies.size() + " cookies for " + url);
    return cookies;
  }
}
