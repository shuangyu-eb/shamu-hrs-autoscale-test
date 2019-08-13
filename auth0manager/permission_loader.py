import csv
from role import Role
from permission import Permission


class PermissionLoader:
    def __init__(self):
        self.permissions = self._load_permission()
        self.roles = self._load_role()

    def _load_role(self):
        roles = []
        with open('config/roles.csv', newline='') as csvFile:
            role_reader = csv.reader(csvFile)
            for row in role_reader:
                role_permissions = []
                permission_types = row[3].split(':')
                for permission in self.permissions:
                    if permission.permission_type in permission_types:
                        role_permissions.append(permission)
                current_role = Role(row[1], row[2], permission_types, role_permissions)
                roles.append(current_role)
        return roles

    @staticmethod
    def _load_permission():
        permissions = []
        with open('config/permissions.csv', newline='') as csvFile:
            permission_reader = csv.reader(csvFile)
            for row in permission_reader:
                current_permission = Permission(row[1], row[2], row[0])
                permissions.append(current_permission)
        return permissions


if __name__ == '__main__':
    permissionLoader = PermissionLoader()
