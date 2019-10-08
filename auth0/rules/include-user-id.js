function (user, context, callback) {

  user.app_metadata = user.app_metadata || {};
  if (user.app_metadata.id) {
    const namespace = configuration['custom-namespace'];
    context.accessToken[`${namespace}id`] = user.app_metadata.id;
    context.idToken[`${namespace}id`] = user.app_metadata.id;
  }
  callback(null, user, context);
}
