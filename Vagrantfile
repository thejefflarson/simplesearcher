Vagrant.configure("2") do |config|
  config.vm.box = "http://cloud-images.ubuntu.com/vagrant/utopic/current/utopic-server-cloudimg-i386-vagrant-disk1.box"
  config.vm.provision "shell", inline: %Q{
    sudo add-apt-repository -y ppa:webupd8team/java
    sudo apt-get update -y
    echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 boolean true" | sudo debconf-set-selections
    sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -q oracle-java8-installer xdg-utils ant
  }
end
