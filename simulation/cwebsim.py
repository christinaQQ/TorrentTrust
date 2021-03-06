import simulation as sim
import math
import argparse

# Trust metric stuff
_local_neighborhood = {}
_eigentrust_values = {}
def _generate_local_neighborhood(u, cutoff = 2):
  # bfs on users assigning 0-1 trust  
  q, qset = [(u, 0)], set([u])
  seen = set()
  while len(q) > 0:
    p, dist = q.pop()
    qset.remove(p)
    seen.add(p)
    for f in p.trusted():
      if not f in seen and not f in qset and dist + 1 <= cutoff:
        q.append((f, dist + 1))
        qset.add(f)
  _local_neighborhood[u.name] = seen

  
def calculateTrustMetric(u, v, trust_metric, cutoff = 2):
  if trust_metric == 'BFS':
    if cutoff < 0:
      return 1.0
    if not u.name in _local_neighborhood:
      _generate_local_neighborhood(u, cutoff)
    return 1.0 if v in _local_neighborhood[u.name] else 0.0
  elif trust_metric == 'EIGENTRUST':
    return _eigentrust_values[v]

def calculateEigentrustMetric(u, eigentrust, cutoff = .001):
  return eigentrust[u] > 0


def calculateEigentrust(users, iterations):
  v = {user: 1.0/len(users) for user in users}
  v_t = {user: 0.0 for user in users}

  for t in xrange(iterations):
    for user in users:
      for neighbor in user.trusted():
        v_t[neighbor] += v[user] / len(user.trusted())
    v = v_t
    v_t = {user: 0.0 for user in users}
  return v

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
def getScore(user, target, trustCutoff = 2, trust_metric='BFS'):
  total = 0.0
  totalCount = 0.0
  for vote in target.votes():
    if user == vote.user:
      # I voted for this
      return vote.score
    trust = calculateTrustMetric(user, vote.user, trust_metric, trustCutoff)
    correlation = calculateCorrelation(user, vote.user)
    total += trust * correlation * vote.score
    totalCount += 1 if trust * correlation != 0 else 0
  if totalCount == 0:
    return None;
  return total / totalCount

def getAllScores(users, targets, trustCutoff = 2, trust_metric='BFS', num_eigentrust_iterations=2):
  global _eigentrust_values
  scores = {}
  if trust_metric == 'EIGENTRUST':
    _eigentrust_values = calculateEigentrust(users, num_eigentrust_iterations) ### TRY WITH 5 ITERATIONS OF EIGENTRUST
  for user in users:
    for target in targets:
      score = getScore(user, target, trustCutoff, trust_metric)
      print "{}\t{}\t{}\t{}\t{}".format(user.name, target.name, score, user.designation, target.designation)
  return scores

if __name__ == "__main__":
  import sys

  parser = argparse.ArgumentParser(description='Simulate Cweb with many parameters')
  parser.add_argument('-b', '--bfsdepth', type=int, default = 0, help='''-1 (always output 1),
                                                      0 (always output 0 if not self),
                                                      1 ~ any (bfs depth k)''')
  parser.add_argument('-m', '--malclique', type=bool, default = False, help = '''malicious clique:
                                                        "true" has malicious users,
                                                        "false" doesnt have any
                                                      ''')
  parser.add_argument('-e', '--evil_targeting', type=bool, default = True, help = '''evil target:
                                                        "true" has evil node voting on one evil target untruthfully,
                                                        "false" doesnt have any, evil voters vote like non-evil ones
                                                      ''')
  parser.add_argument('-i', '--interconnect', type=int, default=0, help = '''
                                                  0 - no connections,
                                                  non-0-# # of connections to connect between spammers and legit users
                                                        ''')
  parser.add_argument('-sf', '--social_network_file', default=0, type=str, help = '''
                                                social_network_file:
                                                  0 - don't read from file
                                                  name of a file from which graph is created
                                                ''')
  parser.add_argument('-t', '--trust_metric', type=str, default='BFS', help = '''
                                                BFS, EIGENTRUST
                                                        ''')
  parser.add_argument('-c', '--num_eigentrust_iterations', type=int, default=2, 
                                                help = ''' number of iterations of eigentrust''')

  args = parser.parse_args()
  bfsdepth = args.bfsdepth
  interconnect = args.interconnect
  malclique = args.malclique
  social_network_file = args.social_network_file
  trust_metric = args.trust_metric
  assign_evil = args.evil_targeting
  num_eigentrust_iterations = args.num_eigentrust_iterations
  
  if trust_metric not in ('BFS', 'EIGENTRUST'):
    raise Exception('Trust metric {} not supported'.format(trust_metric))
  
  with open('graphdump-' + "_".join(map(str, vars(args).values())).replace('/', '-') +'.json', 'w') as f:
    allItems = sim.generateContent(4000, {'GOOD': 80, 'EVIL': 20})
    sys.stderr.write("Created {} objects...\n".format(len(allItems)))
    
    # Build the graph
    
    if social_network_file:
      oneBigCluster = sim.generateFromFile(social_network_file, allItems, designationRatios = {'GOOD': .8, 'EVIL': .2},
        malConnectivity = interconnect)
      sys.stderr.write("Loaded {} users from {}...\n".format(len(oneBigCluster), social_network_file))
    elif malclique == True:
      evilCluster = sim.generateClique(1000, 'EVIL', namePrefix= "Spammers:")
      sys.stderr.write("Created {} spammers...\n".format(len(evilCluster)))
  
      goodCluster = sim.generateRandomizedCluster(1000, designationRatios ={'GOOD': 1}, namePrefix= "Legit:")
      sys.stderr.write("Created {} legit users...\n".format(len(goodCluster)))
      
      if int(interconnect) > 0:
        sys.stderr.write("Joining good/bad clusters with {} edges...\n".format(int(interconnect)))
        sim.randomJoinTrust(goodCluster, evilCluster, int(interconnect))
      else:
        sys.stderr.write("No edges between good & bad ... \n")
      
      oneBigCluster = goodCluster + evilCluster
    else:
      oneBigCluster = sim.generateRandomizedCluster(2000, designationRatios ={'GOOD': 1}, namePrefix= "AllGood:")
    # oneBigCluster must be produced at this point
      
    # if assign_evil:
    #   # Pick one evil item
    #   evilItem = [i for i in allItems if i.designation == 'EVIL'][0]
    #   evilItem.owner = "EvilVirusSomething"
    #   evilCluster = [u for u in oneBigCluster if u.designation == 'EVIL']
    #   for u in evilCluster:
    #     u.group = evilItem.owner
    #   sys.stderr.write("Evil target assigned to EVIL users...\n")
      
      # Assign evil targets
      #s im.assignEvilTargets([u for u in oneBigCluster if u.designation == 'EVIL'], [i for i in allItems if i.designation == 'EVIL']) 
      # sys.stderr.write("Evil targets assigned some owners...\n")


    # Cast votes
    sys.stderr.write("Simulating casting of votes...\n")
    sim.castVotes(oneBigCluster, allItems, 25)
    
    # Guarantee EVIL users cast on their owned items
    sim.guaranteedCasting(oneBigCluster, allItems)
    sys.stderr.write("Guarantee that votes are indeed cast for evil users on their own content...\n")
    
    # Write graph 
    f.write(sim.flattenGraph(oneBigCluster))
    sys.stderr.write("Wrote graph structure to file...\n")
    
    if trust_metric != "EIGENTRUST": 
      sys.stderr.write("Writing results /w BFS @ Cutoff = {}...\n".format(bfsdepth))
    else:
      sys.stderr.write("Writing results /w EIGENTRUST ...\n")
    getAllScores(oneBigCluster, allItems, int(bfsdepth), trust_metric, int(num_eigentrust_iterations))

