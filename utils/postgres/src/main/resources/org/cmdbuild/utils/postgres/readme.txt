
downloaded from here: https://www.enterprisedb.com/download-postgresql-binaries

for example, 9.6 link: https://get.enterprisedb.com/postgresql/postgresql-9.6.6-1-linux-x64-binaries.tar.gz 

then stripped only required binaries, for example:

$ zip -9 postgresql-10.4-1-linux-x64-binaries_stripped.zip pgsql/bin/{pg_dump,pg_restore,psql,psql.bin} pgsql/lib/{libedit*,libncurses*}


# TODO postgres 11
# TODO include libpq
