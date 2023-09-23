docker run --name booking-room -p 5432:5432 -e POSTGRES_PASSWORD=booking-room-pass -d postgres:15.4

psql -U postgres -d postgres

