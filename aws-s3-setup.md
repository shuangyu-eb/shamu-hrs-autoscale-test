## S3 config

## Bucket

The Bucket can be seen as the top-level directory.

### Create a Bucket

1. Sign in to the AWS Management Console and open the Amazon S3 console at [https://console.aws.amazon.com/s3/](https://console.aws.amazon.com/s3/).

2. Choose **Create bucket**.

3. In step 1 **Name and region**

- In the `Bucket name` field, type a unique DNS-compliant name for your new bucket. (The example screen shot uses the bucket name admin-created. You cannot use this name because S3 bucket names must be unique.) Create your own bucket name using the follow naming guidelines:

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- The name must be unique across all existing bucket names in Amazon S3.\
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- After you create the bucket you cannot change the name, so choose wisely.\
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Choose a bucket name that reflects the objects in the bucket because the bucket name is visible in the URL that points to the objects that you're going to put in your bucket.

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For information about naming buckets, see Rules for Bucket Naming in the Amazon Simple Storage Service Developer Guide.


- For `Region`, choose US West (Oregon) as the region where you want the bucket to reside. \

5. Click **Next**
6. Click **Next**, and now you are in step 3 **Set permissions**
- For `Manage public access control lists (ACLs) for this bucket`\
  Uncheck the `Block new public ACLs and uploading public objects (Recommended)` and
  `Remove public access granted through public ACLs (Recommended) `

- For `Manage public bucket policies for this bucket`\
Uncheck `Block new public bucket policies (Recommended)` and
`Block public and cross-account access if bucket has public policies (Recommended)`

![Image](images/s3-permission.png)

7. Click **Next**

8. Choose **Create bucket**.


**Note**:
For more information, see [aws document](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html).


### website hosting config for Bucket

To enable website hosting for an Amazon S3 bucket

1. Sign in to the AWS Management Console and open the Amazon S3 console at https://console.aws.amazon.com/s3/.

2. In the list, choose the bucket that you want to use for your hosted website.

3. Choose the **Properties** tab.

4. Choose **Static website hosting**, and then choose **Use this bucket to host a website**.

- For `Index document` and `Error document`, please input `index.html`

5. Click `Save`.

![Image](images/s3-hosting.png)

**Note**

If you want to use CloudFront to serve HTTPS requests for Amazon S3 bucket, please read
https://aws.amazon.com/premiumsupport/knowledge-center/cloudfront-https-requests-s3/.

https://gist.github.com/bradwestfall/b5b0e450015dbc9b4e56e5f398df48ff



For more information:

1. https://docs.aws.amazon.com/AmazonS3/latest/user-guide/static-website-hosting.html

2. https://medium.com/@sbuckpesch/setup-aws-s3-static-website-hosting-using-ssl-acm-34d41d32e394



### Adding a Bucket Policy That Makes Your Bucket Content Publicly Available

1. In the **Properties** pane for the bucket, choose **Permissions**.

2. Choose **Add Bucket Policy**.

3. To host a website, your bucket must have public read access. It is intentional that everyone in the world will have read access to this bucket. Copy the following bucket policy, and then paste it in the Bucket Policy Editor.
```
{
   "Version":"2012-10-17",
   "Statement":[{
 	"Sid":"PublicReadForGetBucketObjects",
         "Effect":"Allow",
 	  "Principal": "*",
       "Action":["s3:GetObject"],
       "Resource":["arn:aws:s3:::example-bucket/*"
       ]
     }
   ]
 }
 ```
4. In the policy, replace `example-bucket` with the name of your bucket.

5. Choose **Save**.

For more information:

https://docs.aws.amazon.com/AmazonS3/latest/dev/HostingWebsiteOnS3Setup.html


### Permissions config for Bucket

1. Click your bucket

2. Select the `Permissions`

3. Click `Public access settings` -> `Edit`, uncheck all boxes

4. Click `CORS Configuration`, and edit.

The `CORS Configuration` is configure what `URLs` can access the Bucket.
It can have more than one `<CORSRule></CORSRule>`, and the `URL` is between `<AllowedOrigin>` and `</AllowedOrigin>`.

Example configuration:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
<CORSRule>
    <AllowedOrigin>https://ats.interviewed-staging.com</AllowedOrigin>
    <AllowedMethod>GET</AllowedMethod>
    <MaxAgeSeconds>3000</MaxAgeSeconds>
    <AllowedHeader>Authorization</AllowedHeader>
</CORSRule>
</CORSConfiguration>
```

if you won't do this, for the static files are host in aws s3, the google fonts on web page won't work because of an CORS policy.

5. Click `Save`.

## `Access Key ID` and `Secret Access Key`
`Access Key ID` and `Secret Access Key` is used to access the AWS S3 to operate the file.

How to get  `Access Key ID` and `Secret Access Key`:
1. Open the IAM console.
2. From the navigation menu, click `Users`.
3. Select your IAM user name.
4. Click User Actions, and then click `Manage Access Keys`.
5. Click `Create Access Key`.
6. Your keys will look something like this: \
Access key ID example: AKIAIOSFODNN7EXAMPLE \
Secret access key example: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
7. Click `Download Credentials`, and store the keys in a secure location.
