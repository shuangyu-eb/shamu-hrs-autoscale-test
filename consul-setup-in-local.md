## Consul

#### Install Consul
```
cd ~ 
wget https://releases.hashicorp.com/consul/1.4.2/consul_1.4.2_linux_amd64.zip
mkdir consul
unzip consul_1.4.2_linux_amd64.zip -d consul
sudo mv consul/consul /usr/bin/

rm -rf consul
rm consul_1.4.2_linux_amd64.zip
```

#### Set up in development

```
consul agent -dev -ui
```

Visit [http://localhost:8500](http://localhost:8500), then you'll see the web UI for consul.
