To run:

```
cd /filesystempath/muchbetter

gradle build

#optional - only run this if you plan on launching containers outside of docker compose
docker build -t muchbetter .

docker compose build

docker compose up
```

---------------------------------------------------

Note, this will launch a REDIS container instance as well as the muchbetter container instance.

This will fail to launch if there are conflicting ports in use (5050, or 6379). 