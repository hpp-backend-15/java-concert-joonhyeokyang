import csv
import random
from datetime import datetime, timedelta

# Define number of rows for each table
num_concerts = 1000          # Sample for concerts table
num_performance_dates = 5000  # Sample for performance_dates table (many-to-one with concerts)
num_seats = 20000000          # Target rows for seats table

# Select a subset of concerts as "popular" concerts
popular_concert_ids = set(random.sample(range(1, num_concerts + 1), k=int(num_concerts * 0.1)))

# Generate concerts.csv
with open("concerts.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["concerts_id", "performer"])  # Column headers
    for concert_id in range(1, num_concerts + 1):
        performer = f"Performer_{concert_id}"
        writer.writerow([concert_id, performer])

# Generate performance_dates.csv
with open("performance_dates.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["performance_dates_id", "performance_dates_dates", "concerts_id"])  # Column headers
    for performance_date_id in range(1, num_performance_dates + 1):
        if performance_date_id % 10 == 0:  # Make more dates for popular concerts
            concerts_id = random.choice(list(popular_concert_ids))
        else:
            concerts_id = random.randint(1, num_concerts)

        # Randomize dates around today
        performance_date = (datetime.now() + timedelta(days=random.randint(0, 365))).date()
        writer.writerow([performance_date_id, performance_date, concerts_id])

# Generate seats.csv
status_options = ["AVAILABLE", "PENDING", "UNAVAILABLE"]
seat_prices = [70000, 90000, 110000, 150000]  # Updated realistic seat prices
with open("seats.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["seats_id", "version", "last_reserved_at", "performance_dates_id", "seats_price", "seats_status"])  # Column headers
    for seat_id in range(1, num_seats + 1):
        version = random.randint(1, 10)

        # Set last_reserved_at for historical dates and future dates
        reserved_days_ago = random.randint(0, 365)
        last_reserved_at = (datetime.now() - timedelta(days=reserved_days_ago)).strftime("%Y-%m-%d %H:%M:%S.%f")

        # Assign to a performance date, skewed towards popular concerts
        performance_dates_id = random.randint(1, num_performance_dates)

        # Higher chance of 'PENDING' and 'UNAVAILABLE' for seats in popular concerts
        if performance_dates_id % 10 == 0 and reserved_days_ago < 100:
            seats_status = random.choice(["PENDING", "UNAVAILABLE"])
        else:
            seats_status = "UNAVAILABLE" if reserved_days_ago >= 300 else random.choice(status_options)

        seats_price = random.choice(seat_prices)  # Choose price from predefined realistic prices

        # Write row to CSV
        writer.writerow([seat_id, version, last_reserved_at, performance_dates_id, seats_price, seats_status])
