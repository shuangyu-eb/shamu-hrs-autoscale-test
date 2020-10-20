const AWS = require('aws-sdk');

exports.handler = (event, context, callback) => {
  var stepfunctions = new AWS.StepFunctions();
  const { stateMachineArn } = process.env;
  const input = JSON.stringify({
    id: event.Records[0].body
  });
  var params = {
    stateMachineArn,
    input
  };

  stepfunctions.startExecution(params, (err, data) => {
    if (err) {
      return callback(err, 'execute failed.');
    }
    callback(null, data);
  });
};
