FROM python:3.10.4
ENV PYTHONUNBUFFERED 1

WORKDIR /app
ADD    ./bin/requirements ./requirements
RUN    apt-get update -y && apt-get install -y vim cron
RUN    pip install --upgrade pip
RUN    pip install -r ./requirements/production.txt

COPY ./bin/compose/production/django/cronjob /etc/cron.d/cronjob
RUN chmod 0644 /etc/cron.d/cronjob
RUN crontab /etc/cron.d/cronjob
RUN cron

COPY ./bin/compose/production/django/entrypoint /entrypoint
RUN sed -i 's/\r$//g' /entrypoint
RUN chmod +x /entrypoint

COPY ./bin/compose/production/django/start /start
RUN sed -i 's/\r$//g' /start
RUN chmod +x /start

ENTRYPOINT ["/entrypoint"]
