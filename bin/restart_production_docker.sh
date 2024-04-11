docker-compose -p prod_inpleroutine -f production.yml down

if [ "$1" == "build" ]; then
    docker-compose -p prod_inpleroutine -f production.yml up --build -d

    docker exec -it prod_inpleroutine-django-1 python /app/manage.py migrate
    docker exec -it prod_inpleroutine-django-1 python /app/manage.py loaddata initial_data.json
else
    docker-compose -p prod_inpleroutine -f production.yml up -d
fi
