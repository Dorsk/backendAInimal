#Active learning : ajout des données en fonction de l'entropie
import pandas as pd
import numpy as np
import torch
from pathlib import Path

def entropy(model, loaders):
    mainDir = Path(__file__).parent.parent
    pool = pd.read_csv(str(mainDir)+"/shared/pool.csv")
    entropy_list = torch.empty(0)

    for x, y in loaders:
        output = torch.nn.functional.softmax(model(x), dim=1)

        entrop = (-(output+10**-7)*torch.log(output+10**-7)).sum(1)
        entropy_list = torch.cat((entropy_list, entrop), 0)

    pool['entropy'] = entropy_list.tolist()
    top5 = pool.sort_values(by = 'entropy', ascending=False).head()
    top5['path'].to_csv(str(mainDir)+'/shared/topEntropy.txt', sep=' ', index=False) 