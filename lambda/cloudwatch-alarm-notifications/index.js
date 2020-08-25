const { IncomingWebhook } = require('@slack/webhook');
const CloudWatchClient = require('./cloudwatchClient');
const AthenaClient = require('./athenaClient');

const QUERY_PROCESSING_TIME_IN_MILLISECONDS = 10000;

exports.handler = (event, context, callback) => {

    const message = event.Records[0].Sns.Message;
    console.log('Received message:', message);

    const {
        NewStateValue, AlarmName, Region,
        OldStateValue, StateChangeTime, NewStateReason
    } = JSON.parse(message);

    const alarmMessage = `*${NewStateValue}*: "${AlarmName}" in ${Region}`
        + `\nState changed from ${OldStateValue} to ${NewStateValue} at ${StateChangeTime}`
        + `\n_Reason_: ${NewStateReason}`;

    sendToSlack(alarmMessage)
        .then(() => {
            const name = AlarmName.toLowerCase();
            if (name.includes('healthcheck')) {
                const logClient = new CloudWatchClient(AlarmName);
                queryLogs(logClient, callback);
            } else if (name.includes('waf')) {
                const logClient = new AthenaClient(AlarmName);
                queryLogs(logClient, callback);
            } else {
                callback(null, 'Successfully processed event');
            }
        }).catch((err) => callback(err));
};

function queryLogs (logClient, callback) {
    const params = logClient.getParams();

    logClient.triggerQuery(params, (err, data) => {
        if (err) {
            callback(Error(err));
        } else {
            const params = logClient.getRetrievalParams(data);
            setTimeout(() => {
                logClient.retrieveResults(params, (err, data) => {
                    if (err) {
                        callback(Error(err));
                    }
                    const results = logClient.formatResults(data);

                    sendToSlack(results)
                        .then(() => callback(null, 'Successfully processed alarm and resulting logs'))
                        .catch((err) => callback(Error(err)));
                });
            }, QUERY_PROCESSING_TIME_IN_MILLISECONDS);
        }
    });
};

function sendToSlack(message) {
    const webhook = new IncomingWebhook(process.env.SLACK_WEBHOOK);
    return webhook.send({ text: message });
}
