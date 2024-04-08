# Use the official SQL Server 2019 image as the base image
FROM mcr.microsoft.com/mssql/server:2019-latest

# Switch to root user for installation
USER root

WORKDIR /data

# Install necessary packages
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    unixodbc \
    libpq-dev \
    cron \
    vim \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install pyodbc and psycopg
RUN pip3 install pyodbc psycopg2-binary requests

# Copy the Python script and other required files
COPY ./compose/production/mssql/ /data

ADD ./compose/production/mssql/cronjob /etc/cron.d/cronjob
RUN chmod 0644 /etc/cron.d/cronjob
RUN crontab /etc/cron.d/cronjob
RUN cron

COPY ./compose/production/mssql/entrypoint /entrypoint
RUN sed -i 's/\r$//g' /entrypoint
RUN chmod +x /entrypoint

ENTRYPOINT ["/entrypoint"]

# Switch back to mssql user
USER mssql
