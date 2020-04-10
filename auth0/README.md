This subdirectory is used to keep track of changes to auth0 configuration changes for the Shamu HRS. 
The files in this repo can also be used to deploy new auth0 configurations through Ansible and 
the auth0-deploy-cli tool. For more information about the CLI tool, see https://auth0.com/docs/extensions/deploy-cli.

### Naming convention for variables file ###

The naming convention we are using for each variable file which is specific to a particular deploy is:

** Keep in mind that the values of stack_prefix and env should follow the convention as they are vital running the ansible script.

```$xslt
{{ stack_prefix }}-{{ env }}
```

The `{{ stack_prefix }}` consists of two parts

```$xslt
simplyhired-hrs-{{ deploying entity }}
```


The `{{ deploying entity }}` is to identify who carried out the deploys. This enables us to identify the owner of a deploy and accordingly make changes to the deploys if required, possible values are:
indeed
eastbay
The `{{ env }}` is used to define the type of deploy for that instance, possible values are
dev
qa
staging
prod

### Running the ansible playbook ###

Running the ansible playbook is required to generate the `auth0_config.json` file for the auth0 CLI tool. 
This allows the tool to import configuration changes using the same files for all environments, where 
environment variables are replaced with mapping from the `auth0_config.json` as necessary. To run the playbook,
navigate to the appropriate directory by running `cd auto-deploy/ansible` from the root level. Then execute the following command:

```$xslt
ansible-playbook --vault-password-file ~/.ansible/hrs-{ENV_NAME}.txt -e "stack_prefix={{ stack_prefix }} env={{ env }}" ansible/build-auth0-config.yml
```

This should create a `auth0_config.json` file in the current directory. We will then use this file 
to deploy our new configuration to auth0.

### Keyword mappings in auth0

To allow for certain elements of the auth0 configuration to be easily interchangable for different environments, 
this repo uses the keyword mappings feature of the auth0 CLI tool. For more information about keyword mappings, see 
https://auth0.com/docs/extensions/deploy-cli/references/environment-variables-keyword-mappings.

These keyword mappings allow for environment variables to be created within auth0 templates using the `#` symbol, i.e. 
``##your_key_here##``.

These variables are then mapped to corresponding values in `auth0_config.json`.
For example, if `auth0_config.json` included the following...
```$xslt
"AUTH0_KEYWORD_REPLACE_MAPPINGS": {
    "AUTH0_TENANT_NAME": "account.simplyhired-staging.com"
 }
```
...it would replace all instances of `##AUTH0_TENANT_NAME##` with the value `account.simplyhired-staging.com`.

### Deploying to auth0

Updated configurations can be deployed to auth0 using the `import` command of the auth0 CLI tool. 
To run this script, execute the following command from the same directory (`auto-deploy/ansible`):

```$xslt
a0deploy import -c auth0_config.json -i ../../auth0
```

Where `../../auth0` is the relative path to the directory for all of the auth0 files (templated to environment variables).
