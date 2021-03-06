console.log('Loading function');

const aws = require('aws-sdk');
const mysql = require('mysql');
const s3 = new aws.S3();

// If 'client' variable doesn't exist
if (typeof client === 'undefined') {
  // Connect to the MySQL database
  console.log('Connect to the MySQL database');
  var client = mysql.createConnection({
    host: process.env.RDS_HOSTNAME,
    user: process.env.RDS_USERNAME,
    password: process.env.RDS_PASSWORD,
    database: process.env.RDS_DATABASE,
    connectTimeout: 30000,
  });

  client.connect();
}

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
    let {
      title = null,
      'user-id': user_id,
      'company-id': company_id,
      'folder-id': folder_id = null,
      'signature-template-id': signature_template_id = null,
      'size': size,
    } = Metadata;

    user_id = parseInt(user_id);
    company_id = parseInt(company_id) || null;
    folder_id = parseInt(folder_id) || null;
    signature_template_id = parseInt(signature_template_id) || null;
    size = parseInt(size) || null;

    const sql = `INSERT INTO documents(title, url, user_id, company_id ,folder_id, signature_template_id, size, created_at) VALUES ('${title}','${key}',${user_id},${company_id},${folder_id},${signature_template_id},${size},now());`;

    client.query(sql, (err, res) => {
      if (err) {
        throw err;
      }
    });

  } catch (err) {
    console.log(err);
    const message = `Error getting object ${key} from bucket ${bucket}. Make sure they exist and your bucket is in the same region as this function.`;
    console.log(message);
    throw new Error(message);
  }
};
