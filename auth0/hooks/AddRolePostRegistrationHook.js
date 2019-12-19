/**
 @param {object} user - The user being created
 @param {string} user.id - user id
 @param {string} user.tenant - Auth0 tenant name
 @param {string} user.username - user name
 @param {string} user.email - email
 @param {boolean} user.emailVerified - is e-mail verified?
 @param {string} user.phoneNumber - phone number
 @param {boolean} user.phoneNumberVerified - is phone number verified?
 @param {object} user.user_metadata - user metadata
 @param {object} user.app_metadata - application metadata
 @param {object} context - Auth0 connection and other context info
 @param {string} context.requestLanguage - language of the client agent
 @param {object} context.connection - information about the Auth0 connection
 @param {object} context.connection.id - connection id
 @param {object} context.connection.name - connection name
 @param {object} context.connection.tenant - connection tenant
 @param {object} context.webtask - webtask context
 @param {function} cb - function (error, response)
 * applicationClientId and applicationClientSecret are from a machine-to-machine application
 * which has permission of management api.
 *
 */
module.exports = function (user, context, cb) {
  const axios = require('axios@0.18.0');

  const databaseName = 'XXXXXX';
  // applicationClientId and applicationClientSecret are from a machine-to-machine application
  // which has permission of management api.
  const applicationClientId = 'XXXXXXXXXXXXXX';
  const applicationClientSecret = 'XXXXXXXXXXXXXXXXXXXXXXXX';
  const grantType = 'client_credentials';
  // Domain of your spa application without prefix like http/https
  const applicationDomain = 'XXXXXXXXXXXXXXXXXX';

  let targetRole = 'EMPLOYEE';

  const appMetadata = user.app_metadata || {};
  const idVerified = appMetadata.idVerified;
  const role = appMetadata.role;

  if (role && idVerified) {
    if (role == 'SUPER_ADMIN') {
      return cb(new Error("Unauthorized creation of super admin user."));
    }

    targetRole = role;
  } else if (!role && !idVerified) {
    targetRole = 'ADMIN';
  }

  if (context.connection.name === databaseName) {
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
        if (!roles) cb(new Error('Error while getting roles.'));

        let roleId;
        roles.forEach((role) => {
          if (role.name === targetRole) {
            roleId = role.id;
          }
        });

        return roleId;
      }).then((roleId) => {
        return axios.post(
            `https://${applicationDomain}/api/v2/users/auth0|${user.id}/roles`,
            { roles: [roleId] },
            {
              headers: {
                "cache-control": "no-cache",
                authorization: `Bearer ${token}`,
                "content-type": "application/json"
              }
            });
      }).then(() => {
        if (appMetadata) {
          delete appMetadata.role;
        }

        return axios.patch(
            `https://${applicationDomain}/api/v2/users/auth0|${user.id}`,
            { "app_metadata": appMetadata },
            {
              headers: {
                "cache-control": "no-cache",
                authorization: `Bearer ${token}`,
                "content-type": "application/json"
              }
            });
      }).then(() => {
        cb();
      });
    }).catch((error) => {
      cb(error);
    });
  } else {
    cb();
  }
};
