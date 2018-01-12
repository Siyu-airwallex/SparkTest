import time
import docker
import os
import shutil
import docker.errors

DEV_REPOSITORY = 'registry.airwallex.com/postgres'
PRO_REPOSITORY = ''
TAG = '1712'

BASE_IMAGE = 'postgres_base:' + TAG
BASE_CONTAINER = 'postgres_base'

PERSIST_IMAGE = 'postgres_persist:' + TAG
PERSIST_CONTAINER = 'postgres_persist_' + TAG

DEV_IMAGE = DEV_REPOSITORY + ':' + TAG
PRO_IMAGE = PRO_REPOSITORY + ':' + TAG


client = docker.from_env()

try:
  prev_base_image = client.images.get(BASE_IMAGE)
  client.images.remove(BASE_IMAGE, force=True)
except docker.errors.ImageNotFound:
  pass

base_image = client.images.build(path='.', tag=BASE_IMAGE, dockerfile='DockerfileBase')

try:
  prev_base_container = client.containers.get(BASE_CONTAINER)
  prev_base_container.remove(force = True)
except docker.errors.NotFound:
  pass

base_container = client.containers.run(image=BASE_IMAGE, name=BASE_CONTAINER, ports={'5432/tcp':5432}, detach=True)

time.sleep(120)
#db_data, stat = postgres_base.get_archive('/var/lib/postgresql/data') // this method need to improve

if os.path.exists('dbdata'): shutil.rmtree('dbdata')
os.system('docker cp postgres_base:/var/lib/postgresql/data dbdata')

try:
  prev_persist_image = client.images.get(PERSIST_IMAGE)
  client.images.remove(PERSIST_IMAGE, force=True)
except docker.errors.ImageNotFound:
  pass

persist_image = client.images.build(path='.', tag=PERSIST_IMAGE, dockerfile='DockerfilePersist')

base_container.stop()
base_container.remove(force = True)
client.images.remove(BASE_IMAGE, force=True)
if os.path.exists('dbdata'): shutil.rmtree('dbdata')

try:
    prev_dev_image = client.images.get(DEV_IMAGE)
    client.images.remove(DEV_IMAGE, force=True)
except docker.errors.ImageNotFound:
    pass
os.system('docker tag ' + PERSIST_IMAGE + ' ' + DEV_IMAGE)

# try:
#     prev_pro_image = client.images.get(PRO_IMAGE)
#     client.images.remove(PRO_IMAGE, force=True)
# except docker.errors.ImageNotFound:
#     pass
#os.system('docker tag ' + PERSIST_IMAGE + ' ' + PRO_IMAGE)

client.images.push(repository= DEV_IMAGE)

