#!/bin/bash

KEEP=
TEST_CONF=tests.conf

function usage {

    cat << EOF
Usage: $0 [-lk] [-c <conf_file>] <test name>
 -l (optional)     - Run tests against CryptTesting instead of Hadoop.
 -k (optional)     - Keep intermediate testing data.
 -c (optional)     - Use a given test configuration file.
 <test name>       - Make the test name something identifiable and human readable.
    
Example: $0 all-tests-static-ciphers-2
EOF
}

if [ -f /mirror/mpiu/james/env.sh ]
then
    . /mirror/mpiu/james/env.sh
fi

HADOOP=$HADOOP_DIR/bin/hadoop
CONF_DIR="configs"

test_name_index=1
while getopts "hlkc:" OPTION
do
    case $OPTION in
        c)
            TEST_CONF=$OPTARG
            ;;
        h)
            usage
            exit 1
            ;;
        l)
            LOCAL=1
            ;;
        k)
            KEEP=1
            ;;
    esac
    test_name_index=$OPTIND
done

if [ ! -f $TEST_CONF ]
then
    echo "Cannot open test configuration: $TEST_CONF"
    exit 1
fi

eval TN=\$$test_name_index

if [ "$TN" = "" ]
then
    usage
    exit 1
fi

if [ $LOCAL ]
then
    TESTNAME=local-$TN
    TARGET="local"
    OUTPUT_DIR=test_output/local-$TN
    mkdir -p $OUTPUT_DIR
else
    TESTNAME=$TN
    TARGET="hadoop"
    OUTPUT_DIR=test_output/$TN
    mkdir -p $OUTPUT_DIR
fi

# $1 = algorithm
# $2 = size
function cleanup_test {
    if [ "$TARGET" = "hadoop" ]
    then
        T=$1-$2
        time $HADOOP fs -rmr $T-enc 
        time $HADOOP fs -rmr $T-dec
    elif [ "$TARGET" = "local" ]
    then
        rm -f Decrypted.txt
        rm -f Encrypted.txt
    fi
}

# $1 = algorithm
# $2 = size
function run_test {
    ALG=$1
    SIZE=$2
    TEST=$ALG-$SIZE
    OUT_FILE=$OUTPUT_DIR/$TEST.out
    touch $OUT_FILE
    
    echo >> $OUT_FILE 2>&1
    echo >> $OUT_FILE 2>&1
    date >> $OUT_FILE 2>&1
    echo "-------------- RUNNING TEST: $TEST --------------" >> $OUT_FILE 2>&1
    # Cleanup any crufty stuff that may be lying about
    cleanup_test $ALG $SIZE 
    if [ "$TARGET" = "hadoop" ]
    then
        CONFIG=$CONF_DIR/$TEST.xml
        ( time $HADOOP jar build/Secdoop* EncryptDriver -Dfileconf=$CONFIG ) >> $OUT_FILE 2>&1
        ( time $HADOOP jar build/Secdoop* DecryptDriver -Dfileconf=$CONFIG ) >> $OUT_FILE 2>&1
    elif [ "$TARGET" = "local" ]
    then
        ( time java -cp crypt-build/*.jar ${ALG}Encrypter inputs/${SIZE}.txt ) >> $OUT_FILE 2>&1
        ( time java -cp crypt-build/*.jar ${ALG}Decrypter inputs/${SIZE}.txt ) >> $OUT_FILE 2>&1
    fi
    echo "-------------- FINISHED TEST: $TEST --------------" >> $OUT_FILE 2>&1
    if [ ! $KEEP ]
    then
        cleanup_test $ALG $SIZE
    fi
}

algos=( `grep -v '^#' $TEST_CONF | head -1` )
sizes=( `grep -v '^#' $TEST_CONF | tail -1` )

for alg in ${algos[@]}
do
    for size in ${sizes[@]}
    do
        run_test $alg $size
    done
done

cat << EOF

====================================================================
 TEST RUN COMPLETE: $TESTNAME
====================================================================
EOF
