#!/usr/bin/env bash

curl -XPOST http://localhost:3000/api/v1/tasks/ --header "Content-Type:application/json"  -d '
{
    "content" : "task 1 is super cool. We should all start doing it",
    "assignee" : "John"
} '