# Environment setup

This doc contains the necessary steps for setup a separate running environment for the project.

The guide use services (ECS, ECR, S3, RDS, ALB, ElasticBeanstalk) on aws, trying to get familiar with these services before diving into it.

You need to do some changes on the configuration & deployment if you want to run it in other environments.


## Frontend

We use the Amazon Simple Storage Service (Amazon S3) to store the files of web UI, and host a static website on it.

To learn more about it, please read [the file **aws-s3-setup.md** about Amazon S3](aws-s3-setup.md)


**To deploy frontend to S3 Bucket**

The following packages are being required if you want to deploy the code to an environment.

1. Install elastic beanstalk cli tool, here's the [documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3.html)
2. Install yarn, here's the [documentation](https://yarnpkg.com/en/docs/install#mac-stable).

3. Create the config file for deploying
```
cp config/deploy-example.env config/deploy.env
```
4. Edit the config file `config/deploy.env`
```
ENV=${ENV}
BUCKET_NAME=${BUCKET_NAME}
```

replace `${ENV}` to your environment, and please ensure the config file of your environment exist in the folder `config/`. e.g. your environment is `qa`, and the `qa.env` must exist in `config/`.
replace `${BUCKET_NAME}` to your AWS A3 Bucket name.

#### Deploy

Run `bin/deploy` in project root directory to deploy the gateway to S3 bucket.

## Backend

There are three parts in the backend: gateway, Consul cluster and other services.
The gateway is deployed in ElasticBeanstalk, and the other services are deployed into ECS cluster as microservices.


### Gateway

#### Create a project on Elastic Beanstalk

Choose the **services** -> **Elastic Beanstalk**, in aws console. Create an application, then create an environment under this application.

Here's the configuration needed for new environment.

* Select **Web server environment** in **Select environment tier**
* Choose **Java** in Preconfigured platform
* Use **Sample application** for now

Put in all the necessary parameters in the step except above, then create environment. The environment will be ready after a few minutes.

#### Deployment setup

##### Installation

The following packages are being required if you want to deploy the code to an environment.

  - Install elastic beanstalk cli tool, here's the [documentation](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3.html)
  - Install Java openjdk-8 depending on the system. If you're on mac, you can take a look at [this](https://apple.stackexchange.com/questions/334384/how-can-i-install-java-openjdk-8-on-high-sierra).
  - Install yarn, here's the [documentation](https://yarnpkg.com/en/docs/install#mac-stable).
  - Install `zip` cmd in your system.
  - Make sure `pip` is installed to your system, you need to run `pip install pyyaml`.
  - Run `eb init` in project root directory, by following the hint, a config file will be generated like the following in the path `PROJECT_DIR/.elasticbeanstalk/config.yml`:

```
branch-defaults:
	master:
		environment: bass-ats-qa
environment-defaults:
	bass-ats-qa:
		branch: null
		repository: null
global:
	application_name: bass-ats
	default_ec2_keyname: eastbay-aws-eb
	default_platform: arn:aws:elasticbeanstalk:ap-northeast-1::platform/Java 8 running
	    on 64bit Amazon Linux/2.6.8
	default_region: ap-northeast-1
	include_git_submodules: true
	instance_profile: null
	platform_name: null
	platform_version: null
	sc: git
	workspace_type: Application
deploy:
  artifact: ./target/app/app.zip
```

  - Be sure to manually add the `deploy` option in the `config.yml` since this couldn't be auto generated.

##### Deployment

Run `bin/deploy {env}` in project root directory to deploy the gateway to ElasticBeanstalk, e.g. `bin/deploy qa`.


### Consul Cluster
We use the Quick Start of consul for aws to launch the Consul Cluster.
Before we launch it, we need some preparations.
If we are not bang-on with our preparations then we could have problems about the consul cluster, for example, the Consul node can't access the network.

#### **Preparations**
To launch the consul Cluster successfully, we should create some private subnets with NAT Gateway.

**private subnet**: associated with NAT Gateway or NAT Instance, Instances in a private subnet do not have public IP addresses.

**public subnet**: has an Internet gateway (IGW). Instances in the public subnet need public IP addresses to access the Internet.

Step:

1. Create a NAT Gateway in a public subnet.
2. Create 1~3 subnet in the VPC.
3. Create a new Router Table, and edit it
    - Select the route table and choose **Routes**, **Edit**.

    - Choose Add another route. For **Destination**, type 0.0.0.0/0. For **Target**, select the ID of your NAT gateway.

    - Choose **Save**.
4. Make Router Table associated with the newly created subnets
    - Select the route table and choose **Subnet Associations**, **Edit subnet associations**.

    - Choose the newly created subnets.

    - Choose **Save**.

**Important**
   1. All of Consul node should be in the private subnet,

   2. NAT Gateway should be in the public subnet.


#### **launch the Consul Cluster**

We use [Quick Start](https://aws.amazon.com/quickstart/architecture/consul/) of consul to set up a flexible, scalable AWS Cloud environment, and launch HashiCorp Consul automatically.


1. Choose one of the following options to deploy the AWS CloudFormation template into  AWS account.

- [Deploy into a new VPC](https://fwd.aws/qmxr8)
- [Deploy into an existing VPC](https://fwd.aws/Q5Rqv)

We choose **Deploy into an existing VPC**.

The templates are launched in the US West (Oregon) region by default, and we should select the Region that we want to deploy into.


2. For **Specify an Amazon S3 template URL**, type the path of the template.

    **Important**:

    Please choose `View/Edit template in Designer` to view the default template.\
     If there is no  ubuntu image in the `Mappings": {  "AWSAMIRegionMap": {  .......` in the region that you want to launch cluster in,
      please edit the template(add a ubuntu image for the region that you want to launch cluster in), save it to a new template,
       finally launch the Consul Cluster with the new template.

    **The original template**:\
    ![Image](images/templete.origin.png?raw=true)

    **The edited template**:\
    ![Image](images/template.png?raw=true)
3. Choose **Next**.

4. For **ConsulClientNodes**, please type 0.

    The Consul client instances are used to run the service, but we use ECS Instances to run services. We needn't to launch Consul client together with Consul cluster.

5. Select the **PrivateSubnet1ID**, **PrivateSubnet2ID** and **PrivateSubnet3ID** which you create in **`Preparations`**

6. Complete the other required field, and Create the Cluster.


### ECR & ECS

**ECR**: Amazon Elastic Container Registry (Amazon ECR) is a managed AWS Docker registry service
that is secure, scalable, and reliable.

Note: Every service has a repository.

**ECS**: Amazon Elastic Container Service (Amazon ECS) is a highly scalable, fast, container
management service that makes it easy to run, stop, and manage Docker containers on a cluster.

Note: There are two ECS clusters: one is for gateway, the other is for other services.

#### **To create a cluster**

1. Open the Amazon ECS console at https://console.aws.amazon.com/ecs/.

2. From the navigation bar, select the Region to use.

3. In the navigation pane, choose **Clusters**.

4. On the Clusters page, choose **Create Cluster**.

5. For **Select cluster compatibility**, choose **EC2 Linux + Networking** and then choose **Next Step**.

This choice takes you through the choices to launch a cluster of tasks using the EC2 launch type using Linux containers.
The EC2 launch type allows you to run your containerized applications on a cluster of Amazon EC2 instances that you manage.

6. For **Cluster name**, enter a name for your cluster. Up to 255 letters (uppercase and lowercase), numbers, hyphens, and underscores are allowed.

7. For **Subnets**, please select the private subnets.

8. Complete all fields for configuration, and create cluster.

More Information: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create_cluster.html


#### **Make ECS Instances join in Consul cluster**

In order to register the gateway service into Consul cluster, we need run a consul client and make it join in the Consul cluster.


- install consul

```
ssh -i ${the path of key-pair} ec2-user@${puiblic-ip}
sudo yum install wget
sudo yum install unzip

wget https://releases.hashicorp.com/consul/1.4.2/consul_1.4.2_linux_amd64.zip
mkdir consul
unzip consul_1.4.2_linux_amd64.zip -d consul
sudo mv consul/consul /usr/bin/
rm consul_1.4.2_linux_amd64.zip
rm -R consul/
```

- run Consul Client and make it join in the consul Cluster

CLUSTER_NAME: the name of consul cluster
REGION: the region that cluster is in
ACCESS_KEY_ID: aws access key id
SECRET_ACCESS_KEY: aws secret access key
BIND_IP: the private ip of the host
```
nohup consul agent \
 -retry-join="provider=aws tag_key=aws:cloudformation:stack-name tag_value=${CLUSTER_NAME} region=${REGION} access_key_id=${ACCESS_KEY_ID} secret_access_key=${SECRET_ACCESS_KEY}" \
  -bind=${BIND_IP} \
  -data-dir=~/data \
  -ui > log.txt &
```


#### Create ECR repository

Before you can push your Docker images to Amazon ECR,
you must create a repository to store them in. You can create Amazon ECR repositories with the AWS Management Console, or with the AWS CLI and AWS SDKs.

You should create a repository for each Service and gateway.

**To create a repository**

1. Open the Amazon ECR console at https://console.aws.amazon.com/ecr/repositories.

2. From the navigation bar, choose the region to create your repository in.

3. In the navigation pane, choose **Repositories**.

4. On the **Repositories page**, choose **Create repository**.

5. For Repository configuration, enter a unique name for your repository and choose **Create repository**.

6. Select the repository you created and choose **View push commands** to view the steps to push an image to your new repository.

**Important**
Before you push an image to a repository, you must run the following command to package the code.

```
mvn clean package -Dmaven.test.skip=true -e
```

More information: https://docs.aws.amazon.com/AmazonECR/latest/userguide/repository-create.html

#### Task Definitions

Before you can run Docker containers on Amazon ECS, you must create a task definition.
 You can define multiple containers and data volumes in a task definition.

**To create a new task definition**

1. Open the Amazon ECS console at https://console.aws.amazon.com/ecs/.

2. In the navigation pane, choose Task Definitions, Create new Task Definition.

3. On the Select compatibilities page, select the **EC2 launch type** that your task should use and choose Next step.

4. For **Task Definition Name**, type a name for your task definition. Up to 255 letters (uppercase and lowercase), numbers, hyphens, and underscores are allowed.

5. For **Task Role**, choose an IAM role that provides permissions for containers in your task to make calls to AWS APIs on your behalf.

6. For **Network Mode**, choose **Host**.

7. For Task execution role, choose an IAM role that provides permissions for containers in your task to make calls to AWS APIs on your behalf.

8. Choose **Add container**.

   For **Image**, type the image url in ECR.

   For **Port mapping**, type the port which config in you code.

   **Important**:  The following ports has been used by Consul. please not to use them.
   ```
      8500 8600 8501 8502 8503
   ```

    For **ENVIRONMENT** -> **Command**, please type the following command to specify the config file
    ENV: config file, if ENV=qa, it specifies the file `application-qa.yml`
    ```
      --spring.profiles.active=${ENV}
    ```
   Choose **Add** to add your container to the task definition.

9. Choose **Create**.

For more information: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-task-definition.html

#### Run services in ECS cluster
When you create an Amazon ECS service, you specify the basic parameters that define what makes up your service and how it should behave. These parameters create a service definition.
<div>
    <p><strong>Steps</strong></p>
    <ul>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/basic-service-params.html">Step 1: Configuring Basic Service
             Parameters</a></li>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-configure-network.html">Step 2: Configure a Network</a></li>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-create-loadbalancer.html">Step 3: Configuring Your Service to Use a Load
             Balancer</a></li>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-configure-servicediscovery.html">Step 4: (Optional) Configuring Your
             Service to Use Service Discovery</a></li>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-configure-auto-scaling.html">Step 5: (Optional) Configuring Your
             Service to Use Service Auto Scaling</a></li>
       <li><a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-service-review.html">Step 6: Review and Create Your Service</a></li>
    </ul>
 </div>

**Note**:
If there is only 1 instances in your ECS cluster, please use **DAEMON** as **Service type**,
and if not, the service can't update because the port is been used.

`Suggestion`:
Launch more than 1 instances in your cluster, and choose **REPLICA** as **Service type** and **Number of tasks** is less than numbers of instances, so we can update the service dynamically.

For more information: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-service.html
