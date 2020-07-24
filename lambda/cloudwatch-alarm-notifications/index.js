const { IncomingWebhook } = require('@slack/webhook');

exports.handler = async (event) => {
    const webhook = new IncomingWebhook(process.env.SLACK_WEBHOOK);

    await Promise.all(event.Records.map((record) => {
        const message = record.Sns.Message;
        console.log('Received message:', message);

        const {
            NewStateValue, AlarmName, Region,
            OldStateValue, StateChangeTime, NewStateReason
        } = JSON.parse(message);

        return webhook.send({
            text: `*${NewStateValue}*: "${AlarmName}" in ${Region}`
                + `\nState changed from ${OldStateValue} to ${NewStateValue} at ${StateChangeTime}`
                + `\n_Reason_: ${NewStateReason}`,
        });
    }));

    const numEvents = event.Records.length;

    const result = (`Successfully processed ${numEvents} record${numEvents === 1 ? '' : 's'}`);
    console.log(result);
    return result;
};
