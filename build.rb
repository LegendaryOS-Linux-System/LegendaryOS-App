#!/usr/bin/env ruby
# frozen_string_literal: true
# build.rb
# LegendaryOS App — system budowania projektu (desktop, Android, CLI)

require 'fileutils'

# ── Kolory terminala ──────────────────────────────────────────────────────────
module C
  RESET   = "\e[0m"
  BOLD    = "\e[1m"
  MAGENTA = "\e[38;5;165m"
  GREEN   = "\e[38;5;82m"
  RED     = "\e[38;5;196m"
  MUTED   = "\e[38;5;244m"
  CYAN    = "\e[38;5;51m"
  ORANGE  = "\e[38;5;214m"

  # Alias — stara nazwa używana w komunikatach
  GOLD = MAGENTA

  def self.gold(s)    = "#{MAGENTA}#{s}#{RESET}"
  def self.magenta(s) = "#{MAGENTA}#{s}#{RESET}"
  def self.green(s)   = "#{GREEN}#{s}#{RESET}"
  def self.red(s)     = "#{RED}#{s}#{RESET}"
  def self.muted(s)   = "#{MUTED}#{s}#{RESET}"
  def self.bold(s)    = "#{BOLD}#{s}#{RESET}"
  def self.cyan(s)    = "#{CYAN}#{s}#{RESET}"
  def self.orange(s)  = "#{ORANGE}#{s}#{RESET}"
end

# ── Definicja zadań ───────────────────────────────────────────────────────────
TASKS = {
  'run'                   => 'Uruchom desktop GUI',
  'build-desktop'         => 'Zbuduj JAR (uber jar dla bieżącego OS)',
  'build-deb'             => 'Zbuduj .deb',
  'build-rpm'             => 'Zbuduj .rpm (LegendaryOS/Fedora)',
  'build-appimage'        => 'Zbuduj AppImage',
  'build-android'         => 'Zbuduj debug APK',
  'build-android-release' => 'Zbuduj release APK',
  'install-android'       => 'Zainstaluj APK na telefonie (ADB)',
  'cli-install'           => 'Zainstaluj CLI (legendary) globalnie',
  'clean'                 => 'Wyczyść wszystkie artefakty',
  'check-java'            => 'Sprawdź wersję Java i środowisko',
  'check-android'         => 'Sprawdź Android SDK i wygeneruj local.properties',
  'setup'                 => 'Pierwsze uruchomienie: sprawdź środowisko + wygeneruj wrapper',
  'help'                  => 'Wyświetl tę pomoc',
  }.freeze

# ── Helpers ───────────────────────────────────────────────────────────────────
def run_command(cmd)
  puts C.muted("  → #{cmd}")
  success = system(cmd)
  unless success
    puts C.red("\n✗ Polecenie nie powiodło się: #{cmd}")
    exit(1)
  end
  success
end

def gradle_bin
  if File.exist?('./gradlew')
    './gradlew'
  elsif system('which gradle > /dev/null 2>&1')
    'gradle'
  else
    puts C.red("✗ Nie znaleziono gradle ani gradlew.")
    puts C.muted("  Zainstaluj: sudo dnf install gradle")
    puts C.muted("  Lub uruchom: ruby build.rb setup")
    exit(1)
  end
end

def section(title)
  puts "\n#{C.gold('▸')} #{C.bold(title)}"
end

def ok(msg)
  puts C.green("  ✓ #{msg}")
end

def warn_msg(msg)
  puts C.orange("  ⚠ #{msg}")
end

# ── Java ──────────────────────────────────────────────────────────────────────
def check_java_version!
  raw = `java -version 2>&1`
  match = raw.match(/version "?(\d+)/)
  ver = match ? match[1].to_i : 0
  if ver < 21
    puts C.red("✗ Wymagana Java 21+. Wykryta: #{ver == 0 ? 'brak' : ver}")
    puts C.muted("  Zainstaluj: sudo dnf install java-21-openjdk-devel")
    puts C.muted("  Ustaw JAVA_HOME:")
    puts C.muted("    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk")
    puts C.muted("    export PATH=\$JAVA_HOME/bin:\$PATH")
    exit(1)
  end
  ver
end

def set_java_home_hint
  java_home = ENV['JAVA_HOME'] || ''
  unless java_home =~ /java-2[1-9]|java-latest/
    warn_msg "Ustaw JAVA_HOME jeśli build się nie powiedzie:"
    warn_msg "  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk"
    warn_msg "  export PATH=\$JAVA_HOME/bin:\$PATH"
  end
end

def ensure_wrapper!
  return if File.exist?('./gradlew')
  warn_msg "Brak gradlew — generuję wrapper (wymaga systemowego gradle)..."
  unless system('which gradle > /dev/null 2>&1')
    puts C.red("✗ Brak gradle. Zainstaluj: sudo dnf install gradle")
    exit(1)
  end
  system('gradle wrapper --gradle-version 8.8')
  system('chmod +x gradlew')
  ok "gradlew wygenerowany"
end

# ── Android SDK — local.properties ───────────────────────────────────────────
# Gradle Android wymaga local.properties z sdk.dir.
# Kandydaci lokalizacji Android SDK:
ANDROID_SDK_CANDIDATES = [
  ENV['ANDROID_HOME'],
  ENV['ANDROID_SDK_ROOT'],
  File.expand_path('~/Android/Sdk'),
  File.expand_path('~/.android/sdk'),
  '/opt/android-sdk',
  '/usr/lib/android-sdk',
  ].compact.freeze

def find_android_sdk
  ANDROID_SDK_CANDIDATES.find { |p| p && Dir.exist?(File.join(p, 'platform-tools')) }
end

# Zapisuje local.properties jeśli brak lub sdk.dir jest nieaktualny.
def ensure_local_properties!
  props_path = 'local.properties'
  sdk = find_android_sdk

  unless sdk
    puts C.red("✗ Nie znaleziono Android SDK.")
    puts C.muted("  Ustaw zmienną środowiskową:")
    puts C.muted("    export ANDROID_HOME=~/Android/Sdk")
    puts C.muted("  Lub zainstaluj Android Studio / SDK przez:")
    puts C.muted("    https://developer.android.com/studio")
    exit(1)
  end

  # Sprawdź czy local.properties już wskazuje właściwy SDK
  if File.exist?(props_path)
    current = File.read(props_path)
    if current.include?("sdk.dir=#{sdk}") || current.include?("sdk.dir=#{sdk.gsub('/', '\\\\')}")
      return  # już OK
    end
    # Zaktualizuj istniejący plik
    updated = current.gsub(/^sdk\.dir=.*$/, "sdk.dir=#{sdk}")
    if updated == current
      updated += "\nsdk.dir=#{sdk}\n"
    end
    File.write(props_path, updated)
    ok "Zaktualizowano local.properties → sdk.dir=#{sdk}"
  else
    # Utwórz nowy
    File.write(props_path, "sdk.dir=#{sdk}\n")
    ok "Utworzono local.properties → sdk.dir=#{sdk}"
  end
end

# ── Help ──────────────────────────────────────────────────────────────────────
def help
  puts "\n#{C.gold('╔══════════════════════════════════════════════════╗')}"
  puts "#{C.gold('║')}  #{C.bold('LegendaryOS App')} #{C.muted('— build.rb')}                       #{C.gold('║')}"
  puts "#{C.gold('╚══════════════════════════════════════════════════╝')}\n\n"
  puts "  #{C.bold('UŻYCIE:')}  #{C.cyan('ruby build.rb')} #{C.gold('<polecenie>')}\n\n"
  puts "  #{C.bold('PIERWSZE URUCHOMIENIE:')}"
  puts "    #{C.muted('$')} #{C.cyan('ruby build.rb setup')}         #{C.muted('# generuje gradlew + sprawdza SDK')}"
  puts "    #{C.muted('$')} #{C.cyan('ruby build.rb check-java')}    #{C.muted('# sprawdza środowisko Java')}"
  puts "    #{C.muted('$')} #{C.cyan('ruby build.rb check-android')} #{C.muted('# sprawdza Android SDK')}"
  puts "    #{C.muted('$')} #{C.cyan('ruby build.rb run')}           #{C.muted('# uruchamia GUI desktopowe')}\n\n"
  puts "  #{C.bold('POLECENIA:')}"
  TASKS.each do |task, desc|
    puts "    #{C.gold(task.ljust(26))} #{C.muted(desc)}"
  end
  puts "\n  #{C.bold('WYMAGANIA:')}"
  puts "    #{C.muted('Java 21+:  ')} sudo dnf install java-21-openjdk-devel"
  puts "    #{C.muted('ADB:       ')} sudo dnf install android-tools"
  puts "    #{C.muted('Schowek:   ')} sudo dnf install wl-clipboard"
  puts "    #{C.muted('Mirror:    ')} flatpak install flathub info.guardianproject.Scrcpy"
  puts "\n  #{C.bold('UWAGI:')}"
  puts "    #{C.muted('• build-desktop buduje uber JAR (packageUberJarForCurrentOS)')}"
  puts "    #{C.muted('• Android SDK wykrywany z ANDROID_HOME lub ~/Android/Sdk')}"
  puts "    #{C.muted('• local.properties generowany automatycznie')}"
  puts
end

# ── Główna logika ─────────────────────────────────────────────────────────────
task = ARGV.first || 'help'

case task

when 'setup'
  section "Konfiguracja projektu (pierwsze uruchomienie)"
  check_java_version!
  ensure_wrapper!
  ensure_local_properties!
  ok "Projekt gotowy."
  puts C.muted("  Następne kroki:")
  puts C.muted("    ruby build.rb run          # uruchom GUI")
  puts C.muted("    ruby build.rb build-android # zbuduj APK")

when 'check-java'
  section "Sprawdzam środowisko Java"
  ver = check_java_version!
  ok "Java #{ver} — OK"
  set_java_home_hint
  puts C.muted("  JAVA_HOME   = #{ENV['JAVA_HOME'] || '(nieustawiony)'}")
  puts C.muted("  gradle/gradlew = #{gradle_bin rescue '(brak)'}")

when 'check-android'
  section "Sprawdzam Android SDK"
  sdk = find_android_sdk
  if sdk
    ok "Znaleziono Android SDK: #{sdk}"
    ensure_local_properties!
    # Sprawdź dostępne platform-tools
    pt = File.join(sdk, 'platform-tools', 'adb')
    puts C.muted("  ADB: #{File.exist?(pt) ? pt : '(brak platform-tools?)'}")
    # Sprawdź zainstalowane platformy
    platforms = Dir[File.join(sdk, 'platforms', 'android-*')].map { File.basename(_1) }.sort
    puts C.muted("  Platformy: #{platforms.join(', ').then { _1.empty? ? '(brak)' : _1 }}")
  else
    puts C.red("✗ Android SDK nie znaleziony.")
    puts C.muted("  Ustaw: export ANDROID_HOME=~/Android/Sdk")
    exit(1)
  end

when 'run'
  section "Uruchamiam desktop GUI"
  check_java_version!
  ensure_wrapper!
  set_java_home_hint
  run_command("#{gradle_bin} :main:run")

when 'build-desktop'
  # NAPRAWA: packageJar jest niejednoznaczne w Compose Desktop.
  # Właściwa nazwa zadania to packageUberJarForCurrentOS.
  section "Buduję desktop JAR (uber jar)"
  check_java_version!
  ensure_wrapper!
  set_java_home_hint
  run_command("#{gradle_bin} :main:packageUberJarForCurrentOS")
  jar_dir = 'main/build/compose/jars'
  jar = Dir["#{jar_dir}/*.jar"].first
  ok "JAR gotowy: #{jar || jar_dir + '/'}"

when 'build-deb'
  section "Buduję pakiet .deb"
  check_java_version!
  ensure_wrapper!
  run_command("#{gradle_bin} :main:packageDeb")
  ok "Plik .deb gotowy w: main/build/compose/binaries/main/deb/"

when 'build-rpm'
  section "Buduję pakiet .rpm (LegendaryOS / Fedora)"
  check_java_version!
  ensure_wrapper!
  set_java_home_hint
  run_command("#{gradle_bin} :main:packageRpm")
  rpm = Dir['main/build/compose/binaries/main/rpm/*.rpm'].first
  ok "Plik .rpm gotowy: #{rpm || 'main/build/compose/binaries/main/rpm/'}"
  if rpm
    puts C.muted("  Zainstaluj: sudo rpm -i #{rpm}")
    puts C.muted("  Lub:        sudo dnf install #{rpm}")
  end

when 'build-appimage'
  section "Buduję AppImage"
  check_java_version!
  ensure_wrapper!
  run_command("#{gradle_bin} :main:packageAppImage")
  ok "AppImage gotowy w: main/build/compose/binaries/main/app/"

when 'build-android'
  # NAPRAWA: upewniamy się że local.properties istnieje przed buildem
  section "Buduję Android APK (debug)"
  ensure_wrapper!
  ensure_local_properties!
  run_command("#{gradle_bin} :android:assembleDebug")
  apk = 'android/build/outputs/apk/debug/android-debug.apk'
  ok "APK gotowy: #{apk}"
  if File.exist?(apk)
    size_mb = (File.size(apk) / 1024.0 / 1024.0).round(1)
    puts C.muted("  Rozmiar: #{size_mb} MB")
    puts C.muted("  Zainstaluj: ruby build.rb install-android")
  end

when 'build-android-release'
  section "Buduję Android APK (release)"
  ensure_wrapper!
  ensure_local_properties!
  warn_msg "Release wymaga klucza podpisującego (keystore)"
  run_command("#{gradle_bin} :android:assembleRelease")
  ok "APK release gotowy: android/build/outputs/apk/release/"

when 'install-android'
  section "Instaluję APK na urządzeniu Android"
  adb_out = `adb devices 2>&1`
  unless adb_out.include?("\tdevice")
    puts C.red("✗ Brak urządzenia Android. Podłącz przez USB z włączonym USB Debugging.")
    exit(1)
  end
  ensure_wrapper!
  ensure_local_properties!
  run_command("#{gradle_bin} :android:installDebug")
  ok "APK zainstalowany na urządzeniu"

when 'cli-install'
  section "Instaluję CLI (legendary)"
  current_dir = Dir.pwd
  cli_path    = File.join(current_dir, 'cli', 'legendary')
  unless File.exist?(cli_path)
    puts C.red("✗ Nie znaleziono: #{cli_path}")
    exit(1)
  end
  run_command("chmod +x #{cli_path}")
  run_command("sudo ln -sf #{cli_path} /usr/local/bin/legendary")
  ok "CLI zainstalowane — dostępne jako: legendary"
  puts C.muted("  Sprawdź: legendary help")
  puts C.muted("  Uruchom GUI: legendary app")

when 'clean'
  section "Czyszczę artefakty"
  ensure_wrapper!
  run_command("#{gradle_bin} clean")
  ok "Wyczyszczono"

when 'help', '-h', '--help'
  help

else
  puts C.red("\n✗ Nieznane polecenie: #{task}")
  help
  exit 1
end
