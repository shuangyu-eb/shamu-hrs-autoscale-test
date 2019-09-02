/**
 * Please configuration necessary items in rule configurations from Auth0 dashboard.
 * `Configuration` is a global configuration object available in rules.
 */
function (user, context, callback) {

  const jwt = require('jsonwebtoken@8.5.0');
  const uuid = require('uuid@3.3.2');

  // should first verify
  if (!user.email || !user.email_verified) {
    return callback(null, user, context);
  }

  user.app_metadata = user.app_metadata || {};

  function createToken(payload) {
    var options = {
      expiresIn: '10m',
      issuer: configuration['hrs-web-issuer'],
    };
    return jwt.sign(payload, configuration['hrs-web-client-secret'], options);
  }

  if (user.app_metadata.id && user.app_metadata.idVerified) {
    return callback(null, user, context);
  }

  if(context.protocol === 'redirect-callback' && user.app_metadata.id && !user.app_metadata.idVerified) {

    const token = context.request.query.token;
    jwt.verify(
        token,
        configuration['hrs-web-client-secret'], {
          issuer: configuration['hrs-web-issuer']
        },
        function(error, decoded) {
          if (error) {
            return callback(new UnauthorizedError("Errors while resuming authentication ", error));
          }

          const userId = decoded.userId;

          if (user.app_metadata.id !== userId) {
            return callback(null, user, context);
          }

          user.app_metadata.idVerified = true;
          auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
          .then(function(){
            return callback(null, user, context);
          })
          .catch(function(err){
            callback(err);
          });
        });
  }


  if (!user.app_metadata.id || (user.app_metadata.id && !user.app_metadata.idVerified)) {

    let userId;

    if (user.app_metadata.id) {
      userId = user.app_metadata.id;
    } else {
      userId = uuid().replace(/-/g, '');
    }

    const payload = { email: user.email, userId: userId };
    const token = createToken(payload);

    user.app_metadata.id = userId;
    user.app_metadata.idVerified = false;
    return auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
    .then(function(){
      const stateString = context.request.query.stateString;
      // Redirect callback for filling user information in hrs database. The domain can be replaced with different environment domain.
      context.redirect = {
        url: `http://localhost:3000/account/sign-up/step2?token=${token}&stateString=${stateString}`
      };
      return callback(null, user, context);
    })
    .catch(function(err){
      return callback(err);
    });
  }
}
