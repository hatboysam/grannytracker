class Place < ActiveRecord::Base

  # Get all the events that took place today
  def self.get_today
    Place.where('created_at > ?', Date.today)
  end

  def self.last_in(r_id)
    Place.where('room_id = ?', r_id).last
  end

  # Filter events to find the important ones and keep only state changes
  def self.filter(events)
    filtered = [events[0]]
    events.each_with_index do |evt, index|
      if (index > 0)
        prev_evt = events[index - 1]
        filtered.push(evt) unless evt.room_id == prev_evt.room_id
      end
    end

    filtered
  end

  def time_ago
    seconds_diff = created_at - DateTime.now
    minutes_diff = (-1 * seconds_diff) / 60;

    minutes_diff.round
  end

  def clock_time
    created_at.strftime("%H:%M")
  end

end