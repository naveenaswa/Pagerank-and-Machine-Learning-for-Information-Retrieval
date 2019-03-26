import collections
from pprint import pprint
from random import randint
import math
import sys
import time

# generate random value between two integers
def randomGen(rand1,rand2):
    if rand1 > rand2:
        return(randint(rand2,rand1))
    else:
        return(randint(rand1,rand2))
		
# get the count of relevant URLs from qrelHash and store
def getRelevantCount(QIDRelevanceHash):
    # keep the count of number of relevant docs
    # in the QIDRelevanceHash  
    if QID in QIDRelevanceHash.keys():
        QIDRelevanceHash[QID] += relevance
    else:
        QIDRelevanceHash[QID] = relevance
    return QIDRelevanceHash

# build the Relevance vector to calculate the nDCG value.
def getRelevanceVector(QIDRelevanceVectorHash):
    # assign the actual relevance to the QIDRelevanceVectorHash
    # so that this can be used for the nDCG calculation
    if QID in QIDRelevanceVectorHash.keys():
        QIDRelevanceVectorHash[QID].append(relevanceDCG)
    else:
        QIDRelevanceVectorHash[QID] = [relevanceDCG]
    return QIDRelevanceVectorHash


# ----------------------------------------------------------------------------------------------------------------#
# load the scores data from the scorefile into 
# the scoreHash
scoreFilePath = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\query_scores.txt",'r')
# scoreFilePath = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\ScoreOkapiBM25.txt",'r')
# structure: <QID, List<DOCID>>
scoresHash = collections.OrderedDict()

# get every line in the scoreFile split each line
# and assigne the corresponding data to the QID and URL list
for line in scoreFilePath:
    scoreItems = line.split()
# QID URL URL URL 
    QID = scoreItems[0]
    URL = scoreItems[2]
# append the URL for the same QID
# else insert it as new URL
    if QID in scoresHash.keys():
        scoresHash[QID].append(URL)
    else:
        scoresHash[QID] = [URL] 
# close the file after the file usage.
scoreFilePath.close()

#------------------------------------------------------------------------------------------------------------------#
# load the qrel data from the qrelfile into 
# the qrelHash
qrelFilePath = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\qrel_main.txt",'r')
# qrelFilePath = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\qrels.adhoc.51-100.AP89.txt",'r')
# structure: <QID, <URL, randomRelevance>>
qrelHash = collections.OrderedDict()

# structure: <QID, relevanceVector>
QIDRelevanceVectorHash = collections.OrderedDict()

# structure: <URL, count>
QIDRelevanceHash = collections.OrderedDict()


# for each line in the qrelfile split each line and
# assign the corresponding data to the QID and document properties
for line in qrelFilePath:
    qrelItems = line.split()
# QID AID URL RELEVANCE
    QID = qrelItems[0]
    AID = qrelItems[1]
    URL = qrelItems[2]
    relevance = int(qrelItems[3])
    relevanceDCG = relevance

    # since relevance is 0,1 and 2 
    if relevance > 0:
        relevance = 1
	
# if the QID is already present in the qrelHash
# update the qrelHash with the updateDocHash
    if QID in qrelHash.keys():
        updateDocHash = collections.OrderedDict()
        updateDocHash = qrelHash[QID]
        if URL in updateDocHash.keys():
            prevRelevance = updateDocHash[URL]
            updateDocHash[URL] = randomGen(prevRelevance, relevance)
        else:
            updateDocHash[URL] = relevance
        qrelHash[QID] = updateDocHash

# else insert the docid and the relevance to the
# newly created entry by the insertHash
    else:
        qrelHash[QID] = collections.OrderedDict()
        insertDocHash = collections.OrderedDict()
        insertDocHash[URL] = relevance
        qrelHash[QID] = insertDocHash

# get the count of relevant URLs from qrelHash and store
    getRelevantCount(QIDRelevanceHash)

# build the relevance vector for all the QIDs
    getRelevanceVector(QIDRelevanceVectorHash)
	
		
# close qrelFile after extracting the data
qrelFilePath.close()

# qrelHash is now built
# QIDRelevanceHash is also built


# ----------------------------------------------------------------------------------------------------------------#
# Calculate Recall and Precision values

# structure: <QID, List<recall1, recall2,....,recall1000>
# for every QID this contains the recall value at that URL.
recallHash = collections.OrderedDict()

# structure: <QID, List<precision1, precision2,....,precision1000>
# for every QID this contains the precision value at the URL.
precisionHash = collections.OrderedDict()

# structure: <QID, averagePrecisionValue>
# for every QID this contains the average precision.
avgPrecHash = collections.OrderedDict()

# structure: <cutoff, sumPrecValue>
# contains the cutoff as key and overall sum of precision at that cutoff
sumPrecAllQIDCutoffs = dict()

# structure: <cutoff, sumPrecValue>
# contains the cutoff as key and overall sum of recall at that cutoff
sumRecallAllQIDCutoffs = dict()

# contains the binary vector of relevance for each QID
# to use in the nDCG calculation
# structure: <QID, relevanceVector>
rVector = dict()

# contains sum of all the average precision of all QID
sumAvgPrec = 0

# contains sum of rPrecision of all the QIDs
sumRprec = 0

# contains the sum of recalls values for all QIDS
sumPrecAtRecalls = []

# recall values
recallValueList = [0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]

#cutoff values
cutoffValueList = [4,9,19,49,99]

# for each QID in the scoresHash compute the below
for QID in scoresHash.keys():
    # number of retrevied document count
    numRetreived=0
    # number of relevant retreived document count
    numRelRetreived = 0
    # sum of precision for QID
    sumPrecision = 0

    # contains the relevance values for the particualr QID
    rBinaryArray = []
	
    # for each URL in the scoreHash List
    for URL in scoresHash[QID]:
        numRetreived += 1
        
	# build the R binary array for particular QID
        if QID in qrelHash.keys():
            if URL in qrelHash[QID].keys():
		# build the rVector array for the particular QID
                rBinaryArray.append(qrelHash[QID][URL])
		# if we get a relevant URL add this to sumPrecision
                if qrelHash[QID][URL] >= 1:
                    sumPrecision +=  (1 + numRelRetreived)/numRetreived
                    numRelRetreived += 1

        # precision for the paticular QID @k URL
        precisionAtK = numRelRetreived/numRetreived
	# insert precision@k value to the precisionHash which contains
	# precision values @k URL
        if QID in precisionHash.keys():
            precisionHash[QID].append(precisionAtK)
        else:
            precisionHash[QID] = [precisionAtK]
                    
	# recall for the paticular QID @k URL
        recallAtK = numRelRetreived/QIDRelevanceHash[QID]
        # insert recall@k value to the recallHash which contains
	# recall values @k URL
        if QID in recallHash.keys():
            recallHash[QID].append(recallAtK)
        else:
            recallHash[QID] = [recallAtK]

    # insert the rBinaryArray into the rVector for the particular QID
    rVector[QID] = rBinaryArray

    # get the values of the precision and recall at the URLs
    # 5, 10, 20, 50, 100.
    for cutoff in cutoffValueList:    
	# calculate sum of all the precision values for QIDs @k
        if cutoff in sumPrecAllQIDCutoffs.keys():			
            sumPrecAllQIDCutoffs[cutoff] += precisionHash[QID][cutoff]
        else:
            sumPrecAllQIDCutoffs[cutoff] = precisionHash[QID][cutoff]
            
	# calculate sum of all the precision values for QIDs @k	
        if cutoff in sumRecallAllQIDCutoffs.keys():
            sumRecallAllQIDCutoffs[cutoff] += recallHash[QID][cutoff]
        else:
            sumRecallAllQIDCutoffs[cutoff] = recallHash[QID][cutoff]
        

    # assigning the value of number of relevant URL for that QID
    numRel = QIDRelevanceHash[QID]
	
    # average precision for the particular QID 
    avgPrecision = sumPrecision/numRel
    # insert the average precision of each QID into the average precision hash
    avgPrecHash[QID] = avgPrecision

    # sum of average precision over all queries.
    sumAvgPrec += avgPrecision

    # sum of average recall over all queries.	
    sumRecall = numRelRetreived/numRel
	
    for cutoff in cutoffValueList:
        print("Precision Value at ",cutoff+1," : ",precisionHash[QID][cutoff])
        
    for cutoff in cutoffValueList:
        print("Recall Value at ",cutoff+1," : ",recallHash[QID][cutoff])
        
    fMeasureAtK = (2 * precisionHash[QID][cutoff] * recallHash[QID][cutoff])/(precisionHash[QID][cutoff] + recallHash[QID][cutoff])
    for cutoff in cutoffValueList:
        print("F-Measure Value at ",cutoff+1," : ",fMeasureAtK)


    # for a number in range till 1000 assign the value till 1000th element
    # so that even if the number of relevant document is less than 1000 we
    # have 1000 values for consistency.
    for num in range(numRetreived+1,1001):
        precisionHash[QID].append(numRetreived/num)
        recallHash[QID].append(sumRecall)

    # rPrecision at the point where precision@numRel
    if numRel > numRetreived:
        rPrecisionValue = numRelRetreived/numRel
    else:
        rPrecisionValue = precisionHash[QID][numRel-1]
	
    print ("R-precision for query id ",QID,"is ",rPrecisionValue)
            
    # sum of rPrecisions over all the QIDs
    sumRprec += rPrecisionValue

 #--------------------------------------------------------------------------
 # implementing the interpolated results
 # precision list is assigned to precList

    
    precList = precisionHash[QID]
    max_precision = 0;
    for num in range(999,0):
        if precList[num] > max_precision:
            max_precision = precList[num]
        else:
            precList[num] = max_precision
    # assign back the precList to the corresponding precisionHash
    precisionHash[QID] = precList

    # list containing the precision at recalls
    precAtRecalls = []
    count = 0
    recallList = recallHash[QID]
    precList = precisionHash[QID]
    # for the recallvalues upadate the precAtRecalls
    for recall in recallValueList:
        while count < 1000 and recallList[count] < recall:
            count += 1
        if count < 1000:
            precAtRecalls.append(precList[count])
        else:
            precAtRecalls.append(0)
    # calculate the sum precision at recall values 
    for num in range(0,len(recallValueList)):
        if num < len(sumPrecAtRecalls):
            sumPrecAtRecalls[num] += precAtRecalls[num]
        else:
            sumPrecAtRecalls.append(precAtRecalls[num])

    print("Interpolated value for query:", QID)
    for num in range(0,len(recallValueList)):
        print(num, " : ",precAtRecalls[num])
        
    

     

# ----------------------------------------------------------------------------------------------------------------#
# Calculate the nDCG value.

# rVector for each QID
rVectorForQID = []
# nDCG hash to hold the values for each QID
nDCGHash = dict()
# sum of nDCG for all QID
sumOfnDCG = 0
DCGCount = 0

# Calculate the nDCG value for each QID
for QID in QIDRelevanceVectorHash.keys():
    rVectorForQID = QIDRelevanceVectorHash[QID]
    DCG = rVectorForQID[0]
    for num in range(1,len(rVectorForQID)):
        DCG += (rVectorForQID[num]/math.log(num+1))
    # sort the nDCG values.
    rVectorForQID.sort(reverse=True)    
    sortedDCG = rVectorForQID[0]
    if sortedDCG>0:
        for num in range(1,len(rVectorForQID)):
            sortedDCG += (rVectorForQID[num]/math.log(num+1)) 
        nDCG = DCG/sortedDCG
    else:
        nDCG = 0
    nDCGHash[QID] = nDCG


# calculate nDCG for all the QIDs
for QID in nDCGHash.keys():
    sumOfnDCG += nDCGHash[QID]
    DCGCount += 1

# average nDCG value of all QIDs
avgnDCG = sumOfnDCG/DCGCount    

# ----------------------------------------------------------------------------------------------------------------#
# print all the values.
avgPrecAtCutoffs = dict()
avgRecallAtCutoffs = dict()
avgFmeasureAtCutoff = dict()

# print all the values of the cutoffs

print ("Interpolated precision values average")
avgPrecAtRecalls = []
for i in range(0,len(recallValueList)):
    avgPrecAtRecalls.append(sumPrecAtRecalls[i]/len(scoresHash.keys()))
    print (i,": ",avgPrecAtRecalls[i]) 

print("Precision values for queries")
for cutoff in cutoffValueList:
    avgPrecAtCutoffs[cutoff] = sumPrecAllQIDCutoffs[cutoff]/len(scoresHash.keys())
    print ("Precision value at ",cutoff+1," docs (avg over all queries): ",avgPrecAtCutoffs[cutoff])

print("Recall values for queries")
for cutoff in cutoffValueList:
    avgRecallAtCutoffs[cutoff] = sumRecallAllQIDCutoffs[cutoff]/len(scoresHash.keys())
    print ("Recall value at ",cutoff+1," docs (avg over all queries): ",avgRecallAtCutoffs[cutoff])

print("F-Measure values for queries")
for cutoff in cutoffValueList:
    avgFmeasureAtCutoff[cutoff] = (2 * avgPrecAtCutoffs[cutoff] * avgRecallAtCutoffs[cutoff])/(avgPrecAtCutoffs[cutoff]+avgRecallAtCutoffs[cutoff])
    print ("F-measure value at ",cutoff+1," docs (avg over all queries): ",avgFmeasureAtCutoff[cutoff])
    
totalAvgPrecision = sumAvgPrec/len(scoresHash.keys())
print ("Average Precision: ",totalAvgPrecision)

totalAvgrPrecision = sumRprec/len(scoresHash.keys())
print ("R-Precision: ",totalAvgrPrecision)

print ("nDCG: ",avgnDCG)
