function setRolesToUser(user, context, callback) {
  const axios = require('axios@0.18.0');

  const applicationClientId = configuration['management-client-id'];
  const applicationClientSecret = configuration['management-client-secret'];
  const grantType = 'client_credentials';
  const applicationDomain = configuration['auth0-domain'];
  
  user.app_metadata = user.app_metadata || {};
  if (!user.identities[0].isSocial || (user.app_metadata && user.app_metadata.isGranted)) {
    return callback(null, user, context);
  }
  
  const addRolesToUser = function (user) {
    const idVerified = user.app_metadata.idVerified;
    if (idVerified || (context.request && context.request.query && context.request.query.employeeId)) {
      return 'EMPLOYEE';
    } 
    return 'ADMIN';
  };

  const targetRole = addRolesToUser(user);

  if ((user.app_metadata.verification_email_sent && user.app_metadata.email_verified_indeed) || user.app_metadata.isEmployee) {
    axios.post(
      `https://${applicationDomain}/oauth/token`,
      {
        grant_type: grantType,
        client_id: applicationClientId,
        client_secret: applicationClientSecret,
        audience: `https://${applicationDomain}/api/v2/`
      },
      { headers: { "content-type": "application/json" } }
    ).then((response) => {
      const token = response.data.access_token;
      return axios.get(`https://${applicationDomain}/api/v2/roles`, {
        headers: {
          "cache-control": "no-cache",
          authorization: `Bearer ${token}`,
          "content-type": "application/json"
        }
      }).then((response) => {
          const roles = response.data;
          if (!roles) callback(new Error('Error while getting roles.'));
          
          let roleId;
          roles.forEach((role) => {
            if (role.name === targetRole) {
              roleId = role.id;
            }
          });
          
          return roleId;
        }).then((roleId) => {
          return axios.post(
            `https://${applicationDomain}/api/v2/users/${user.user_id}/roles`,
            { roles: [roleId] },
            {
              headers: {
                "cache-control": "no-cache",
                authorization: `Bearer ${token}`,
                "content-type": "application/json"
              }
            });
          }).then(() => {
            user.app_metadata.isGranted = true;
            auth0.users.updateAppMetadata(user.user_id, user.app_metadata);
            callback(null, user, context);
          });
        }).catch((error) => {
          callback(error);
        });
  } else {
    callback(null, user, context);
  }
}
