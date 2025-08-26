#!/usr/bin/env bash
set -e -x

# scrape docs from website
cd $1
rm *.htm* || true
wget -e robots=off -nd -r --convert-links -H -D aphassets.blob.core.windows.net,brailleblaster.org,dev.brailleblaster.org --level=inf -A html,jpg,jpeg,png,gif,PNG,JPG https://dev.brailleblaster.org/docs/manual/manualV2_1.html
