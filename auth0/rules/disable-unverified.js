function (user, context, callback) {
  if (user.status.toString() === 'PENDING_VERIFICATION') {
    return callback(new UnauthorizedError('PENDING_VERIFICATION'));
  }
  callback(null, user, context);
}