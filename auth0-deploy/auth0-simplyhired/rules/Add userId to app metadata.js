function (user, context, callback) {

  const uuid = require('uuid@3.3.2');

  // should first verify
  if (!user.email || !user.email_verified) {
    return callback(null, user, context);
  }

  user.app_metadata = user.app_metadata || {};


  if (user.app_metadata.id && user.app_metadata.idVerified) {
    return callback(null, user, context);
  }

  if(context.protocol === 'redirect-callback' && user.app_metadata.id && !user.app_metadata.idVerified) {

    user.app_metadata.idVerified = true;
    auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
    .then(function(){
      return callback(null, user, context);
    })
    .catch(function(err){
      callback(err);
    });
  }

  if (!user.app_metadata.id || (user.app_metadata.id && !user.app_metadata.idVerified)) {

    let userId;

    if (user.app_metadata.id) {
      userId = user.app_metadata.id;
    } else {
      userId = uuid().replace(/-/g, '');
    }

    user.app_metadata.id = userId;
    user.app_metadata.idVerified = false;
    return auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
    .then(function(){
      const stateString = context.request.query.stateString;
      context.redirect = {
        url: `##ENVIRONMENT_URL##/account/${userId}/sign-up/step2?stateString=${stateString}`,
      };
      return callback(null, user, context);
    })
    .catch(function(err){
      return callback(err);
    });
  }
}
