Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty32"

  config.vm.provision "shell", inline: <<-SHELL
    sudo apt-get update
  SHELL
end
