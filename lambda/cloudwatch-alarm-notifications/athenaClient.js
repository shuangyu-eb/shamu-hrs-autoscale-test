const AWS = require('aws-sdk');
const { getYesterday } = require('./util');

class AthenaClient {
    constructor(alarmName) {
        this.alarmName = alarmName;
        this.awsClient = this.createAwsClient();
    }

    createAwsClient() {
        return new AWS.Athena();
    }

    getAwsClient() {
        return this.awsClient || this.createAwsClient();
    }

    getParams() {
        const yesterday = getYesterday();

        const query = 'SELECT COUNT() as count, httprequest.httpmethod, httprequest.uri, nonterminatingmatchingrules'
            + '\nFROM waf_logs'
            + `\nWHERE cardinality(nonterminatingmatchingrules) > 0 AND timestamp > ${yesterday}`
            + '\nGROUP BY httprequest.uri, httprequest.httpmethod, nonterminatingmatchingrules'
            + '\nORDER BY count DESC'
            + '\nLIMIT 10';

        return {
            QueryString: query,
            WorkGroup: 'primary',
        };
    }

    triggerQuery(params, callback) {
        const awsClient = this.getAwsClient();
        awsClient.startQueryExecution(params, callback);
    }

    getRetrievalParams(data) {
        return { QueryExecutionId: data.QueryExecutionId };
    }

    retrieveResults(params, callback) {
        const awsClient = this.getAwsClient();
        awsClient.getQueryResults(params, callback);
    }

    formatResults(data) {
        let resultString = 'WAF logs from the past 24 hours...\n\n';

        const resultSet = data.ResultSet || {};
        const rows = resultSet.Rows || [];

        rows.forEach(row => {
            row.Data.forEach(dataPoint => {
                resultString += ` ${dataPoint.VarCharValue}`;
            });
            resultString += '\n';
        });
        return '```' + resultString + '```';
    }
}

module.exports = AthenaClient;
