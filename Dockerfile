FROM nixos/nix:latest
ENV HOME=/home/skylunch/app/
WORKDIR $HOME
COPY . .
ENV NIX_CONFIG='extra-experimental-features = nix-command flakes'
RUN nix develop .#build --command mvn install -DskipTests=true
RUN cp /home/skylunch/app/target/*.jar /skylunch.jar
ENTRYPOINT ["nix", "develop", ".#build", "--command", "java", "-jar", "/skylunch.jar"]
