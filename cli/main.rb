#!/usr/bin/env ruby
# frozen_string_literal: true

# ╔══════════════════════════════════════════════════════════════╗
# ║           LegendaryOS App — CLI (Ruby)                       ║
# ║  Zarządzaj LegendaryOS, HackerOS i Androidem z terminala     ║
# ╚══════════════════════════════════════════════════════════════╝

require 'optparse'
require 'open3'
require 'json'
require_relative 'lib/adb'
require_relative 'lib/bootc'
require_relative 'lib/ssh_bridge'
require_relative 'lib/display'

VERSION = '1.0.0'

# ── Color helpers ──────────────────────────────────────────────────────────────
module C
  RESET  = "\e[0m"
  BOLD   = "\e[1m"
  GOLD   = "\e[38;5;178m"
  ORANGE = "\e[38;5;208m"
  BLUE   = "\e[38;5;75m"
  GREEN  = "\e[38;5;82m"
  RED    = "\e[38;5;196m"
  MUTED  = "\e[38;5;244m"
  CYAN   = "\e[38;5;51m"

  def self.gold(s)   = "#{GOLD}#{s}#{RESET}"
  def self.orange(s) = "#{ORANGE}#{s}#{RESET}"
  def self.blue(s)   = "#{BLUE}#{s}#{RESET}"
  def self.green(s)  = "#{GREEN}#{s}#{RESET}"
  def self.red(s)    = "#{RED}#{s}#{RESET}"
  def self.muted(s)  = "#{MUTED}#{s}#{RESET}"
  def self.bold(s)   = "#{BOLD}#{s}#{RESET}"
end

# ── Banner ─────────────────────────────────────────────────────────────────────
def print_banner
  puts C.gold(<<~BANNER)
    ╔══════════════════════════════════════════════════╗
    ║  #{C.bold('LegendaryOS App')}#{C.gold(' CLI v')}#{VERSION}#{C.gold('                         ║')}
    ║  #{C.muted('LegendaryOS · HackerOS · Android Bridge')}#{C.gold('        ║')}
    ╚══════════════════════════════════════════════════╝
  BANNER
end

# ── Commands ──────────────────────────────────────────────────────────────────
COMMANDS = {
  # System
  'status'       => 'Pokaż status systemu LegendaryOS',
  'upgrade'      => 'Zaktualizuj system przez bootc',
  'rollback'     => 'Przywróć poprzednią wersję systemu',
  'info'         => 'Informacje o systemie i zasobach',

  # Android
  'adb:devices'  => 'Lista podłączonych urządzeń Android',
  'adb:connect'  => 'Połącz z urządzeniem (IP lub USB)',
  'adb:shell'    => 'Otwórz powłokę ADB',
  'adb:mirror'   => 'Uruchom mirror ekranu (scrcpy)',
  'adb:files'    => 'Transfer plików Android ↔ Linux',
  'adb:sms'      => 'Pokaż ostatnie SMS-y',

  # HackerOS
  'hackeros:ssh'     => 'Połącz SSH z HackerOS',
  'hackeros:status'  => 'Status zdalnego HackerOS',
  'hackeros:copy'    => 'Kopiuj plik na/z HackerOS (SCP)',

  # App
  'gui'          => 'Uruchom GUI LegendaryOS App',
  'help'         => 'Wyświetl pomoc',
  'version'      => 'Wersja CLI',
}.freeze

def print_help
  print_banner
  puts
  puts C.bold("  UŻYCIE:")
  puts "    #{C.gold('legendary')} #{C.blue('<polecenie>')} [opcje]\n\n"
  puts C.bold("  POLECENIA:")
  COMMANDS.each do |cmd, desc|
    puts "    #{C.gold(cmd.ljust(22))} #{C.muted(desc)}"
  end
  puts
  puts C.bold("  PRZYKŁADY:")
  [
    'legendary status',
    'legendary upgrade',
    'legendary adb:devices',
    'legendary adb:connect 192.168.1.5:5555',
    'legendary adb:mirror',
    'legendary hackeros:ssh user@192.168.1.20',
    'legendary gui',
  ].each { |ex| puts "    #{C.muted('$')} #{C.blue(ex)}" }
  puts
end

# ── Command Runners ────────────────────────────────────────────────────────────
def run_status
  puts "\n#{C.bold(C.gold('── System LegendaryOS ──'))}"
  [
    ['Dystrybucja', `uname -o`.strip],
    ['Jądro',       `uname -r`.strip],
    ['Hostname',    `hostname`.strip],
    ['Uptime',      `uptime -p`.strip],
  ].each { |k, v| puts "  #{C.muted(k.ljust(16))} #{v}" }

  puts "\n#{C.bold(C.gold('── bootc Status ──'))}"
  out, _, status = Open3.capture3('bootc status')
  if status.success?
    puts out.gsub(/^/, '  ')
  else
    puts C.muted('  bootc niedostępny')
  end

  puts "\n#{C.bold(C.gold('── Android ──'))}"
  adb_out = AdbHelper.devices
  puts adb_out.gsub(/^/, '  ')
end

def run_upgrade
  puts C.gold("\n→ Uruchamiam: bootc upgrade\n")
  system('sudo bootc upgrade')
end

def run_rollback
  puts C.orange("\n→ Uruchamiam: bootc rollback\n")
  system('sudo bootc rollback')
end

def run_info
  puts "\n#{C.bold(C.gold('── Zasoby systemowe ──'))}"
  mem = File.read('/proc/meminfo').split("\n")
  total = mem.find { _1.start_with?('MemTotal') }&.split&.dig(1)&.to_i || 0
  avail = mem.find { _1.start_with?('MemAvailable') }&.split&.dig(1)&.to_i || 0
  used = total - avail
  ram_pct = total > 0 ? (used.to_f / total * 100).round(1) : 0

  disk = `df -h /`.split("\n").last.split
  puts "  #{C.muted('RAM użyty:')}   #{used / 1024}MB / #{total / 1024}MB (#{ram_pct}%)"
  puts "  #{C.muted('Dysk /:')}      #{disk[2]} / #{disk[1]} (#{disk[4]})"
  puts "  #{C.muted('CPU load:')}    #{File.read('/proc/loadavg').split[0..2].join(', ')}"
end

def run_adb(sub, args)
  case sub
  when 'devices' then puts AdbHelper.devices
  when 'connect' then puts AdbHelper.connect(args.first || '')
  when 'shell'   then exec('adb shell')
  when 'mirror'  then
    puts C.gold('→ Uruchamiam scrcpy...')
    system('scrcpy --stay-awake --window-title="LegendaryOS — Mirror"')
  when 'files'   then puts AdbHelper.pull_push(args)
  when 'sms'     then puts AdbHelper.sms_list
  else puts C.red("Nieznana subkomenda adb: #{sub}")
  end
end

def run_hackeros(sub, args, options)
  case sub
  when 'ssh'
    target = args.first || "#{options[:hackeros_user]}@#{options[:hackeros_host]}"
    exec("ssh #{target}")
  when 'status'
    SshBridge.status(options)
  when 'copy'
    SshBridge.scp(args, options)
  else
    puts C.red("Nieznana subkomenda hackeros: #{sub}")
  end
end

def run_gui
  puts C.gold('→ Uruchamiam LegendaryOS App GUI...')
  app_jar = File.expand_path('../main/build/compose/jars/LegendaryOS-App.jar', __dir__)
  if File.exist?(app_jar)
    exec("java -jar #{app_jar}")
  else
    puts C.red("GUI nie znaleziony. Zbuduj najpierw: ./gradlew :main:packageJar")
  end
end

# ── Main ────────────────────────────────────────────────────────────────────────
options = {
  hackeros_host: 'localhost',
  hackeros_user: 'hacker',
  hackeros_port: 22,
}

OptionParser.new do |opts|
  opts.banner = ''
  opts.on('--hackeros-host HOST', 'Host HackerOS')   { |v| options[:hackeros_host] = v }
  opts.on('--hackeros-user USER', 'Użytkownik SSH')   { |v| options[:hackeros_user] = v }
  opts.on('--hackeros-port PORT', Integer, 'Port SSH') { |v| options[:hackeros_port] = v }
  opts.on('-v', '--version', 'Wersja') { puts "legendary v#{VERSION}"; exit }
  opts.on('-h', '--help', 'Pomoc')     { print_help; exit }
end.parse!

command = ARGV.shift&.downcase

if command.nil?
  print_help
  exit 0
end

if command.include?(':')
  prefix, sub = command.split(':', 2)
  args = ARGV
  case prefix
  when 'adb'      then run_adb(sub, args)
  when 'hackeros' then run_hackeros(sub, args, options)
  else puts C.red("Nieznany prefix: #{prefix}")
  end
else
  case command
  when 'status'  then run_status
  when 'upgrade' then run_upgrade
  when 'rollback' then run_rollback
  when 'info'    then run_info
  when 'gui'     then run_gui
  when 'help'    then print_help
  when 'version' then puts "legendary v#{VERSION}"
  else
    puts C.red("Nieznane polecenie: #{command}")
    puts C.muted("Wpisz 'legendary help' aby zobaczyć dostępne polecenia.")
    exit 1
  end
end
