module Display
  def self.progress_bar(value, max: 100, width: 20, color: "\e[38;5;178m")
    filled = [(value.to_f / max * width).round, width].min
    bar    = "█" * filled + "░" * (width - filled)
    "#{color}#{bar}\e[0m #{value}/#{max}"
  end

  def self.table(rows, headers: nil)
    all = headers ? [headers] + rows : rows
    widths = all.first.length.times.map { |i| all.map { |r| r[i].to_s.length }.max }

    if headers
      puts headers.each_with_index.map { |h, i| h.to_s.ljust(widths[i]) }.join("  ")
      puts widths.map { |w| "─" * w }.join("  ")
    end

    rows.each do |row|
      puts row.each_with_index.map { |cell, i| cell.to_s.ljust(widths[i]) }.join("  ")
    end
  end
end
