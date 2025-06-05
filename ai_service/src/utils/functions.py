import yaml

def read_config(file_path):
    """ Reads application configurations from .yml file """
    try:
        with open(file_path, 'r') as config_file:
            config = yaml.safe_load(config_file)
        return config
    except yaml.YAMLError as e:
        print(e)