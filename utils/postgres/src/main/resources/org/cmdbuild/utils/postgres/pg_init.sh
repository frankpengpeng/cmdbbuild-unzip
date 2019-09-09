#!/bin/bash

install_path="$1"
binaries="$2"
port=$3
admin_psw="$4"

data_path="$install_path/data"

tar -xf "$binaries" -C "$install_path" || exit 1

export PATH="$install_path"/pgsql/bin:$PATH 

initdb -D "$data_path" -U postgres || exit 1

echo -e "\n\nport = $port\n\n" >> "$data_path"/postgresql.conf

# sed -i -r 's#^(host.*all.*all.*)trust#\1md5#g' "$data_path"/pg_hba.conf  ## TODO

