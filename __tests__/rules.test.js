const AUTH0_DIRECTORY = '../auth0';

const addUserIdToAppMetadata = require(AUTH0_DIRECTORY + '/rules/Add userId to app metadata.json');
const checkUserSecret = require(AUTH0_DIRECTORY + '/rules/Check user secret.json');
const disableUnverifiedUser = require(AUTH0_DIRECTORY + '/rules/Disable unverified user.json');
const includeUserId = require(AUTH0_DIRECTORY + '/rules/Include user id.json');
const multifactorWithGuardian = require(AUTH0_DIRECTORY + '/rules/Multifactor with Auth0 Guardian.json');
const userLoginsCount = require(AUTH0_DIRECTORY + '/rules/User Logins Count.json');

describe('Auth0 Rules', () => {

    const rules = [
        addUserIdToAppMetadata,
        checkUserSecret,
        disableUnverifiedUser,
        includeUserId,
        multifactorWithGuardian,
        userLoginsCount,
    ];

    test('All necessary rules should be included', () => {
      expect(rules).toHaveLength(6);
    });

    test('Rules should be in the correct order', () => {
      const sortedNames = rules.sort((a, b) => a.order - b.order).map(rule => rule.name);
      expect(sortedNames).toEqual([
         'Disable unverified user',
         'Add userId to app metadata',
         'Include user id',
         'User Logins Count',
         'Check user secret',
         'Multifactor with Auth0 Guardian',
      ]);
    });
});
