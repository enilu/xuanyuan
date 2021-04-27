#!/bin/bash

ps -ef|grep xuanyuan |grep -v grep | awk '{print $2}' | xargs kill -9
