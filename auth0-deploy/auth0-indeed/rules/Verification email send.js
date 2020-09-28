function (user, context, callback) {
  const hrisDomain = configuration['hris-domain'];
  user.app_metadata = user.app_metadata || {};

  function sendVerificationEmail(user, context) {
    const axios = require('axios@0.18.0');
    const hrisGateway = configuration['hris-gateway'];

    axios.post(`${hrisGateway}/company/users/indeed-verification-email`, user)
    .catch((error) => {
      console.log(error);
    });
  }
	
  if(!user.app_metadata.isEmployee && !user.app_metadata.verification_email_sent) {
    sendVerificationEmail(user, context);
    context.redirect = {
        url: `${hrisDomain}/account/sign-up/done`,
      };
  }

  return callback(null, user, context);
}
