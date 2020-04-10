function (user, context, callback) {
  
  if (!user.email_verified) {
    return callback(new UnauthorizedError('Email not verified!'));
  }
  callback(null, user, context);
}