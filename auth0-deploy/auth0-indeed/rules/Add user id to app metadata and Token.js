function (user, context, callback) {
  const uuid = require('uuid@3.3.2');
  const hrisDomain = configuration['hris-domain'];
  user.app_metadata = user.app_metadata || {};
  function includeUserIdAndUserEmail(id, email) {
    const namespace = configuration['custom-namespace'];
    context.accessToken[`${namespace}id`] = id;
    context.accessToken[`${namespace}email`] = email;
    context.idToken[`${namespace}id`] = id;
  }
  
  const isEmployeeFirstTimeLogin = context.request.query && context.request.query.employeeId;
  if(!user.app_metadata.id && isEmployeeFirstTimeLogin) {
    let userId = context.request.query.employeeId;
    user.app_metadata.id = userId;
    user.app_metadata.isEmployee = true;
    auth0.users.updateAppMetadata(user.user_id, user.app_metadata);
  }

  if(user.app_metadata.id && !user.app_metadata.idVerified && !user.app_metadata.isEmployee) {
    const userInfo = {
      companyId: user.app_metadata.companyId,
      companyName: user.companyName,
      userId: user.app_metadata.id,
      workEmail: user.email,
    };
    const data = JSON.stringify(userInfo);
    const encryptedData = new Buffer(data).toString('base64');
    context.redirect = {
        url: `${hrisDomain}/account/sign-up/step2?data=${encryptedData}`,
    };
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

  includeUserIdAndUserEmail(user.app_metadata.id, user.email);
  callback(null, user, context);
}
