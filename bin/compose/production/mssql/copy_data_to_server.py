import requests
import pyodbc
import psycopg2
import csv
import os

from datetime import datetime, timedelta
from psycopg2 import sql


MSSQL_SERVER = "localhost"
MSSQL_PORT = "1433"
MSSQL_DATABASE = "inco"
MSSQL_USERNAME = "SA"
MSSQL_PASSWORD = "incoroutine1!"


current_date = datetime.now().strftime("%Y%m%d")
# current_date = datetime(2024, 3, 11).strftime("%Y%m%d")
previous_date = (datetime.now() - timedelta(days=1)).strftime("%Y%m%d")
# previous_date = (datetime(2024, 3, 11) - timedelta(days=1)).strftime("%Y%m%d")

log_directory = "/logs"
temp_file = os.path.join(log_directory, f"{current_date}_data.csv")


def parse_and_format_datetime(datetime_str, format_str):
    return datetime.strptime(
        datetime_str, "%Y%m%d%H%M%S").strftime(format_str) if datetime_str else ''


def main():
    # 로그 디렉토리 생성
    if not os.path.exists(log_directory):
        os.makedirs(log_directory)

    try:
        print(f"{datetime.now()} 일자 데이터 추출 시작")

        # MSSQL 접속 & 데이터 추출
        # sql_query = f"SELECT * FROM [dbo].[SECOM_WORKHISTORY]"
        sql_query = f"SELECT * FROM [dbo].[SECOM_WORKHISTORY] WHERE WorkDate \
                    LIKE '{current_date}%' OR WorkDate LIKE '{previous_date}%'"

        mssql_conn_string = (
            f"DRIVER={{ODBC Driver 17 for SQL Server}};"
            f"SERVER={MSSQL_SERVER},{MSSQL_PORT};"
            f"DATABASE={MSSQL_DATABASE};"
            f"UID={MSSQL_USERNAME};"
            f"PWD={MSSQL_PASSWORD}"
        )
        mssql_conn = pyodbc.connect(mssql_conn_string)

        cursor = mssql_conn.cursor()
        cursor.execute(sql_query)

        with open(temp_file, 'w', newline='', encoding='utf-8') as file:
            csv_writer = csv.writer(file)
            csv_writer.writerow(["username", "work_date", "arrived_at", "left_at"])

            for row in cursor:
                fullname, work_date, wstime, wctime = map(
                    str, [row[4], row[0], row[20], row[21]]
                )

                work_date = datetime.strptime(work_date, '%Y%m%d').strftime('%Y-%m-%d')
                arrived_at = parse_and_format_datetime(wstime, "%H:%M:%S")
                left_at = parse_and_format_datetime(wctime, "%H:%M:%S")

                csv_writer.writerow([
                    fullname,
                    work_date or '',
                    arrived_at or '',
                    left_at or ''
                ])

        cursor.close()
        mssql_conn.close()

        try:
            api_endpoint = 'http://django:13200/api/commute/upload'

            with open(temp_file, 'rb') as file:
                files = {'file': (f"{current_date}_data.csv", file, 'text/csv')}
                response = requests.post(api_endpoint, files=files)

            if response.status_code == 200:
                print(f"CSV file successfully sent to the API endpoint: {api_endpoint}")
            else:
                print(f"Failed to send CSV file. Response status code: {response.status_code}, Response content: {response.content}")
        except Exception as e:
            print(e)
            pass

        print(f"{datetime.now()} 일자 데이터 전송 완료\n")
    except Exception as e:
        print(f"{datetime.now()} 일자 작업 중 오류가 발생하였습니다 : {e}")


if __name__ == "__main__":
    main()
