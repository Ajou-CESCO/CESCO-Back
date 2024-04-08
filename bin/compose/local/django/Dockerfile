FROM python:3.10.6
ENV PYTHONUNBUFFERED 1

WORKDIR /app
ADD    ./requirements .
RUN    apt-get update -y && apt-get install -y vim cron
RUN    pip install --upgrade pip
RUN    pip install -r local.txt

# copy application code to WORKDIR
COPY ./../ /app/
WORKDIR /app/

COPY ./compose/production/django/entrypoint /entrypoint
RUN sed -i 's/\r$//g' /entrypoint
RUN chmod +x /entrypoint

COPY ./compose/local/django/start /start
RUN sed -i 's/\r$//g' /start
RUN chmod +x /start


ENTRYPOINT ["/entrypoint"]
