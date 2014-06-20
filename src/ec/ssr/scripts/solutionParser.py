# -*- coding: utf-8 -*-
"""
Created on Thu Jun 12 14:37:48 2014

@author: luiz
"""
import numpy as np
from pylab import *
from matplotlib.backends.backend_pdf import PdfPages

numZeroDiv = 0

def readSolutionFile(path):
    f = open(path, 'r')
    lines = f.readlines()
    solutions = []
    b_startNewSolution = False
    for l in lines:
        if "Execution" in l:
            b_startNewSolution = True
        elif b_startNewSolution:
            newSolution = Solution()
            solutions.append(newSolution)
            newSolution.addNewFunction(l)
            b_startNewSolution = False
        elif not l.strip() == '':
            newSolution.addNewFunction(l)
    return solutions
    
def plotSolution(solutionPath, testPath, trainPath, maxIteration = 0, execution = 0):
    solutions = readSolutionFile(solutionPath + "/solution.txt") 
    firstSol = solutions[execution]
    global numZeroDiv
    test = np.genfromtxt(testPath, delimiter='\t', skip_header=2)
    train = np.genfromtxt(trainPath, delimiter='\t', skip_header=2)
    pp = PdfPages('/tmp/' + str(execution).zfill(2) + '_functionOutputs.pdf')
    # Saida
    f = open('/tmp/nroDivisoes.txt', 'a')
    #f.write("Execution " + str(execution).zfill(2) + "\n")
    separator = ""
    if maxIteration == 0:
        maxIteration = size(firstSol.getFunctions())
    for i in range(0,maxIteration):
        # Contagem do numero de divsoes por 0
        numZeroDiv = 0
        x1 = train[:,0]
        eval(firstSol.getFunctions()[i])
        f.write(separator + str(numZeroDiv))
        separator = ","
        # ===================================                
        fig = figure()        
        x1 = test[:,0]
        y = test[:,1]        
        y1 = eval(firstSol.getFunctions()[i])        
        scatter(train[:,0],train[:,1], color='blue')
        plot(x1,y, color='blue')
        plot(x1, y1, color='red')
        ylim([-1,1])
        pp.savefig(fig);
        close(fig)
    pp.close()
    f.write("\n")
    f.close()
                            
def div(a,b):
    # Contagem do nro de divisoes por 0
    global numZeroDiv    
    if isinstance(a, (int, long, float)):
        if isinstance(b, (int, long, float)):
            if b == 0: 
                numZeroDiv += 1
                return 1
            else: return a/b
        else:
            a = np.repeat(a,size(b))
    else:
        if isinstance(b, (int, long, float)):
            b = np.repeat(b,size(a))
    c = np.repeat(1,size(a))
    for i in range(0,size(a)):
        if b[i] != 0:
            c[i] = a[i]/b[i]
        else:
            numZeroDiv += 1
    return c
            
class Solution:
    def __init__(self):
        self.functions = []
    def addNewFunction(self, str_function):
        self.functions.append(str_function)
    def getFunctions(self):
        return self.functions