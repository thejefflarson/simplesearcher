Vagrant.configure("2") do |config|
  config.vm.box = "http://cloud-images.ubuntu.com/vagrant/utopic/current/utopic-server-cloudimg-i386-vagrant-disk1.box"
  config.vm.provision "shell", inline: %Q{
    sudo add-apt-repository -y ppa:webupd8team/java
    sudo apt-get update -y
    sudo apt-get install -y oracle-java8-installer xdg-utils ant
  }
end
