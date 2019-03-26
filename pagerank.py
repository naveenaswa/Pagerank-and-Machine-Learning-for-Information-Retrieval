from pprint import pprint
import operator

# load file contents into the inlinkHash
filepath = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\wt2g_inlinks.txt","r")

# contains the content of the file into the inlink hash
# structure : <BaseURL, List<inlink1, inlink2,....,inlinkn>>
inlinkHash = dict()

# contains the number of outlinks of the particular Page
# sturcture : <BaseURL, outlinkCount>
outlinkHash = dict()

# contains the pageRank value of each page
# structure : <BaseURL, pageRankValue>
pageRankHash = dict()

#--------------------------------------------------------------------------------------#
# Load the file contents into the Hashes
# for each line in the file 
for line in filepath:
    lineItems = line.split()
    baseURL = lineItems[0]
    
	# inlinkList contains the list of inlinks for the particular Page
	# if particular line has one or more inlinks assign it to the inlinkList 
    inlinkList = []
    if len(lineItems) > 1:
        inlinkList = lineItems[1:]
    
	# assign back inlinkList to inlinkHash
    inlinkHash[baseURL] = inlinkList

	# for every URL/Page in the inlinkList add it to
	# outlinkHash and increment the number of times it repeats
	# gives the number of outlink for each Page.
    for inlink in inlinkList:
        if inlink in outlinkHash.keys():
            outlinkHash[inlink] += 1
        else:
            outlinkHash[inlink] = 1

	# populate the pageRankHash with the values in the lineItems
	# and assigned the value to be zero initially
    for baseURL in lineItems:
        pageRankHash[baseURL] = 0     

# close filepath
filepath.close()

#-----------------------------------------------------------------------------------#
# first iteration with the base value of 1/N
totalPages = len(pageRankHash.keys())      
dampingFactor = 0.85                            

# 1st iteration
for link in pageRankHash.keys():
    pageRankHash[link] = 1/totalPages


#----------------------------------------------------------------------------------#	
# Calculate page rank for all the pages
print ("Iteration: 1")
haltingMeasure = True
iteration = 1
while (haltingMeasure):

    iteration += 1
    haltingMeasure = False
    # for each baseURL in the pagerankHash
	# compute the pageRank
    for baseURL in pageRankHash.keys():
        prFactor = 0
        inlinks = inlinkHash[baseURL]
		# for each inlinks compute pageRank share
        for inlink in inlinks:
            prFactor += (pageRankHash[inlink]/outlinkHash[inlink])    
        prFactor = dampingFactor * prFactor
        prFactor = (1-dampingFactor)/totalPages + prFactor

		# compute the delta value for the previous value to the current value
		# if it is less than 0.00001 for all the pagerank then stop the iteration
        if abs(prFactor - pageRankHash[baseURL]) > 0.00001:
            haltingMeasure = True
            
        pageRankHash[baseURL] = prFactor
    print ("Iteration: ",iteration)	

# sort the pageRank Hash based on the pagerank of each entry
finalPageRank = sorted(pageRankHash.items(), key=operator.itemgetter(1))
finalPageRank.reverse()

# write the pageRank into a file
fileWrite = open("C:\\Users\\Naveen\\Desktop\\TrecEvalDemo\\pageRank.txt","a")
line = ""
for baseURL, score in finalPageRank:
    line += str(baseURL)+" : "+str(score)+"\n"
	
fileWrite.write(line)
fileWrite.close()


    
        







