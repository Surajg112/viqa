import sys
import os
from src.utils.constants import ARTIFACTS_DIR_PATH
from src.utils.logger import logger
from src.utils.exceptions import AiServiceException

if __name__ == "__main__":
    logger.info("Starting the AI Service...")
    os.makedirs(ARTIFACTS_DIR_PATH, exist_ok=True)
    
    try:
       x = 1/0
    except Exception as e:
        logger.error(AiServiceException(e, sys))
        raise AiServiceException(e, sys)
    finally:
        logger.info("AI Service stopped.")