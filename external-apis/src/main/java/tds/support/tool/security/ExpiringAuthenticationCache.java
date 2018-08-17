package tds.support.tool.security;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.springframework.security.oauth2.common.OAuth2AccessToken.EXPIRES_IN;

/**
 * Caches authentication tokens.
 */
class ExpiringAuthenticationCache
        extends ForwardingCache<String, OAuth2Authentication>
        implements LoadingCache<String, OAuth2Authentication> {

    private final LoadingCache<String, OAuth2Authentication> delegate;

    ExpiringAuthenticationCache(final Function<String, OAuth2Authentication> loader) {
        delegate = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build(new CacheLoader<String, OAuth2Authentication>() {
                    @Override
                    public OAuth2Authentication load(final String key) {
                        return loader.apply(key);
                    }
                });
    }

    @Override
    protected Cache<String, OAuth2Authentication> delegate() {
        return delegate;
    }

    @Override
    public OAuth2Authentication get(final String key) throws ExecutionException {
        final String now = Long.toString(System.currentTimeMillis());
        final OAuth2Authentication authentication = delegate.get(key);
        if (authentication.getOAuth2Request().getRequestParameters().get(EXPIRES_IN).compareTo(now) > 0) {
            return authentication;
        }
        // token has probably expired, so invalidate to force a reload
        delegate.invalidate(key);
        return delegate.get(key);
    }

    @Override
    public OAuth2Authentication getUnchecked(final String key) {
        return delegate.getUnchecked(key);
    }

    @Override
    public ImmutableMap<String, OAuth2Authentication> getAll(final Iterable<? extends String> keys) throws ExecutionException {
        return delegate.getAll(keys);
    }

    @SuppressWarnings("deprecation")
    @Override
    public OAuth2Authentication apply(final String key) {
        return delegate.apply(key);
    }

    @Override
    public void refresh(final String key) {
        delegate.refresh(key);
    }
}
