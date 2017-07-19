#!/bin/bash

git add .
echo "Enter Commit Message"
read commit
git commit -m "$commit"
git push origin master
