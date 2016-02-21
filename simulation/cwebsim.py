import simulation as sim
import math

# Trust metric stuff
_local_neighborhood = {}
DISTANCE_CUTOFF = 3
def _generate_local_neighborhood(u):
  # bfs on users assigning 0-1 trust  
  q = [(u, 0)]
  seen = set()
  while len(q) > 0:
    p, dist = q.pop()
    seen.add(p)
    for f in p.trusted():
      if not f in q and dist + 1 <= DISTANCE_CUTOFF:
        q.append((f, dist + 1))
  _local_neighborhood[u.name] = seen
  
def calculateTrustMetric(u, v):
  if not u.name in _local_neighborhood:
    _generate_local_neighborhood(u)
  return 1.0 if v in _local_neighborhood[u.name] else 0.0

# Correlation stuff
DEFAULT_CORRELATION = 0.1
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
def getScore(user, target):
  total = 0.0
  for vote in target.votes():
    if user == vote.user:
      # I voted for this
      return vote.score
    trust = calculateTrustMetric(user, vote.user)
    correlation = calculateCorrelation(user, vote.user)
    total += trust * correlation * 1.0
  if len(target.votes()) == 0:
    return 0.0;
  return total / len(target.votes())

def getAllScores(users, targets):
  scores = {}
  for user in users:
    sumGood, sumEvil = 0, 0
    totalGood, totalEvil = 0, 0
    maxGood, maxEvil, minGood, minEvil = -2, -2, 2, 2
    for target in targets:
      score = getScore(user, target)
      if target.designation == 'GOOD':
        sumGood += score
        totalGood += 1
        maxGood = max(maxGood, score)
        minGood = min(minGood, score)
      else:
        sumEvil += score
        totalEvil += 1
        maxEvil = max(maxEvil, score)
        minEvil = min(minEvil, score)
    print "{}\t{}\t{}\t".format(user, sumGood / totalGood, sumEvil / totalEvil)
  return scores
      

if __name__ == "__main__":
  import sys
  allItems = sim.generateContent(4000, {'GOOD': 80, 'EVIL': 20})
  sys.stderr.write("Created {} objects...\n".format(len(allItems)))
  
  badCluster = sim.generateRandomizedCluster(1000, designationRatios ={'GOOD': 1, 'EVIL': 99}, namePrefix= "MostlySpammers:")
  sys.stderr.write("Created {} spammers...\n".format(len(badCluster)))
  
  goodCluster = sim.generateRandomizedCluster(2000, designationRatios ={'GOOD': 99, 'EVIL': 1}, namePrefix= "MostlyLegit:")
  sys.stderr.write("Created {} legit users...\n".format(len(goodCluster)))
  
  sim.randomJoinTrust(goodCluster, badCluster)
  sys.stderr.write("Joined user clusters.\n")
  oneBigCluster = goodCluster + badCluster
  
  # Assign evil targets
  sim.assignEvilTargets([u for u in oneBigCluster if u.designation == 'EVIL'], [i for i in allItems if i.designation == 'EVIL']) 
  sys.stderr.write("Evil targets assigned some owners...\n")
  
  sim.castVotes(oneBigCluster, allItems, 50)
  sys.stderr.write("Simulating casting of votes...\n")
  sim.guaranteedCasting(oneBigCluster, allItems)
  sys.stderr.write("Confirming that votes are indeed cast for evil users on their own content")
  
  scores = getAllScores(oneBigCluster, allItems)
  
  for user, target in scores:
    print score[(user, target)]
