#!/usr/bin/env bash
set -e -x

# scrape docs from website
cd $1
rm *.htm* || true
wget -e robots=off -nd -r --convert-links -H -D aphassets.blob.core.windows.net,brailleblaster.org,dev.brailleblaster.org,aphtech.github.io --level=inf -A html,jpg,jpeg,png,gif,PNG,JPG https://aphtech.github.io/brailleblaster-docs/index.html
