const querystring = require('querystring');
const sendgrid = require('@sendgrid/mail');
const welcomeEmail = require('./welcomeEmail');
const { GoogleSpreadsheet } = require('google-spreadsheet');
const googleCredentials = require('./credentials.json');
const moment = require('moment-timezone');
const { IncomingWebhook } = require('@slack/webhook')

exports.handler = async (event) => {
    const decoded = Buffer.from(event.body, 'base64').toString('ascii');
    const parsed = querystring.parse(decoded);

    const { fullName, workEmail, companyName } = parsed;

    const doc = new GoogleSpreadsheet(process.env.GOOGLE_SPREADSHEET_ID);

    const sheetsRequest = doc.useServiceAccountAuth(googleCredentials)
        .then(() => doc.loadInfo())
        .then(() => {
            const sheet = doc.sheetsByIndex[0];
            const timestamp = moment().tz('America/Chicago').format('M/D/YYYY hh:mm:ss');
            return sheet.addRow([
                timestamp,
                fullName,
                workEmail,
                companyName
            ], {insert: true});
        });

    sendgrid.setApiKey(process.env.SENDGRID_API_KEY);
    const sendgridRequest = sendgrid.send({
        to: workEmail,
        from: 'SimplyHired <no-reply@hr.simplyhired.com>',
        subject: 'Thanks for your interest in the SimplyHired HRIS!',
        html: welcomeEmail,
    });

    const webhook = new IncomingWebhook(process.env.SLACK_WEBHOOK);
    const slackRequest = webhook.send({
        text: `*New Lead!*\n\nName: ${fullName}\nEmail: ${workEmail}\nCompany Name: ${companyName}`
    });

    await Promise.all([sheetsRequest, sendgridRequest, slackRequest]);

    const response = {
        statusCode: 200,
        body: JSON.stringify('Successful form submission for the SimplyHired HRIS!'),
    };
    return response;
};

