#### Prerequisite
Make sure python3 has been installed:
```
python3 --version
```

If command not found, install it with following command:
```
sudo apt install python3
```

Make sure this machine has installed auth0 python sdk:
```
pip3 install auth0-python
```

#### Configuration
Before you can run this script, you need some configurations on both Auth0 and this script.
Firstly, make sure you have created a machine-to-machine application on Auth0. And select Auth0 management api in step2.
Select scopes including `read:resource_servers`, `update:resource_servers`, `read:roles`, `create_roles`, `delete_roles`, `update_roles`.
After that click authorize and the machine-to-machine application will be created.
Click into the application, remember the domain, client id, client secret, these are going to be used in following steps.


Create file `config/config.py`:
This file contains configurations about auth0 connection:
```
auth0Domain = 'Your auth0 domain'
auth0ClientId = 'Your machine-to-machine application client id'
auth0ClientSecret = 'Your machine-to-machine application client secret'
auth0ApiIdentifier = 'api identifier(The api audience which you want to manage)'
```

#### Run
Run this script:
```
python3 auth0_util.py
```  


#### Other
`config/roles.csv` file format:  
`role type,role name,role description,permission types`  

`config/permissions.csv` file format:
`permission type,permission type,permission description`