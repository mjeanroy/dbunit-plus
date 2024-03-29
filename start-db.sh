#!/bin/bash

##
# The MIT License (MIT)
#
# Copyright (c) 2015-2023 Mickael Jeanroy
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
##

RED="\033[0;31m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
RESET_COLORS="\033[0m"

function start_mssql_server {
  local version=$1

  if [ "$version" == "" ]; then
    version="2017"
  fi

  local port="1433"
  local password="Azerty123!"
  local image="mcr.microsoft.com/mssql/server:${version}-latest"

  echo -e "${GREEN}Pulling image: ${image}${RESET_COLORS}"
  docker pull "${image}"

  echo ""
  echo -e "${GREEN}Starting MsSQL server${RESET_COLORS}"
  echo -e "${YELLOW}  Port: ${port}${RESET_COLORS}"
  echo -e "${YELLOW}  Password: ${password}${RESET_COLORS}"
  echo ""

  docker run --rm \
    -p "${port}:1433" \
    -e 'ACCEPT_EULA=Y' \
    -e "SA_PASSWORD=${password}" \
    "${image}"
}

function start_mysql {
  local version=$1

  if [ "$version" == "" ]; then
    version="5.7"
  fi

  local port="3306"
  local db_name="test"
  local user="test"
  local password="Azerty123!"
  local image="mysql:${version}"

  echo -e "${GREEN}Pulling image: ${image}${RESET_COLORS}"
  docker pull ${image}

  echo ""
  echo -e "${GREEN}Starting MySQL server${RESET_COLORS}"
  echo -e "${YELLOW}  Database: ${db_name}${RESET_COLORS}"
  echo -e "${YELLOW}  Port: ${port}${RESET_COLORS}"
  echo -e "${YELLOW}  User: ${user}${RESET_COLORS}"
  echo -e "${YELLOW}  Password: ${password}${RESET_COLORS}"
  echo ""

  docker run --rm \
    -p "${port}:3306" \
    -e "MYSQL_ROOT_PASSWORD=${password}" \
    -e "MYSQL_DATABASE=${db_name}" \
    -e "MYSQL_USER=${user}" \
    -e "MYSQL_PASSWORD=${password}" \
    "${image}"
}

function start_mariadb {
  local version=$1

  if [ "$version" == "" ]; then
    version="10"
  fi

  local port="3306"
  local db_name="test"
  local user="test"
  local password="Azerty123!"
  local image="mariadb:${version}"

  echo -e "${GREEN}Pulling image: ${image}${RESET_COLORS}"
  docker pull ${image}

  echo ""
  echo -e "${GREEN}Starting mariadb server${RESET_COLORS}"
  echo -e "${YELLOW}  Database: ${db_name}${RESET_COLORS}"
  echo -e "${YELLOW}  Port: ${port}${RESET_COLORS}"
  echo -e "${YELLOW}  User: ${user}${RESET_COLORS}"
  echo -e "${YELLOW}  Password: ${password}${RESET_COLORS}"
  echo ""

  docker run --rm \
    -p "${port}:3306" \
    -e "MARIADB_ROOT_PASSWORD=${password}" \
    -e "MARIADB_DATABASE=${db_name}" \
    -e "MARIADB_USER=${user}" \
    -e "MARIADB_PASSWORD=${password}" \
    "${image}"
}

function start_postgres {
  local version=$1

  if [ "$version" == "" ]; then
    version="13"
  fi

  local db_name="test"
  local port="5432"
  local user="test"
  local password="Azerty123!"
  local image="postgres:${version}-alpine"

  echo -e "${GREEN}Pulling image: ${image}${RESET_COLORS}"
  docker pull ${image}

  echo ""
  echo -e "${GREEN}Starting Postgres${RESET_COLORS}"
  echo -e "${YELLOW}  Database: ${db_name}${RESET_COLORS}"
  echo -e "${YELLOW}  Port: ${port}${RESET_COLORS}"
  echo -e "${YELLOW}  User: ${user}${RESET_COLORS}"
  echo -e "${YELLOW}  Password: ${password}${RESET_COLORS}"
  echo ""

  docker run --rm \
    -p "${port}:5432" \
    -e "POSTGRES_DB=${db_name}" \
    -e "POSTGRES_USER=${app_user}" \
    -e "POSTGRES_PASSWORD=${password}" \
    "${image}"
}

function start_oracle {
  local db_name="test"
  local port="1521"
  local app_user="test"
  local password="Azerty123!"
  local image="gvenzl/oracle-xe:21-slim-faststart"

  echo -e "${GREEN}Pulling image: ${image}${RESET_COLORS}"
  docker pull ${image}

  echo ""
  echo -e "${GREEN}Starting OracleXE server${RESET_COLORS}"
  echo -e "${YELLOW}  Database: ${db_name}${RESET_COLORS}"
  echo -e "${YELLOW}  Port: ${port}${RESET_COLORS}"
  echo -e "${YELLOW}  System user: system${RESET_COLORS}"
  echo -e "${YELLOW}  App user: ${app_user}${RESET_COLORS}"
  echo -e "${YELLOW}  Password: ${password}${RESET_COLORS}"
  echo ""

  docker run --rm \
    -p "${port}:1521" \
    -e "ORACLE_DATABASE=${db_name}" \
    -e "APP_USER=${app_user}" \
    -e "APP_USER_PASSWORD=${password}" \
    -e "ORACLE_PASSWORD=${password}" \
    "${image}"
}

db_engine=$1

if [ "$db_engine" == "mssql" ]; then
  start_mssql_server $2
elif [ "$db_engine" == "mysql" ]; then
  start_mysql $2
elif [ "$db_engine" == "mariadb" ]; then
  start_mariadb $2
elif [ "$db_engine" == "postgres" ]; then
  start_postgres $2
elif [ "$db_engine" == "pg" ]; then
  start_postgres $2
elif [ "$db_engine" == "oracle" ]; then
  start_oracle
else
  echo -e "${RED}Error: Unknown database engine: $db_engine${RESET_COLORS}"
  exit -1
fi
