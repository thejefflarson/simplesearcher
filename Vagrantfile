Vagrant.configure("2") do |config|
  config.vm.box = "http://cloud-images.ubuntu.com/vagrant/vivid/current/vivid-server-cloudimg-i386-vagrant-disk1.box

  config.vm.provider do |v|
    v.memory = 1024
    v.cpus = 2
  end

  config.vm.provision "shell", inline: %Q{
    sudo apt-get update -y
    sudo apt-get install -y openjdk-8-jdk xdg-utils ant openjfx
  }
end
