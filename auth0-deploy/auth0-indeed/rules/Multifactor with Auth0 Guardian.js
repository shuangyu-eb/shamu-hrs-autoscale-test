function (user, context, callback) {
    const authorization = context.authorization || {};
    const roles = authorization.roles || [];

    const requireMFA = roles.some(role => (role === 'ADMIN' || role === 'SUPER_ADMIN'));

    const { email = '' } = user;
    const isTestAccount = email.startsWith('shamu.hrs.selenium+') && email.endsWith('gmail.com');

    if (requireMFA && !isTestAccount) {
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
