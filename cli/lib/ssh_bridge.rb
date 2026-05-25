require 'open3'

module SshBridge
  def self.status(options)
    host = options[:hackeros_host]
    user = options[:hackeros_user]
    port = options[:hackeros_port]
    cmd  = "ssh -p #{port} -o ConnectTimeout=5 -o BatchMode=yes #{user}@#{host} 'uname -a && uptime'"
    out, err, status = Open3.capture3(cmd)
    if status.success?
      puts "\e[38;5;208mHackerOS @ #{host}:\e[0m"
      puts out.gsub(/^/, '  ')
    else
      puts "\e[38;5;196m✗ Brak połączenia z #{host}: #{err.strip}\e[0m"
    end
  end

  def self.scp(args, options)
    src, dst = args[0], args[1]
    return puts "\e[38;5;196m⚠ Użycie: legendary hackeros:copy <źródło> <cel>\e[0m" unless src && dst

    host = options[:hackeros_host]
    user = options[:hackeros_user]
    port = options[:hackeros_port]

    # Dodaj prefix hosta jeśli brakuje
    src = "#{user}@#{host}:#{src}" if dst.include?('/') && !src.include?('@')
    dst = "#{user}@#{host}:#{dst}" if src.include?('/') && !dst.include?('@')

    puts "\e[38;5;178m→ scp -P #{port} #{src} #{dst}\e[0m"
    system("scp -P #{port} #{src} #{dst}")
  end
end
