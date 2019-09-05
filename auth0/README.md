#### Custom Domain  
To use custom domain, there are several steps to go through:  
1. Make sure that `Custom Domains` has been configured and verified from [Tenant Settings](https://manage.auth0.com/#/tenant).  
2. Make sure the auth0 connection configuration in custom universal custom login page is shown as follow:
    ```
    var webAuth = new auth0.WebAuth({
      clientID: config.clientID, 
      domain: config.auth0Domain, 
      //code omitted for brevity
      overrides: {
        __tenant: config.auth0Tenant,
        __token_issuer: config.authorizationServer.issuer
      },
      //code omitted for brevity
    });
    ```
3. Make sure front-end client is initialized with custom domain:
    ```
    createAuth0Client({
        domain: 'Custom Domain',
        client_id: AUTH0_CLIENT_ID,
        connection: AUTH0_DATABASE,
        audience: AUTH0_AUDIENCE,
        redirect_uri: `${ORIGIN}/parse`,
      })
    ```
4. Make sure that there are no localhost in `Allowed Callback URLS`, `Allowed Web Origins`, `Alloed Logout URLS`, \
`Allowed Origins` of application settings.  

<small>Note: even though `Custom Domain` is configured, we can use both Auth0 default domain and custom domain.</small>  
<small>Refer: https://auth0.com/docs/custom-domains</small> 

#### Skip User Consent  
Auth0 will always show the user consent form when your spa application is running as a third party application. This is \
distinguished by the url - if it's your own domain(not Auth0 domain or localhost).
  
To skip user consent, firstly make sure the button of `Allow Skipping User Consent` is flipped on at the API settings. \
Also make sure that localhost is not included in `Allowed Callback URLS`, `Allowed Web Origins`, `Alloed Logout URLS`, \
`Allowed Origins` of application settings. 

#### Default Auth0 Database  
Navigate to tenant settings(https://manage.auth0.com/#/tenant), in tab `General`, find the section `API Authorization Settings`. Fill the `Default Directory` with default database name. This \
is used for password grant. We use password grant for validating user password in features like unlock session, etc. 

