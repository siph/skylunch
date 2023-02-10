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
            buildInputs = with pkgs; [ openjdk11_headless maven ];
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
            name = "build-environment";
            nativeBuildInputs = [
              openjdk17_headless
              maven
            ];
            shellHook = ''
              export SPRING_MAIN_ALLOWBEANDEFINITIONOVERRIDING=true
              export APPLICATION_AIRPORT_DAYSUNTILSTALE=0
              export APPLICATION_AIRPORT_API_APIKEY="k"
              export APPLICATION_AIRPORT_API_BASEURL="https://airports-by-api-ninjas.p.rapidapi.com/v1/airports"
              export APPLICATION_RESTAURANT_DAYSUNTILSTALE=0
              export APPLICATION_RESTAURANT_API_APIKEY="k"
              export APPLICATION_RESTAURANT_API_BASEURL="https://maps.googleapis.com/maps/api/place"
              export APPLICATION_REDIS_HOSTNAME="http://0.0.0.0"
              export APPLICATION_REDIS_PORT=6379
              export APPLICATION_REDIS_USERNAME="redis-user"
              export APPLICATION_REDIS_PASSWORD="redis-password"
              export APPLICATION_SECURITY_SECURITYHEADERKEY="key"
              export APPLICATION_SECURITY_SECURITYHEADERVALUE="value"
            '';
          };
      });
}
