#

1. To ensure data consistency between origin database and target multiple 
tenant database schemas, please stop your company service before executing this script.

2. Edit file *datasource.properties* in the folder **src/main/resources** 
to add database connection required information. Before editing, make sure you
have read the explanation for *datasource.properties* in the end of this README. 
    
3. Use command `mvn clean package` to generate script jar file: *company-database-migration-1.0.jar* 
in the folder **target**.

4. Go to folder **target** and use command `java -jar company-database-migration-1.0.jar` to execute this script.

5. It will retry five times on each tenant's creating failed. If exceeds the limit of retry times, the script will
continue to migrate next tenant. Failure log will be recorded in the file *error-log.properties* of the folder **execute-log**.
After all tenants migrated, you can view *error-log.properties* to see the problems and solve them. Usually, the problems
are caused by the network.

6. This script supports continuing execution from where it stopped last time. As a result, if you find the script stuck when 
executing, just stop and re-execute it.

## Here is the explanation for *datasource.properties*:
1. **No need** to add '/' to the end of `origin.jdbc-url`. The entire database url
is `${origin.jdbc-url}/${origin.schema}` and the character '/' will be added by script.  
In this demo, the final url of original database in script which used to create the
 database connection will be 'jdbc:mysql://original-database.demo.com/company'.

2. The value of `tenant.prefix` and `default.schema` **should not be changed unless**
you are migrating your local environment's data.   
Considering the prefix of document service's tenant schemas are also 'tenant_', 
and it may lead to some problems when you perform migration operation on company
service at local when all these schemas share the same database, I make it editable.

3. Don't forget to leave '/{schema}' to the end of `target.jdbc-url-template`. You don't need to 
specify the specific schema to the end of `target.jdbc-url-template` since we only use it to create
a connection to the target database to create multiply tenant schemas rather than connecting to any specific
tenant schema. 
      
```properties
# original datasource
origin.user=demo
origin.password=demo
origin.schema=company
origin.jdbc-url=jdbc:mysql://original-database.demo.com

# target datasource
target.user=demo
target.password=demo
target.jdbc-url-template=jdbc:mysql://target-database.demo.com/{schema}
tenant.prefix=tenant_
default.schema=company_default
```
