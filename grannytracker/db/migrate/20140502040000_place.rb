class Place < ActiveRecord::Migration
  def up
    create_table :places do |t|
      t.integer :room_id

      t.timestamps
    end
  end

  def down
    drop_table :places
  end
end
