#!/usr/bin/python3
# -*- coding: utf-8 -*- 
# Script permettant de créer deux csv pool.csv et train.csv
# Pool est l'ensemble des données non labélisé on a donc le chemine et l'entropie
# Train est l'ensemble des données labélisé on a donc le chemin et le label
# Entrée attendue : chemin du dossier des images

import sys
from os import listdir
from os.path import isfile, join
import pandas as pd
import numpy as np
from pathlib import Path

try :
    path = sys.argv[1]
except :
    print("No argument where one is required")
 
 
mainDir = Path(__file__).parent.parent
files = [path + "/" + f for f in listdir(path) if isfile(join(path, f))]
entropy = np.zeros(len(files))

data = {'path': files,
        'entropy': entropy}

pool = pd.DataFrame(data)

firstCSV = "/shared/pool.csv" 
print( "-- Start : "+ str(mainDir) + str(firstCSV))
pool.to_csv(str(mainDir) + str(firstCSV), index=False)
print( "-- End : "+ str(mainDir) + str(firstCSV))

data = {'path': [],
        'label': []}

train = pd.DataFrame(data)

secondCSV = "/shared/train.csv"
print( "-- Start : "+ str(mainDir) + str(secondCSV))
train.to_csv(str(mainDir) + str(secondCSV), index=False)
print( "-- End : "+ str(mainDir) + str(secondCSV))
