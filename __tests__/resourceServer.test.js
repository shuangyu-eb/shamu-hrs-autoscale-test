const AUTH0_DIRECTORY = '../auth0';

const backend = require(AUTH0_DIRECTORY + '/resource-servers/HRS backend');

describe('HRS Backend Resource Server', () => {

    test('All scopes should include a description and value', () => {
        const { scopes } = backend;
        const notValid = [];
        scopes.forEach((scope) => {
            if (!scope.description || !scope.value) {
                notValid.push(scope);
            }
        });
        expect(notValid).toEqual([]);
    });

    test('All scope values should match their description', () => {
        const { scopes } = backend;
        const notMatching = [];
        scopes.forEach((scope) => {
            if (scope.description !== scope.value) {
                notMatching.push(scope);
            }
        });
        expect(notMatching).toEqual([]);
    });
});
