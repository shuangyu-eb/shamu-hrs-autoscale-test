const AUTH0_DIRECTORY = '../auth0';

const backend = require(AUTH0_DIRECTORY + '/resource-servers/HRS backend');
const adminRole = require(AUTH0_DIRECTORY + '/roles/ADMIN');
const employeeRole = require(AUTH0_DIRECTORY + '/roles/EMPLOYEE');
const inactivatedRole = require(AUTH0_DIRECTORY + '/roles/INACTIVATE');
const managerRole = require(AUTH0_DIRECTORY + '/roles/MANAGER');
const superAdminRole = require(AUTH0_DIRECTORY + '/roles/SUPER_ADMIN');

describe('User roles', () => {

    const { scopes } = backend;
    const scopeValues = scopes.map(scope => scope.value);
    const SUPER_PERMISSION = 'SUPER_PERMISSION';

    describe('Admin Role', () => {

        const { permissions } = adminRole;

        test('Should not include any roles not specified in HRS backend scopes', () => {
            permissions.forEach((permission) => {
                expect(scopeValues).toContain(permission.permission_name);
            });
        });

        test('Should have more permissions than employee role', () => {
            expect(permissions.length).toBeGreaterThan(employeeRole.permissions.length);
        });

        test('Should not include super permission', () => {
            expect(permissions.some(p => p.permission_name === SUPER_PERMISSION)).toBe(false);
        });
    });

    describe('Employee Role', () => {

        const { permissions } = employeeRole;

        test('Should not include any roles not specified in HRS backend scopes', () => {
            permissions.forEach((permission) => {
                expect(scopeValues).toContain(permission.permission_name);
            });
        });

        test('Should have more permissions than inactivated role', () => {
            expect(permissions.length).toBeGreaterThan(inactivatedRole.permissions.length);
        });

        test('Should not include super permission', () => {
            expect(permissions.some(p => p.permission_name === SUPER_PERMISSION)).toBe(false);
        });
    });

    describe('Inactivated Role', () => {

        const { permissions } = inactivatedRole;

        test('Should not include any roles not specified in HRS backend scopes', () => {
            permissions.forEach((permission) => {
                expect(scopeValues).toContain(permission.permission_name);
            });
        });

        test('Should not include super permission', () => {
            expect(permissions.some(p => p.permission_name === SUPER_PERMISSION)).toBe(false);
        });
    });

    describe('Manager Role', () => {

        const { permissions } = managerRole;

        test('Should not include any roles not specified in HRS backend scopes', () => {
            permissions.forEach((permission) => {
                expect(scopeValues).toContain(permission.permission_name);
            });
        });

        test('Should not include super permission', () => {
            expect(permissions.some(p => p.permission_name === SUPER_PERMISSION)).toBe(false);
        });
    });

    describe('Super Admin Role', () => {

        const { permissions } = superAdminRole;

        test('Should not include any roles not specified in HRS backend scopes', () => {
            permissions.forEach((permission) => {
                expect(scopeValues).toContain(permission.permission_name);
            });
        });

        test('Should have more permissions than admin role', () => {
            expect(permissions.length).toBeGreaterThan(adminRole.permissions.length);
        });
    });
});
