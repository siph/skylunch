{
  description = "API for aviators";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in rec {
        packages = {
          skylunch = pkgs.stdenv.mkDerivation {
            name = "skylunch";
            src = ./.;
            buildInputs = with pkgs; [ jdk11 maven ];
            buildPhase = "mvn clean install -Dmaven.repo.local=$out -DskipTests=true";
            installPhase = ''
              find $out -type f \
              -name \*.lastUpdated -or \
              -name resolver-status.properties -or \
              -name _remote.repositories \
              -delete
            '';
            dontFixup = true;
            outputHashAlgo = "sha256";
            outputHashMode = "recursive";
            outputHash = "sha256-pZt5gBU3mggN9RecPPKTILyd1BPLJxP99RHDqB5iECI=";
          };
          default = packages.skylunch;
        };
        apps = {
          skylunch = flake-utils.lib.mkApp { drv = packages.skylunch; };
          default = apps.skylunch;
        };
        devShell = with pkgs;
          mkShell {
            buildInputs = [
              docker
              doctl
              jdk11
              kubectl
              maven
            ];
          };
      });
}
