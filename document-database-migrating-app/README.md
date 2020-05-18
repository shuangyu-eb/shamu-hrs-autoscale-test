# migrating app

Use maven to build this project.

Edit the `database.propertis` of the resource dir.

Before you build the package this app, please edit the `database.propertis` of the resource dir first.

Build an artifact, `mvn clean package`

Run the command `java -jar {actifact_name}`.

The app would dump the document database to a SQL zip, put the file in the same dictionary as this jar.

If the app throws an error, the error is always network error, please just try again but don't delete the dump dir and execute-log dir, the app would migrate the database continue at the position of the last time.

If you want to run the app again completely, please delete the dump dir and log dir before you run the app, and delete all databases that create by this app.
