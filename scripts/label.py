# Script permettant de transférer les 5 plus grosses entropies de pool dans train
import pandas as pd
import numpy as np
from pathlib import Path

mainDir = Path(__file__).parent.parent

train = pd.read_csv(str(mainDir) + '/shared/train.csv')
pool = pd.read_csv(str(mainDir) + '/shared/pool.csv')

label = open(str(mainDir)+"/shared/label.txt", "r")
path = open(str(mainDir)+"/shared/topEntropy.txt", "r")

labels = label.readlines()
paths = path.readlines()[1:]

for i in range(len(labels)-1):
    labels[i] = labels[i][:-1]
    
for i in range(len(paths)):
    paths[i] = paths[i][:-1]

labels={'path': paths,
        'label': labels}

data = pd.DataFrame(labels)

train = pd.concat([train, data])

for path in paths:
    pool.drop(np.where(pool['path'] == path)[0][0], inplace = True )

train.to_csv(str(mainDir)+'/shared/train.csv', index=False)
pool.to_csv(str(mainDir)+'/shared/pool.csv', index=False)