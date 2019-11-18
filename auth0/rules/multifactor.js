function (user, context, callback) {
    const authorization = context.authorization || {};
    const roles = authorization.roles || [];

    const requireMFA = roles.some(role => (role === 'ADMIN' || role === 'SUPER_ADMIN'));

    if (requireMFA) {
      // https://auth0.com/docs/multifactor-authentication/custom
      context.multifactor = {
        // required
        provider: 'any',

        // optional, defaults to true. Set to false to force Guardian authentication every time.
        // See https://auth0.com/docs/multifactor-authentication/custom#change-the-frequency-of-authentication-requests for details
        allowRememberBrowser: true
      };
    }

  callback(null, user, context);
}