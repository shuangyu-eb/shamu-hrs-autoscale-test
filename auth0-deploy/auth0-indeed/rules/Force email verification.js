function emailVerified(user, context, callback) {
  user.app_metadata = user.app_metadata || {};
  if (user.app_metadata.verification_email_sent && !user.app_metadata.email_verified_indeed) {
    const query = context.request.query;
    if (query && query.isUserVerified === 'true' && query.userId === user.app_metadata.id) {
    	user.app_metadata.email_verified_indeed = true;
      auth0.users.updateAppMetadata(user.user_id, user.app_metadata);
      return callback(null, user, context);
    } else {
    	return callback(new UnauthorizedError('Please verify your email before logging in.'));
    }
  } else {
    return callback(null, user, context);
  }
}
