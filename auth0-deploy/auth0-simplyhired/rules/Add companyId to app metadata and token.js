function (user, context, callback) {
  user.app_metadata = user.app_metadata || {};
  function includeCompanyId(companyId) {
    const namespace = configuration['custom-namespace'];
    context.accessToken[`${namespace}companyId`] = companyId;
    context.idToken[`${namespace}companyId`] = companyId;
  }

  includeCompanyId(user.app_metadata.companyId);
  callback(null, user, context);
}
