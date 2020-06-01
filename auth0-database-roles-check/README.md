# auth0-database-roles-check

## python related libraries

Make sure python3 has been installed:
```
python3 --version
```

If command not found, install it with following command:
```
sudo apt install python3
```

Make sure the machine has installed auth0 python sdk:
```
pip3 install auth0-python
```

Make sure the machine has installed pymysql:
```
pip3 install pymysql
```

Make sure the machine has installed PyYAML:
```
pip3 install PyYAML
```

## you need to create the auth0-database-${env}.yml under include folder and use ansible to encrypt the data 
### the description of each parameter in the file
```
database_host: Mysql connection Hostname
database_user: Mysql connection Username
database_password: Mysql connection Password
database: The database name
database_port: The database port
auth0_tenant_name：The Auth0 Domain
auth0_clientId_cli: The Auth0 Client ID
auth0_client_secret_cli: The Auth0 Client Secret
```

## Run
### use ansible to create the config
```
ansible-playbook --vault-password-file ~/.ansible/hrs-env.txt -e 'env=${env}' auth0-database-roles-check/ansible/create-auth0-database-config.yml
```
the '~/.ansible/hrs-env.txt' is the file where the password is stored
the encrypted passwords of dev and qa are in lastpass
for example with the pro environment:
```
ansible-playbook --vault-password-file ~/.ansible/hrs-env.txt -e 'env=pro' auth0-database-roles-check/ansible/create-auth0-database-config.yml
```
