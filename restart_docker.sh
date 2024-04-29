docker stop pillintime_spring
docker rm pillintime_spring

docker build -t pillintime_spring .
docker run -d -p 8080:8080 --name pillintime_spring pillintime_spring
