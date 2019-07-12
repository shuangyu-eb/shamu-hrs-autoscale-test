console.log('Loading function');

const aws = require('aws-sdk');
const mysql = require('mysql');
const s3 = new aws.S3();

const con = mysql.createConnection({
  host: process.env.RDS_HOSTNAME,
  user: process.env.RDS_USERNAME,
  password: process.env.RDS_PASSWORD,
  database: process.env.RDS_DATABASE,
  connectTimeout: 30000,
});

exports.handler = async (event, context) => {

  // Get the object from the event and show its content type
  const bucket = event.Records[0].s3.bucket.name;
  const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
  const params = {
    Bucket: bucket,
    Key: key,
  };
  try {
    const { Metadata } = await s3.getObject(params).promise();
    console.log('Metadata:', Metadata);
    const { title = null, 'user-id': user_id, 'folder-id': folder_id = null, 'signature-template-id': signature_template_id = null } = Metadata;

    const sql = `INSERT INTO documents(title, url, user_id, folder_id, signature_template_id) VALUES (${title},${key},${user_id},${folder_id},${signature_template_id});`;
    con.query(sql, (err, res) => {
      if (err) {
        throw err;
      }
      console.log(res);
    });
  } catch (err) {
    console.log(err);
    const message = `Error getting object ${key} from bucket ${bucket}. Make sure they exist and your bucket is in the same region as this function.`;
    console.log(message);
    throw new Error(message);
  }
};
