Vagrant.configure("2") do |config|
  config.vm.box = "ubunty/trusty32"
  config.vm.provision "shell", inline: %Q{
    sudo apt-get -y update
  }
end