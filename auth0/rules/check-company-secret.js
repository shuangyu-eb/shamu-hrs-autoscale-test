function (user, context, callback) {
  const mysql = require('mysql');
  const bcrypt = require('bcrypt');

  function addToken(payload) {
    const namespace = configuration['custom-namespace'];
    context.idToken[`${namespace}companySecret`] = payload;
    context.accessToken[`${namespace}companySecret`] = payload;
  }

  addToken('GET_COMPANY_SECRET_FAILED');

  const secretConnection = mysql.createConnection({
    host: configuration.secret_database_url,
    user: configuration.secret_database_user,
    password: configuration.secret_database_password,
    database: configuration.secret_database_name
  });

  const companyServiceConnection = mysql.createConnection({
    host: configuration.company_database_url,
    user: configuration.company_database_user,
    password: configuration.company_database_password,
    database: configuration.company_database_name,
  });

  secretConnection.connect();
  companyServiceConnection.connect();

  function closeConnections() {
    secretConnection.end();
    companyServiceConnection.end();
  }

  const get_company_id = 'SELECT hex(company_id) FROM users WHERE id = unhex(?)';

  const user_id = user.app_metadata.id;

  companyServiceConnection.query(get_company_id, [user_id], function(err, results) {
    if (err) {
      closeConnections();
      return callback(err);
    }

    if (results.length === 0) {
      closeConnections();
      return callback(null, user, context);
    }

    const { company_id } = results[0];

    const get_company_hash = 'SELECT secret_hash FROM company_secrets WHERE company_id = ?';
    secretConnection.query(get_company_hash, [company_id], function(err, results) {
      if (err) {
        closeConnections();
        return callback(err);
      }

      if (results.length === 0) {
        const add_company_secret_hash = 'INSERT INTO company_secrets (company_id, secret_hash) VALUES (?, ?)';
        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync(`${company_id}`, salt);
        secretConnection.query(add_company_secret_hash, [company_id, hash], function(err, results){
          if(err) {
            closeConnections();
            return callback(err);
          }

          addToken(hash);
          closeConnections();
          return callback(null, user, context);
        });
      } else{
        addToken(results[0].secret_hash);
        closeConnections();
        return callback(null, user, context);
      }
    });
  });

}
