#!/usr/bin/python

import os
import re

def parse_time(tstr):
    mind = tstr.find('m')
    minutes = float(tstr[:mind])
    seconds = float(tstr[mind+1:-1])
    return (minutes*60)+seconds

dirs = os.listdir('.')
cluster_dirs = []
standalone_dirs = []
map_3_dirs = []
map_4_dirs = []
for d in dirs:
    if d.startswith('cluster'):
        cluster_dirs.append(d)
    elif d.startswith('standalone'):
        standalone_dirs.append(d)
    elif d.startswith('3'):
        map_3_dirs.append(d)
    elif d.startswith('4'):
        map_4_dirs.append(d)

aes = {'files': [], 'encrypt': {}, 'decrypt': {}}
des = {'files': [], 'encrypt': {}, 'decrypt': {}}
desede = {'files': [], 'encrypt': {}, 'decrypt': {}}

i=1
for d in cluster_dirs:
    files = os.listdir(d)
    for f in files:
        if f.startswith('AES'):
            aes['files'].append("%s/%s" % (d, f))
        elif f.startswith('DESede'):
            desede['files'].append("%s/%s" % (d, f))
        else:
            des['files'].append("%s/%s" % (d, f))

for algo in [aes, des, desede]:
    flist = algo['files']
    for f in flist:
        size = re.search('\w+-(\d+[GM]B).out', f).group(1)
        flist = open(f)
        r = 0
        for line in flist:
            if line.startswith('real'):
                time_str = re.search('real\s*(.*)', line).group(1)
                time = parse_time(time_str)
                if r == 0:
                    try:
                        algo['encrypt'][size] += time
                    except KeyError:
                        algo['encrypt'][size] = time
                    r += 1
                else:
                    try:
                        algo['decrypt'][size] += time
                    except KeyError:
                        algo['decrypt'][size] = time
    
    for size in algo['encrypt'].keys():
        algo['encrypt'][size] /= 3

    for size in algo['decrypt'].keys():
        algo['decrypt'][size] /= 3

map_3_aes = {'files': [], 'encrypt': {}, 'decrypt': {}}
map_3_des = {'files': [], 'encrypt': {}, 'decrypt': {}}
map_3_desede = {'files': [], 'encrypt': {}, 'decrypt': {}}

i=1
for d in map_3_dirs:
    files = os.listdir(d)
    for f in files:
        if f.startswith('AES'):
            map_3_aes['files'].append("%s/%s" % (d, f))
        elif f.startswith('DESede'):
            map_3_desede['files'].append("%s/%s" % (d, f))
        else:
            map_3_des['files'].append("%s/%s" % (d, f))

for algo in [map_3_aes, map_3_des, map_3_desede]:
    flist = algo['files']
    for f in flist:
        size = re.search('\w+-(\d+[GM]B).out', f).group(1)
        flist = open(f)
        r = 0
        for line in flist:
            if line.startswith('real'):
                time_str = re.search('real\s*(.*)', line).group(1)
                time = parse_time(time_str)
                if r == 0:
                    try:
                        algo['encrypt'][size] += time
                    except KeyError:
                        algo['encrypt'][size] = time
                    r += 1
                else:
                    try:
                        algo['decrypt'][size] += time
                    except KeyError:
                        algo['decrypt'][size] = time
    
    for size in algo['encrypt'].keys():
        algo['encrypt'][size] /= 3

    for size in algo['decrypt'].keys():
        algo['decrypt'][size] /= 3

map_4_aes = {'files': [], 'encrypt': {}, 'decrypt': {}}
map_4_des = {'files': [], 'encrypt': {}, 'decrypt': {}}
map_4_desede = {'files': [], 'encrypt': {}, 'decrypt': {}}

i=1
for d in map_4_dirs:
    files = os.listdir(d)
    for f in files:
        if f.startswith('AES'):
            map_4_aes['files'].append("%s/%s" % (d, f))
        elif f.startswith('DESede'):
            map_4_desede['files'].append("%s/%s" % (d, f))
        else:
            map_4_des['files'].append("%s/%s" % (d, f))

for algo in [map_4_aes, map_4_des, map_4_desede]:
    flist = algo['files']
    for f in flist:
        size = re.search('\w+-(\d+[GM]B).out', f).group(1)
        flist = open(f)
        r = 0
        for line in flist:
            if line.startswith('real'):
                time_str = re.search('real\s*(.*)', line).group(1)
                time = parse_time(time_str)
                if r == 0:
                    try:
                        algo['encrypt'][size] += time
                    except KeyError:
                        algo['encrypt'][size] = time
                    r += 1
                else:
                    try:
                        algo['decrypt'][size] += time
                    except KeyError:
                        algo['decrypt'][size] = time
    
    for size in algo['encrypt'].keys():
        algo['encrypt'][size] /= 3

    for size in algo['decrypt'].keys():
        algo['decrypt'][size] /= 3

# cluster map2 vs map3 vs map4 for aes
f = open('../cluster-map-2-3-4-aes.csv', 'w+')
for algo in [aes, map_3_aes, map_4_aes]:
    line = ""
    times = algo['encrypt'].values()
    times.sort()
    for time in times:
        line += str(time) + ","
    line = line[:-1] + "\n"
    f.write(line)
f.flush()
f.close()

f = open('../cluster-map-2-3-4-des.csv', 'w+')
for algo in [des, map_3_des, map_4_des]:
    line = ""
    times = algo['encrypt'].values()
    times.sort()
    for time in times:
        line += str(time) + ","
    line = line[:-1] + "\n"
    f.write(line)
f.flush()
f.close()

f = open('../cluster-map-2-3-4-desede.csv', 'w+')
for algo in [desede, map_3_desede, map_4_desede]:
    line = ""
    times = algo['encrypt'].values()
    times.sort()
    for time in times:
        line += str(time) + ","
    line = line[:-1] + "\n"
    f.write(line)
f.flush()
f.close()

# generate a csv that looks like
# aestime1, aestime2, etc
# destime1, destime2, etc
# desede blah blah blah
cac = open('../cluster-all-ciphers.csv', 'w+')
for algo in [aes, des, desede]:
    line = ""
    times = algo['encrypt'].values()
    times.sort()
    for time in times:
        line += str(time) + ","
    line = line[:-1] + "\n"
    cac.write(line)
cac.flush()
cac.close()

# generate standalone csv
# aes
# des
# desede

s_aes = {'files': [], 'encrypt': {}, 'decrypt': {}}
s_des = {'files': [], 'encrypt': {}, 'decrypt': {}}
s_desede = {'files': [], 'encrypt': {}, 'decrypt': {}}

i=1
for d in standalone_dirs:
    files = os.listdir(d)
    for f in files:
        if f.startswith('AES'):
            s_aes['files'].append("%s/%s" % (d, f))
        elif f.startswith('DESede'):
            s_desede['files'].append("%s/%s" % (d, f))
        else:
            s_des['files'].append("%s/%s" % (d, f))

for algo in [s_aes, s_des, s_desede]:
    flist = algo['files']
    for f in flist:
        size = re.search('\w+-(\d+[GM]B).out', f).group(1)
        flist = open(f)
        r = 0
        for line in flist:
            if line.startswith('real'):
                time_str = re.search('real\s*(.*)', line).group(1)
                time = parse_time(time_str)
                if r == 0:
                    try:
                        algo['encrypt'][size] += time
                    except KeyError:
                        algo['encrypt'][size] = time
                    r += 1
                else:
                    try:
                        algo['decrypt'][size] += time
                    except KeyError:
                        algo['decrypt'][size] = time

    
# generate a csv that looks like
# aestime1, aestime2, etc
# destime1, destime2, etc
# desede blah blah blah
cac = open('../standalone-all-ciphers.csv', 'w+')
for algo in [s_aes, s_des, s_desede]:
    line = ""
    times = algo['encrypt'].values()
    times.sort()
    for time in times:
        line += str(time) + ","
    line = line[:-1] + "\n"
    cac.write(line)
cac.flush()
cac.close()

# generate a csv for each algorithm
# cluster v standalone

# cluster, cluster cluster
# standalone standal

f = open('../cluster-v-standalone-aes.csv', 'w+')
times = aes['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)

times = s_aes['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)
f.flush()
f.close()

#########################################################################


f = open('../cluster-v-standalone-des.csv', 'w+')
times = des['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)

times = s_des['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)
f.flush()
f.close()


###########################################################################

f = open('../cluster-v-standalone-desede.csv', 'w+')
times = desede['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)

times = s_desede['encrypt'].values()
times.sort()
s = ""
for t in times:
    s += str(t) + ","
s = s[:-1] + "\n"
f.write(s)
f.flush()
f.close()
