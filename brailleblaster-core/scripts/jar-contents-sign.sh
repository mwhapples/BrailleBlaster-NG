while getopts s:e:j: flag
do
    case "${flag}" in
        s) IDENTITY=${OPTARG};;
        e) ENTITLEMENTS=${OPTARG};;
        j) JARFILE=${OPTARG};;
    esac
done
echo $IDENTITY
jar tf "${JARFILE}" | grep '\.so$\|\.dylib$\|\.jnilib$' > tmp-filelist.txt

while read f
do
    jar xf "${JARFILE}" $f
    codesign -s "$IDENTITY" --entitlements "${ENTITLEMENTS}" --timestamp --options runtime -f --deep -v $f
    jar uf "${JARFILE}" $f
    rm -rf $f
done < tmp-filelist.txt

rm -rf tmp-filelist.txt

