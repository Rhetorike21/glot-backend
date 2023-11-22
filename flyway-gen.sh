if [ -z "$1" ]
  then
    echo "-> Please provide file name"
    exit 1
fi

folder_name='./src/main/resources/db/migration'
file_name=${1}

time_stamp=$(date '+%Y%m%d%H%M%S')
file_path="${folder_name}/V${time_stamp}__${file_name}.sql"
touch "$file_path"
echo "Migration file created at $file_path"
