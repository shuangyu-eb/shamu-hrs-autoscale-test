const AWS = require('aws-sdk');
const { getToday, getYesterday } = require('./util');

class CloudWatchClient {
    constructor(alarmName) {
        this.alarmName = alarmName;
        this.awsClient = this.createAwsClient();
    }

    createAwsClient() {
        return new AWS.CloudWatchLogs();
    }

    getAwsClient() {
        return this.awsClient || this.createAwsClient();
    }

    getParams() {
        const stackPrefix = process.env.STACK_PREFIX;
        const env = process.env.ENV;

        const logGroups = [
            `/aws/elasticbeanstalk/${stackPrefix}-${env}/var/log/web-1.log`,
            `/ecs/${stackPrefix}-${env}-company-service`,
            `/ecs/${stackPrefix}-${env}-document-service`,
            `/ecs/${stackPrefix}-${env}-logging-service`,
            `/ecs/${stackPrefix}-${env}-search-service`,
        ];
        const query = 'fields @timestamp, @message, service | filter level="ERROR" | sort @timestamp desc | limit 10';

        return {
            logGroupNames: logGroups,
            queryString: query,
            startTime: getYesterday(),
            endTime: getToday(),
        };
    }

    triggerQuery(params, callback) {
        const awsClient = this.getAwsClient();
        awsClient.startQuery(params, callback);
    }

    getRetrievalParams(data) {
        return { queryId: data.queryId };
    }

    retrieveResults(params, callback) {
        const awsClient = this.getAwsClient();
        awsClient.getQueryResults(params, callback);
    }

    formatResults(data) {
        const { results } = data;
        let resultString = '';

        results.forEach(log => {
            const [timestamp, message, service] = log;
            resultString += timestamp.value;
            resultString += ` | ${service.value}-service`;
            resultString += '\n';

            const msgObj = JSON.parse(message.value);
            const msg = msgObj.thrown ? msgObj.thrown.message : msgObj.message;
            resultString += msg;
            resultString += '\n';
        });

        return '```' + resultString + '```';
    }
}

module.exports = CloudWatchClient;
