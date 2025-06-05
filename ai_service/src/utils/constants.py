from src.utils.functions import read_config

# Rading config file
config = read_config("config.yml")
logging_config = config.get("Logging")
artifacts_config = config.get("Artifacts")

## Configurable Constants

# Logging
LOG_FILE_NAME = logging_config['LogFileName']
LOGS_DIR = logging_config['LogDir']
LOGS_STORAGE_DURATION = logging_config['LogStorageDuration']
LOG_LEVEL = logging_config['LogLevel']

# Artifacts
ARTIFACTS_DIR_PATH =  artifacts_config['ArtifactsDirPath']