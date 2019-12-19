function (user, context, callback) {

  context.stats = context.stats || {};
  if (context.stats.loginsCount) {
    const namespace = configuration['custom-namespace'];

    context.accessToken[`${namespace}loginsCount`] = context.stats.loginsCount;
    context.idToken[`${namespace}loginsCount`] = context.stats.loginsCount;
  }
  callback(null, user, context);
}
