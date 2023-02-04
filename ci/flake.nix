{
  description = "k3s images";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
    nixos-generators = {
      url = "github:nix-community/nixos-generators";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, nixos-generators, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in rec {
        packages = {
          # Using `nixos-generators`, nix can be used to build a wide variety of images.
          # This example generates a Digitalocean image although this application doesn't make use of it.
          # Instead, `skylunch` uses `nixos-infect` to build the infractructure after creation.
          k3s-host = nixos-generators.nixosGenerate {
            inherit system;
            modules = [ ./server/k3s-host.nix ];
            format = "do";
          };
        };
        devShell = with pkgs;
          mkShell {
            buildInputs = [
              docker
              doctl
              kubectl
              kubernetes-helm
            ];
          };
      });
}
