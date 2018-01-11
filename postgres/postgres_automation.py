import time
import docker
import os
import shutil
import docker.errors

tag = 'test'

client = docker.from_env()

try:
  prev_base_image = client.images.get('postgres_base:test')
  client.images.remove('postgres_base:test', force=True)
except docker.errors.ImageNotFound:
  pass

base_image = client.images.build(path='.', tag='postgres_base:test', dockerfile='DockerfileBase')

try:
  prev_base_container = client.containers.get('postgres_base')
  prev_base_container.remove(force = True)
except docker.errors.NotFound:
  pass

base_container = client.containers.run(image='postgres_base:test', name='postgres_base', ports={'5432/tcp':5432}, detach=True)

time.sleep(100)
#db_data, stat = postgres_base.get_archive('/var/lib/postgresql/data') // this method need to improve

if os.path.exists('dbdata'): shutil.rmtree('dbdata')
os.system('docker cp postgres_base:/var/lib/postgresql/data dbdata')

persist_image = client.images.build(path='.', tag='charliefeng/postgres_persist:test', dockerfile='DockerfilePersist')

base_container.stop()
client.images.remove('postgres_base:test', force=True)
if os.path.exists('dbdata'): shutil.rmtree('dbdata')

client.images.push(repository= 'charliefeng/postgres_persist:' + tag, auth_config={'username':'charliefeng', 'password':'lyf61825'})

