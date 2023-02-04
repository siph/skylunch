{pkgs, ...}:
{
  networking.firewall.allowedTCPPorts = [ 80 443 6443 10250 ];
  environment.systemPackages = with pkgs; [ k3s ];
  services = {
    k3s = {
      enable = true;
      disableAgent = false;
      role = "server";
    };
  };
  programs = {
    neovim = {
      enable = true;
      defaultEditor = true;
      vimAlias = true;
    };
  };
  system.stateVersion = "22.11";
}
