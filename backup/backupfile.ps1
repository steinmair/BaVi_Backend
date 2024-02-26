# Set variables for MinIO backup
$minioContainerName = "minio_server"
$minioBackupFileName = "minio_data_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').zip"
$minioBackupPath = "C:\backup"

# Set variables for MySQL backup
$mysqlContainerName = "mysql_server"
$mysqlBackupFileName = "mysql_data_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').zip"
$mysqlBackupPath = "C:\backup"

# Create backup directory if it doesn't exist
New-Item -ItemType Directory -Force -Path $minioBackupPath | Out-Null

# Export MinIO data to zip archive
docker exec $minioContainerName tar -cf - /data | Compress-Archive -DestinationPath "$minioBackupPath\$minioBackupFileName"

# Check if the MinIO backup was successful
if (Test-Path "$minioBackupPath\$minioBackupFileName") {
    Write-Host "Backup of MinIO data completed successfully. Backup file saved to: $minioBackupPath\$minioBackupFileName"
} else {
    Write-Host "Backup of MinIO data failed."
}

# Export MySQL data to zip archive
docker exec $mysqlContainerName mysqldump -u root --password=root BAVI | Compress-Archive -DestinationPath "$mysqlBackupPath\$mysqlBackupFileName"

# Check if the MySQL backup was successful
if (Test-Path "$mysqlBackupPath\$mysqlBackupFileName") {
    Write-Host "Backup of MySQL data completed successfully. Backup file saved to: $mysqlBackupPath\$mysqlBackupFileName"
} else {
    Write-Host "Backup of MySQL data failed."
}
