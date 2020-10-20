var https = require('https');

exports.handler = (event, context, callback) => {
  const { host, pathPrefix, lambdaToken } = process.env;
  const path = pathPrefix + event.id;

  const options = {
    host,
    path,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Tenant-Lambda-Token': lambdaToken
    }
  };

  const req = https.request(options, res => {
    res.setEncoding('utf8');
    const { statusCode } = res;
    if (statusCode !== 200 && statusCode !== 504) {
      return callback('Invoke failed, statusCode: ' + statusCode, 'Invoke failed.');
    }
  });

  const data = JSON.stringify({});
  req.write(data);
  req.end();
};
