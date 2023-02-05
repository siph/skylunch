# Skylunch Infrastructure and Deployment

This package contains all the files needed to build the kubernetes infrastructure and deploy
`skylunch` to that infrastructure. The kubernetes cluster is built using NixOS and k3s and
uses Digitalocean as a cloud provider. These three things come together to provide a
full-featured, production ready kubernetes cluster with full TLS, data-persistence,
loadbalancer, and downtime-free updates all on a *single* machine while retaining the ability to
scale.

## Kubernetes

### K3s

[K3s](https://github.com/k3s-io/k3s) is a lightweight Kubernetes distribution and is used in this
configuration because of its extreme ease of use when combined with NixOS. By default, k3s
provides some really useful features like a loadbalancer, CoreDNS, and a metrics-server. Using
NixOS, a simple k3s instance can be built using only a few lines of declarative configuration.

### TLS

`skylunch` uses `cert-manager` to handle to TLS certificates for the application instances.
This configuration uses `ACME` as a protocol and `lets-encrypt` as a provider. Included in this
configuration are two different `clusterIssuers`; One for production and one for staging.
Both issuers share the same secret key reference so only one can be used at a time.

### Skylunch

This configuration utilizes a `traefik` loadbalancer and `cert-manager` to provide https
encryption and load-based routing. The deployment is simple with two replicas for downtime-free
updates. A
[`redis-stack`](https://redis.io/docs/stack/) [image](https://hub.docker.com/r/redis/redis-stack)
is available and can be configured in this deployment but this configuration uses the redis
cloud service to simplify the deployment and data management.

### Secrets

Secrets can be placed in [`secrets.yaml`](kubernetes/skylunch/secrets.yaml) and must be base64 encoded.

```shell
echo secret | base64
```

## NixOS

### Configuration

NixOS systems are configured declaratively using the functional language `nix`. A
very simple configuration for a single node k3s instance looks like this:

```nix
{
  networking.firewall.allowedTCPPorts = [ 6443 ];
  services.k3s.enable = true;
  services.k3s.role = "server";
  environment.systemPackages = [ pkgs.k3s ];
}
```

### Building the Configuration

There are multiple options for deriving a NixOS system from a configuration.

`nixos-generators` can be used to generate a wide variety of image formats including ISOs,
docker images, and digitalocean images. While an example of this does exist in this configuration,
`skylunch` doesn't utilize this approach and instead chooses to use `nixos-infect` to build the
infrastructure after creation. 

[`nixos-infect`](https://github.com/elitak/nixos-infect) is a tool that can convert a non-NixOS
install into a NixOS system. A `cloud-config` exists in
[`skylunch-userdata.yaml`](server/skylunch-userdata.yaml) where the infect
script and system configuration are applied to the droplet at creation time either with `doctl`
or the web interface. This will turn a default Ubuntu-22.04 server into the NixOS configuration
which is then ready to start receiving kubernetes objects.


### Nix Development Environment

In addition to managing the application environment, nix is also used to manage the devops
environment. `flake.nix` holds a devshell for the needed dependencies
(`docker`, `doctl`, `kubectl`, etc) to initialize and manage the infrastructure and deployment.
```shell
nix develop
```
