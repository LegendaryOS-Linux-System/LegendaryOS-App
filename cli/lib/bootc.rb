require 'open3'

module Bootc
  def self.status
    out, _, status = Open3.capture3('bootc status')
    status.success? ? out : "(bootc niedostępny)"
  end

  def self.upgrade!
    system('sudo bootc upgrade')
  end

  def self.rollback!
    system('sudo bootc rollback')
  end

  def self.switch!(image)
    system("sudo bootc switch #{image}")
  end
end
