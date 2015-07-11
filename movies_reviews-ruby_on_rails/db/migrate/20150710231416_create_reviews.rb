class CreateReviews < ActiveRecord::Migration
  def change
    create_table :reviews do |t|
      t.string :comments
      t.string :email
      t.integer :rating
      t.integer :m_id

      t.timestamps null: false
    end
  end
end
