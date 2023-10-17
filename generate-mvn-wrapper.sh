#!/bin/bash

##
# Copyright Mickael Jeanroy - All Rights Reserved
# Unauthorized copying of this file, via any medium is strictly prohibited
# Proprietary and confidential
# Written by Mickael Jeanroy <mickael.jeanroy@gmail.com>
##

# mvn wrapper:wrapper

function clean_mvnw {
  local GREEN="\033[0;32m"
  local RESET_COLORS="\033[0m"

  echo -e "⌛ ${GREEN}Cleaning mvnw...${RESET_COLORS}"
  rm -f mvnw
  rm -f mvnw.cmd
  rm -rf .mvn
  echo -e "✅ ${GREEN}Cleaning done...${RESET_COLORS}"
  echo ""
}

function generate_mvnw {
  local GREEN="\033[0;32m"
  local RESET_COLORS="\033[0m"

  echo -e "⌛ ${GREEN}Cleaning mvnw...${RESET_COLORS}"
  mvn wrapper:wrapper
  echo -e "✅ ${GREEN}Cleaning done...${RESET_COLORS}"
  echo ""
}

clean_mvnw
generate_mvnw
