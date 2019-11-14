function (user, context, callback) {
  const bcrypt = require('bcrypt');

  function addToken(payload) {
    const namespace = configuration['custom-namespace'];
    context.idToken[`${namespace}userSecret`] = payload;
    context.accessToken[`${namespace}userSecret`] = payload;
  }

  const { id: user_id, userSecret } = user.app_metadata;

  if (userSecret) {
    addToken(userSecret);
    return callback(null, user, context);
  }

  const salt = bcrypt.genSaltSync(10);
  const hash = bcrypt.hashSync(`${user_id}`, salt);
  user.app_metadata.userSecret = hash;

  auth0.users.updateAppMetadata(user.user_id, user.app_metadata)
  .then(function(){
    addToken(hash);
    return callback(null, user, context);
  })
  .catch(function(err){
    return callback(err);
  });
}
