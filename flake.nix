{
  description = "API for aviators";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
    nixos-generators = {
      url = "github:nix-community/nixos-generators";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
    nixos-generators,
  }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };

        jdk = pkgs.openjdk17_headless;

        # Build Dependencies.
        buildInputs = with pkgs; [
          jdk
          maven
        ];

        # Development environment. Includes additional tools not needed at build time.
        devInputs = with pkgs; buildInputs ++ [
          ktlint
        ];

        # CI Environment.
        ciInputs = with pkgs; [
          docker
          doctl
          kubectl
          kubernetes-helm
        ];

        # Environment variables passed into shell at creation.
        testEnvironment = ''
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

      in rec {
        packages = {
          # Using `nixos-generators`, nix can be used to build a wide variety of images.
          # This example generates a Digitalocean image although this application doesn't make use of it.
          # Instead, `skylunch` uses `nixos-infect` to build the infractructure after creation.
          k3s-host = nixos-generators.nixosGenerate {
            inherit system;
            modules = [ ./ci/server/k3s-host.nix ];
            format = "do";
          };
          # Nix derivation of `skylunch` application.
          skylunch =
            let
              # Maven repository
              repository = pkgs.stdenv.mkDerivation {
                name = "skylunch";
                src = ./.;
                inherit buildInputs;
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
                outputHash = "sha256-8v8ME+0nB71Zz5XDRjb5C2nT8tY3n+X+NpCs6XIZTn4=";
              };
            in
              pkgs.stdenv.mkDerivation rec {
                name = "skylunch";
                version = "0.1.6";
                src = ./.;
                nativeBuildInputs = [ pkgs.makeWrapper ] ++ buildInputs;
                installPhase = ''
                  mkdir -p $out/bin
                  mkdir -p $out/jar
                  cp ${repository}/com/skylunch/skylunch/${version}/${name}-${version}.jar $out/jar
                  makeWrapper ${jdk}/bin/java $out/bin/${name} \
                    --add-flags "-jar $out/jar/${name}-${version}.jar"
                '';
            };
          default = packages.skylunch;
        };
        apps = {
          skylunch = flake-utils.lib.mkApp { drv = packages.skylunch; };
          default = apps.skylunch;
        };
        devShells = with pkgs; rec {
          build = mkShell {
            name = "build-environment";
            inherit buildInputs;
            shellHook = testEnvironment;
          };
          ci = mkShell {
            name = "ci-tools";
            buildInputs = ciInputs;
          };
          dev = mkShell {
            name = "dev-environment";
            buildInputs = devInputs;
          };
          default = dev;
        };
      });
}
