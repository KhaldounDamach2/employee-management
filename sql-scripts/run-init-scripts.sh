#!/bin/bash
echo "Waiting for Oracle to be ready..."
while ! nc -z localhost 1521; do
  sleep 5
done

echo "Oracle is ready, running initialization scripts..."
for sql_file in /docker-entrypoint-initdb.d/tables/*.sql; do
  echo "Running table script: $sql_file"
  sqlplus -s system/"$ORACLE_PWD"@XE @"$sql_file"
done

for sql_file in /docker-entrypoint-initdb.d/packages/*.sql; do
  echo "Running package script: $sql_file"
  sqlplus -s system/"$ORACLE_PWD"@XE @"$sql_file"
done

echo "Database initialization complete!"