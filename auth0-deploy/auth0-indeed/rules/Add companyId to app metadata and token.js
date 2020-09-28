function (user, context, callback) {
  user.app_metadata = user.app_metadata || {};
  function includeCompanyId(companyId) {
    const namespace = configuration['custom-namespace'];
    context.accessToken[`${namespace}companyId`] = companyId;
    context.idToken[`${namespace}companyId`] = companyId;
  }
  if (context.request.query && context.request.query.employeeId) {
    const companyId = context.request.query.companyId;
    const userSecret = context.request.query.userSecret;
    user.app_metadata.companyId = companyId;
    user.app_metadata.userSecret = userSecret;
    auth0.users.updateAppMetadata(user.user_id, user.app_metadata);
    includeCompanyId(user.app_metadata.companyId);
  }
	if ((user.app_metadata.verification_email_sent && user.app_metadata.email_verified_indeed) || user.app_metadata.isEmployee) {
    includeCompanyId(user.app_metadata.companyId);
  }
  callback(null, user, context);
}
