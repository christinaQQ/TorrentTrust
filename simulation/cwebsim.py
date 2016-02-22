import simulation as sim
import math

# Trust metric stuff
_local_neighborhood = {}
def _generate_local_neighborhood(u, cutoff = 2):
  # bfs on users assigning 0-1 trust  
  q = [(u, 0)]
  seen = set()
  while len(q) > 0:
    p, dist = q.pop()
    seen.add(p)
    for f in p.trusted():
      if not f in q and dist + 1 <= cutoff:
        q.append((f, dist + 1))
  _local_neighborhood[u.name] = seen
  
def calculateTrustMetric(u, v, cutoff = 2):
  if cutoff < 0:
    return 1.0
  if not u.name in _local_neighborhood:
    _generate_local_neighborhood(u, cutoff)
  return 1.0 if v in _local_neighborhood[u.name] else 0.0

# Correlation stuff
DEFAULT_CORRELATION = 0
_cache = {}
def calculateCorrelation(u, v):
  if (u.name, v.name) in _cache:
    return _cache[(u.name, v.name)]
  if (v.name, u.name) in _cache:
    return _cache[(v.name, u.name)]
  commonTargets = [t for t in u.votedTargets if t in v.votedTargets]
  if len(commonTargets) == 0:
    return DEFAULT_CORRELATION
  correlationScore = 0;
  uscore, vscore = 0.0, 0.0;
  for target in commonTargets:
    uvote, vvote = target.getVotesOf(u), target.getVotesOf(v)
    correlationScore += uvote.score * vvote.score
    uscore += uvote.score ** 2
    vscore += vvote.score ** 2
  _cache[(u.name, v.name)] = correlationScore / (math.sqrt(uscore) * math.sqrt(vscore))
  return _cache[(u.name, v.name)]

# Scores 
def getScore(user, target, trustCutoff = 2):
  total = 0.0
  totalCount = 0.0
  for vote in target.votes():
    if user == vote.user:
      # I voted for this
      return vote.score
    trust = calculateTrustMetric(user, vote.user, trustCutoff)
    correlation = calculateCorrelation(user, vote.user)
    total += trust * correlation * vote.score
    totalCount += 1 if trust * correlation != 0 else 0
  if totalCount == 0:
    return None;
  return total / totalCount

def getAllScores(users, targets, trustCutoff = 2):
  scores = {}
  for user in users:
    for target in targets:
      score = getScore(user, target, trustCutoff)
      print "{}\t{}\t{}\t{}\t{}".format(user, target, score, user.designation, target.designation)
  return scores

if __name__ == "__main__":
  import sys
  
  if len(sys.argv) <= 1:
    print """
cwebsim [bfsdepth] [malicious clique] [interconnects]

Parameters listed in order:

	bfsdepth : 
		-1 (always output 1), 
		0 (always output 0 if not self), 
		1 ~ any (bfs depth k)
	malicious clique: 
		"true" has malicious users, 
		"false" doesnt have any
	interclique connections: 
		0 - no connections, 
		non-0-# # of connections to connect between spammers and legit users
"""
    exit(1)
  params = sys.argv[1:]
  bfsdepth, malclique, interconnect = tuple(params)
  with open('graphdump-' + "_".join(params) +'.json', 'w') as f:
    allItems = sim.generateContent(4000, {'GOOD': 80, 'EVIL': 20})
    sys.stderr.write("Created {} objects...\n".format(len(allItems)))
    
    if malclique == "true":
      badCluster = sim.generateClique(1000, 'EVIL', namePrefix= "Spammers:")
      sys.stderr.write("Created {} spammers...\n".format(len(badCluster)))
  
      goodCluster = sim.generateRandomizedCluster(1000, designationRatios ={'GOOD': 1}, namePrefix= "Legit:")
      sys.stderr.write("Created {} legit users...\n".format(len(goodCluster)))
      
      if int(interconnect) > 0:
        sys.stderr.write("Joining good/bad clusters with {} edges...\n".format(int(interconnect)))
        sim.randomJoinTrust(goodCluster, badCluster, int(interconnect))
      else:
        sys.stderr.write("No edges between good & bad ... \n")
      
      oneBigCluster = goodCluster + badCluster
      
      # Pick one evil item
      evilItem = [i for i in allItems if i.designation == 'EVIL'][0]
      evilItem.owner = "EvilVirusSomething"
      for u in badCluster:
        u.group = evilItem.owner
      sys.stderr.write("Evil target assigned owners...\n")
      
      # Assign evil targets
      # sim.assignEvilTargets([u for u in oneBigCluster if u.designation == 'EVIL'], [i for i in allItems if i.designation == 'EVIL']) 
      # sys.stderr.write("Evil targets assigned some owners...\n")
    else:
      oneBigCluster = sim.generateRandomizedCluster(2000, designationRatios ={'GOOD': 1}, namePrefix= "AllGood:")

    sys.stderr.write("Simulating casting of votes...\n")
    sim.castVotes(oneBigCluster, allItems, 25)
    
    sim.guaranteedCasting(oneBigCluster, allItems)
    sys.stderr.write("Guarantee that votes are indeed cast for evil users on their own content...\n")
    
    
    f.write(sim.flattenGraph(oneBigCluster))
    sys.stderr.write("Wrote graph structure to file...\n")
    
    sys.stderr.write("Writing results @ trustCutoff = {}...\n".format(bfsdepth))
    getAllScores(oneBigCluster, allItems, int(bfsdepth))

