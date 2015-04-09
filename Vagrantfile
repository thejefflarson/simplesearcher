Vagrant.configure("2") do |config|
  config.vm.box = "http://cloud-images.ubuntu.com/vagrant/utopic/current/utopic-server-cloudimg-i386-vagrant-disk1.box"
  config.vm.provision "shell", inline: %Q{
    sudo apt-get -y update
    sudo apt-get -y install openjfx openjdk-8-jdk ant
  }
end
