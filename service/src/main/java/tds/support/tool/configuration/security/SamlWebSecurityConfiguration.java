package tds.support.tool.configuration.security;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.trust.httpclient.TLSProtocolSocketFactory;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;
import tds.support.tool.security.SamlUserDetailsServiceImpl;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

@EnableWebSecurity
@Import({
        SamlSettings.class,
        SamlUserDetailsServiceImpl.class
})
public abstract class SamlWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private SamlSettings samlSettings;

    @Autowired
    private SamlUserDetailsServiceImpl samlUserDetailsServiceImpl;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private Set<AbstractEndpoint> actuatorEndpoints;

    // Initialization of OpenSAML library
    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    // XML parser pool needed for OpenSAML parsing
    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    // Bindings, encoders and decoders used for creating and parsing messages
    @Bean
    public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
        return new MultiThreadedHttpConnectionManager();
    }

    @Bean
    public HttpClient httpClient() {
        return new HttpClient(multiThreadedHttpConnectionManager());
    }

    // SAML Authentication Provider responsible for validating of received SAML
    // messages
    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        final SAMLAuthenticationProvider provider = new SAMLAuthenticationProvider();
        provider.setUserDetails(samlUserDetailsServiceImpl);
        provider.setForcePrincipalAsString(false);
        return provider;
    }

    // Provider of default SAML Context
    @Bean
    public SAMLContextProvider contextProvider() throws ServletException {
        final SamlSettings.LoadBalancedSettings lbSettings = samlSettings.getLoadBalance();
        if (!lbSettings.isEnabled()) {
            return new SAMLContextProviderImpl();
        }
        final SAMLContextProviderLB contextProvider = new SAMLContextProviderLB();
        contextProvider.setScheme(lbSettings.getScheme());
        contextProvider.setServerName(lbSettings.getHostname());
        contextProvider.setContextPath(lbSettings.getContextPath());
        contextProvider.setServerPort(lbSettings.getPort());
        contextProvider.setIncludeServerPortInRequestURL(lbSettings.isIncludeServerPort());
        return contextProvider;
    }

    /**
     * Filter to extract values from "Forwarded" and "X-Forwarded-*" headers
     * in order to wrap and override the request schema, host, etc properties.
     * This is necessary for load balancer integration and must be registered before the security filter chain.
     *
     * @return the filter
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    // Logger for SAML messages and events
    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        return new WebSSOProfileConsumerImpl();
    }

    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 Web SSO profile
    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    // SAML 2.0 Holder-of-Key Web SSO profile
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 ECP profile
    @Bean
    public WebSSOProfileECPImpl ecpprofile() {
        return new WebSSOProfileECPImpl();
    }

    @Bean
    public SingleLogoutProfile logoutprofile() {
        return new SingleLogoutProfileImpl();
    }

    @Bean
    public KeyManager keyManager() {
        final Resource storeFile = resourceLoader.getResource(samlSettings.getKeyStoreFile());
        final String storePass = samlSettings.getKeyStorePassword();
        final Map<String, String> passwords = new HashMap<>();
        passwords.put(samlSettings.getPrivateKeyEntryAlias(), samlSettings.getPrivateKeyEntryPassword());
        final String defaultKey = samlSettings.getPrivateKeyEntryAlias();
        return new JKSKeyManager(storeFile, storePass, passwords, defaultKey);
    }

    // Setup TLS Socket Factory
    @Bean
    public TLSProtocolConfigurer tlsProtocolConfigurer() {
        return new TLSProtocolConfigurer();
    }

    @Bean
    public ProtocolSocketFactory socketFactory() {
        return new TLSProtocolSocketFactory(keyManager(), null, "default");
    }

    @Bean
    public Protocol socketFactoryProtocol() {
        return new Protocol("https", socketFactory(), 443);
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        final WebSSOProfileOptions options = new WebSSOProfileOptions();
        options.setIncludeScoping(false);
        return options;
    }

    // SAML Entry point to initialize authentication, default values taken from
    // properties file
    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        final SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    // Setup advanced info about metadata
    @Bean
    public ExtendedMetadata extendedMetadata() {
        final ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        //extendedMetadata.setIdpDiscoveryEnabled(true); todo: try setting to false
        extendedMetadata.setSignMetadata(false);
        extendedMetadata.setRequireLogoutRequestSigned(false);
        return extendedMetadata;
    }

    // IDP Discovery Service
    // todo: may not need this
    @Bean
    public SAMLDiscovery samlIDPDiscovery() {
        final SAMLDiscovery idpDiscovery = new SAMLDiscovery();
        idpDiscovery.setIdpSelectionPath("/saml/idpSelection");
        return idpDiscovery;
    }

    @Bean
    @Qualifier("idp-ssocircle")
    public ExtendedMetadataDelegate ssoCircleExtendedMetadataProvider() throws MetadataProviderException {
        final String idpSSOCircleMetadataURL = samlSettings.getIdpMetadataUrl();
        final Timer backgroundTaskTimer = new Timer(true);
        final HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(backgroundTaskTimer, httpClient(), idpSSOCircleMetadataURL);
        httpMetadataProvider.setParserPool(parserPool());
        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(httpMetadataProvider, extendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(true);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        return extendedMetadataDelegate;
    }

    // IDP Metadata configuration - paths to metadata of IDPs in circle of trust
    // is here Do no forget to call iniitalize method on providers
    @Bean
    public CachingMetadataManager metadata() throws MetadataProviderException {
        final List<MetadataProvider> providers = new ArrayList<>();
        providers.add(ssoCircleExtendedMetadataProvider());
        return new CachingMetadataManager(providers);
    }

    // Filter automatically generates default SP metadata
    @Bean
    public MetadataGenerator metadataGenerator() {
        final MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setEntityId(samlSettings.getSpEntityId());
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setEntityBaseURL(samlSettings.getEntityBaseUrl());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());
        return metadataGenerator;
    }

    // The filter is waiting for connections on URL suffixed with filterSuffix
    // and presents SP metadata there
    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }

    @Bean
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
        final SAMLWebSSOHoKProcessingFilter filter = new SAMLWebSSOHoKProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return filter;
    }

    // Processing filter for WebSSO profile messages
    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
        final SAMLProcessingFilter filter = new SAMLProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return filter;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }

    // Handler for successful logout
    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        final SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/");
        return handler;
    }

    // Logout handler terminating local session
    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        final SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.setInvalidateHttpSession(true);
        handler.setClearAuthentication(true);
        return handler;
    }

    // Filter processing incoming logout messages
    // First argument determines URL user will be redirected to after successful
    // global logout
    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
    }

    // Overrides default logout processing filter with the one processing SAML
    // messages
    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successLogoutHandler(),
                new LogoutHandler[]{logoutHandler()},
                new LogoutHandler[]{logoutHandler()}
        );
    }

    // Bindings
    private ArtifactResolutionProfile artifactResolutionProfile() {
        final ArtifactResolutionProfileImpl profile = new ArtifactResolutionProfileImpl(httpClient());
        profile.setProcessor(new SAMLProcessorImpl(soapBinding()));
        return profile;
    }

    @Bean
    public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
        return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
    }

    @Bean
    public HTTPSOAP11Binding soapBinding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPSOAP11Binding httpSOAP11Binding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding httpPAOS11Binding() {
        return new HTTPPAOS11Binding(parserPool());
    }

    // Processor
    @Bean
    public SAMLProcessorImpl processor() {
        final Collection<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        bindings.add(artifactBinding(parserPool(), velocityEngine()));
        bindings.add(httpSOAP11Binding());
        bindings.add(httpPAOS11Binding());
        return new SAMLProcessorImpl(bindings);
    }

    /**
     * Define the security filter chain in order to support SSO Auth by using SAML 2.0
     *
     * @return Filter chain proxy
     * @throws Exception an error occurs
     */
    @Bean
    public FilterChainProxy samlFilter() throws Exception {
        final List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"), samlWebSSOHoKProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), samlIDPDiscovery()));
        return new FilterChainProxy(chains);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(samlAuthenticationProvider());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/*.ico", "/assets/public/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .authenticationEntryPoint(delegatingEntryPoint());
        http
                // this filter must run first for load balancing to work
                .addFilterBefore(forwardedHeaderFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(metadataGeneratorFilter(), ForwardedHeaderFilter.class)
                .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/error", "/error/**").permitAll()
                .antMatchers("/saml/**").permitAll();

        for (final AbstractEndpoint endpoint : actuatorEndpoints) {
            http.authorizeRequests().antMatchers("/" + endpoint.getId() + "/**").permitAll();
        }

        http
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .anyRequest().authenticated();
        http
                .logout()
                .logoutSuccessUrl("/");

        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();
    }

    @Bean
    public DelegatingAuthenticationEntryPoint delegatingEntryPoint() {
        final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(new AntPathRequestMatcher("/api/**"), new Http401NotAuthorizedNoChallengeEntryPoint());

        final DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        delegatingEntryPoint.setDefaultEntryPoint(samlEntryPoint());
        return delegatingEntryPoint;
    }

    public abstract AuthenticationSuccessHandler authenticationSuccessHandler();

    public abstract AuthenticationFailureHandler authenticationFailureHandler();

}
