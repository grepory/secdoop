#!/usr/bin/python
# Script to generate files of various sizes.

import sys
from os.path import exists
from os import mkdir
import re
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-i", "--input-dir", dest="input_dir", default="inputs",
        help="Write input files to DIR.", metavar="DIR")
parser.add_option("-c", "--config-dir", dest="config_dir", default="configs",
        help="Write config files to DIR.", metavar="DIR")
parser.add_option("-t", "--testconf", dest="test_conf", default="tests.conf",
        help="Use FILE for test configuration.", metavar="FILE")
parser.add_option("-n", "--numlines", dest="num_lines", default=1,
        help="Set numLines to VALUE.", metavar="VALUE")

(options, args) = parser.parse_args()

input_dir=options.input_dir
config_dir=options.config_dir
test_conf=options.test_conf
num_lines=options.num_lines

def parse_algos(algos_str):
    alg_list = []
    for alg in algos_str.split():
        alg_list.append(alg)
    return alg_list

def parse_sizes(sizes_str):
    size_dict = {}
    for size in sizes_str.split():
        size_dict.update(parse_size(size))
    return size_dict

def parse_size(size_str):
    m = re.search("(\d+)([GM]B)", size_str)
    size = m.group(1)
    unit = m.group(2)
    if unit == "KB":
        factor = 1
    elif unit == "MB":
        factor = 1024
    elif unit == "GB":
        factor = 1024**2
    elif unit == "TB":
        factor = 1024**3
    size_dict = {}
    size_dict[size_str] = int(size)*factor
    return size_dict

def parse_conf(conf):
    tsizes = {}
    algos = []
    if not exists(conf):
        sys.exit('Cannot find configuration file %s' % conf)
    f = open(conf, 'r')
    parsed_algos = False
    for line in f.readlines():
        if line.startswith('#'):
            continue
        else:
            if not parsed_algos:
                algos = parse_algos(line)
                parsed_algos = True
            else:
                tsizes = parse_sizes(line)
    return (tsizes, algos)


def write_input_file(name, size):
    '''Write a file of given "name" and "size" where size is in KB.'''

    path = input_dir + "/" + name
    if not exists(path):
        f = open(path, "w")
        for n in xrange(size):
            index = str(n)
            f.write(index+":")
            f.write("A"*(1022-len(index)))
            f.write("\n")
        f.close()

def property_str(name, value):
    lines = []
    lines.append('    <property>\n')
    lines.append('        <name>%s</name>\n' % name)
    lines.append('        <value>%s</value>\n' % value)
    lines.append('    </property>\n')
    return lines

def write_config(name, input_file, encrypted_file, decrypted_file, algorithm):
    path = config_dir + "/" + name + ".xml"
    input_path = "inputs/" + input_file
    f = open(path, 'w')
    lines = []
    
    lines.append('<?xml version="1.0"?>\n')
    lines.append('<configuration>\n')
    lines += property_str("inputFile", input_path)
    lines += property_str("encryptedFile", encrypted_file)
    lines += property_str("decryptedFile", decrypted_file)
    lines += property_str("algorithm", algorithm)
    lines += property_str("numLines", num_lines)
    lines.append('</configuration>\n')
    
    f.writelines(lines)
    f.close()


if not exists(input_dir):
    mkdir(input_dir)

if not exists(config_dir):
    mkdir(config_dir)

testsizes = {}
algorithms = []
(testsizes, algorithms) = parse_conf(test_conf)

for (k,v) in testsizes.iteritems():
    filename = k+".txt"
    write_input_file(filename, v)
    for alg in algorithms:
        test_name = alg+"-"+k
        write_config(test_name, filename, test_name+"-enc", test_name+"-dec", alg)

