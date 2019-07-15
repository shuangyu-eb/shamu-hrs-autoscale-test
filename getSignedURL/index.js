const AWS = require('aws-sdk');
const s3 = new AWS.S3();

exports.handler = async (event) => {
  return await getUploadURL(event);
};

const getUploadURL = async (document) => {
  const { url, ...data } = document;

  const key = url + '.pdf';
  const s3Params = {
    Bucket: 'shamu-hrs-qa-document',
    Key: key,
    Metadata: data,
    ContentType: 'application/pdf',
    ContentDisposition: 'inline',
    ACL: 'public-read',
  };
  return new Promise((resolve, reject) => {
    let uploadURL = s3.getSignedUrl('putObject', s3Params);
    resolve({
      uploadUrl: uploadURL,
      filePath: key,
    });
  });
};
