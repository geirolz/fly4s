#!/bin/sh
newtag="$(git describe --tags --abbrev=0)"
git log --no-merges --pretty=format:"- %s" HEAD..."$newtag" > changelog.md