# Dependancy Imports
import os
import sys
import logging
from src.utils.constants import LOG_FILE_NAME
from src.utils.constants import LOGS_DIR
from src.utils.constants import LOGS_STORAGE_DURATION
from src.utils.constants import LOG_LEVEL
from logging.handlers import TimedRotatingFileHandler


# Ensure the archive directory exists
if not os.path.exists(LOGS_DIR):
    os.makedirs(LOGS_DIR)
    

# Initializing log file and log handler
logger = logging.getLogger()
formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
stream_handler = logging.StreamHandler(sys.stdout)
stream_handler.setFormatter(formatter)

# Initialize rotating file handler for logger
rotating_handler = TimedRotatingFileHandler(LOG_FILE_NAME, when="midnight", backupCount=LOGS_STORAGE_DURATION)
rotating_handler.suffix = "%Y%m%d"
rotating_handler.setFormatter(formatter)

class CustomTimedRotatingFileHandler(TimedRotatingFileHandler):
    def doRollover(self):
        # Perform the standard rollover
        super().doRollover()
        
        # Move the old log files to the archive folder
        for filename in os.listdir("."):
            if filename.startswith(LOG_FILE_NAME) and filename != LOG_FILE_NAME:
                old_log_path = os.path.join(".", filename)
                new_log_path = os.path.join(LOGS_DIR, filename)
                os.rename(old_log_path, new_log_path)

# Replace the original handler with the custom handler
custom_rotating_handler = CustomTimedRotatingFileHandler(LOG_FILE_NAME, when="midnight", backupCount=LOGS_STORAGE_DURATION)
custom_rotating_handler.suffix = "%Y%m%d"
custom_rotating_handler.setFormatter(formatter)
logger.addHandler(custom_rotating_handler)

# Set log level
logger.setLevel(LOG_LEVEL)