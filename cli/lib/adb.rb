require 'open3'

module AdbHelper
  def self.run(*args)
    out, err, status = Open3.capture3('adb', *args)
    status.success? ? out.strip : "Błąd ADB: #{err.strip}"
  end

  def self.devices
    run('devices', '-l')
  end

  def self.connect(address)
    return "⚠ Podaj adres IP (np. 192.168.1.5:5555)" if address.empty?
    run('connect', address)
  end

  def self.pull_push(args)
    src, dst = args[0], args[1]
    return "⚠ Użycie: legendary adb:files <źródło> <cel>" unless src && dst
    if src.start_with?('android:')
      run('pull', src.sub('android:', ''), dst)
    else
      run('push', src, dst.sub('android:', ''))
    end
  end

  def self.sms_list
    # Wymaga ADB + odpowiednich uprawnień
    result = run('shell', 'content query --uri content://sms/inbox --projection address,body,date --sort "date DESC" --where "date > #{(Time.now.to_i - 86400) * 1000}"')
    result.empty? ? "(brak SMS-ów)" : result
  end
end
