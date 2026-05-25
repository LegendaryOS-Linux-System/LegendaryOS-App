#!/usr/bin/env ruby
require 'fileutils'

# Definicja dostępnych zadań i ich opisów
TASKS = {
	'run'                   => 'Uruchom desktop GUI',
	'build-desktop'         => 'Zbuduj JAR',
	'build-deb'             => 'Zbuduj .deb',
	'build-rpm'             => 'Zbuduj .rpm (LegendaryOS)',
	'build-appimage'        => 'Zbuduj AppImage',
	'build-android'         => 'Zbuduj debug APK',
	'build-android-release' => 'Zbuduj release APK',
	'install-android'       => 'Zainstaluj APK na telefonie',
	'cli-install'           => 'Zainstaluj CLI (legendary)',
	'clean'                 => 'Wyczyść artefakty',
	'help'                  => 'Wyświetl tę pomoc'
}.freeze

def run_command(cmd)
	puts "-> #{cmd}"
	system(cmd) || exit(1)
end

def help
	puts "\n  LegendaryOS App — dostępne polecenia:\n\n"
	TASKS.each do |task, desc|
		puts "  ruby build.rb #{task.ljust(25)} #{desc}"
	end
	puts "\n"
end

# Logika wykonawcza dla poszczególnych zadań
task = ARGV.first || 'help'

case task
when 'run'
	run_command('./gradlew :main:run')

when 'build-desktop'
	run_command('./gradlew :main:packageJar')

when 'build-deb'
	run_command('./gradlew :main:packageDeb')

when 'build-rpm'
	run_command('./gradlew :main:packageRpm')

when 'build-appimage'
	run_command('./gradlew :main:packageAppImage')

when 'build-android'
	run_command('./gradlew :android:assembleDebug')
	puts "\n✓ APK gotowy: android/build/outputs/apk/debug/android-debug.apk"

when 'build-android-release'
	run_command('./gradlew :android:assembleRelease')

when 'install-android'
	run_command('./gradlew :android:installDebug')

when 'cli-install'
	current_dir = Dir.pwd
	run_command('chmod +x cli/legendary')
	run_command("sudo ln -sf #{current_dir}/cli/legendary /usr/local/bin/legendary")
	puts "✓ CLI zainstalowane: legendary"

when 'clean'
	run_command('./gradlew clean')

when 'help', '-h', '--help'
	help

else
	puts "Nieznane polecenie: #{task}"
	help
	exit 1
end
