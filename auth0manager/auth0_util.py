import config.config as config
from auth0.v3.authentication import GetToken
from auth0.v3.management import Auth0
from permission_loader import PermissionLoader


class Auth0Util(object):
    def __init__(self):
        self.accessToken = self._get_access_token()
        self.auth0 = Auth0(config.auth0Domain, self.accessToken)
        permission_loader = PermissionLoader()
        self.roles = permission_loader.roles
        self.permissions = permission_loader.permissions

    def update_all(self):
        self.update_roles()
        self.update_permissions()
        self.update_roles_permissions()

    @staticmethod
    def _get_access_token():
        token = GetToken(config.auth0Domain).client_credentials(config.auth0ClientId, config.auth0ClientSecret,
                                                                'https://%s/api/v2/' % config.auth0Domain)
        return token['access_token']

    def update_roles(self):
        existing_roles = self.get_all_roles()
        common_roles_name = self._get_common_roles_name(existing_roles)
        invalid_roles_id = self._get_invalid_roles_ids(existing_roles, common_roles_name)
        roles_to_add = self._get_roles_to_add(common_roles_name)
        self.delete_roles(invalid_roles_id)
        self.add_roles(roles_to_add)

    def update_permissions(self):
        permissions = []
        for permission in self.permissions:
            permissions.append({'value': permission.permission_name, 'description': permission.permission_description})
        params = {'scopes': permissions}
        self.auth0.resource_servers.update(config.auth0ApiIdentifier, params)
        print('Api %s permissions updated.' % config.auth0ApiIdentifier)

    def update_roles_permissions(self):
        role_name_permission_dict = self._get_role_permission_dict()
        roles = self.get_all_roles()
        for role in roles:
            permissions = role_name_permission_dict[role['name']]
            permissions_param = []
            for permission in permissions:
                permissions_param.append({'permission_name': permission.permission_name,
                                          'resource_server_identifier': config.auth0ApiIdentifier})
            self.auth0.roles.add_permissions(role['id'], permissions_param)
        print('Role permissions updated.')

    def _get_common_roles_name(self, existing_roles):
        latest_roles_name = set()
        for role in self.roles:
            latest_roles_name.add(role.role_name)

        existing_roles_name = set()
        for role in existing_roles:
            existing_roles_name.add(role['name'])

        common_roles_name = latest_roles_name & existing_roles_name
        return list(common_roles_name)

    @staticmethod
    def _get_invalid_roles_ids(existing_roles, common_roles_name):
        role_ids_to_delete = []
        for role in existing_roles:
            if role['name'] not in common_roles_name:
                role_ids_to_delete.append(role['id'])

        return role_ids_to_delete

    def _get_roles_to_add(self, common_roles_name):
        roles_to_add = []

        for role in self.roles:
            if role.role_name not in common_roles_name:
                roles_to_add.append(role)

        return roles_to_add

    def get_all_roles(self):
        roles = self.auth0.roles.list()
        return roles['roles']

    def add_roles(self, roles):
        for current_role in roles:
            role_param = {'name': current_role.role_name, 'description': current_role.role_description}
            self.auth0.roles.create(role_param)
        print('All roles added successfully.')

    def delete_roles(self, roles_ids_to_delete):
        for role_id in roles_ids_to_delete:
            self.auth0.roles.delete(role_id)
        print('All unneeded roles deleted.')

    def _get_role_permission_dict(self):
        role_permission_dict = {}
        for role in self.roles:
            role_permission_dict[role.role_name] = role.permissions
        return role_permission_dict


if __name__ == '__main__':
    auth0 = Auth0Util()
    auth0.update_all()
