import random
import json
import numpy as np
# Some designations
GOOD, EVIL = "GOOD", "EVIL"

def nameGenerator(prefix = "", count = 100):
  for i in range(0, count):
    yield "{}{}".format(prefix, i)

class Vote (object):
  def __init__(self, user, target, score = 1.0):
    self.user = user
    self.target = target
    self.score = score

class User (object):
  '''
    A user being simulated
  '''


  def __init__(self, name, designation = 'GOOD', votingFunction = None):
    self._trusted = []
    self.votes = []
    self.votedTargets = set()
    self.name = name
    self.group = name
    self.designation = designation
    self.votingFunction = self._defaultVotingFunction if votingFunction == None else votingFuntion
  
  # Default function used if no voting behavior is specified
  def _defaultVotingFunction(self, user, target):
    if target.designation == "GOOD":
      return Vote(user, target, 1.0)
    else:
      if user.designation == "EVIL" and target.owner == user.group:
        return Vote(user, target, 1.0)
      return Vote(user, target, -1.0)
  
  # Gets list of trusted nodes
  def trusted(self):
    return self._trusted
    
  # Adds item onto list of trusted nodes
  def addTrusted(self, user):
    if not user in self._trusted:
      self._trusted.append(user)
    return
  
  def hasVoteFor(self, target):
    return target in self.votedTargets
  
  # Vote on content
  def vote(self, target):
    if self.hasVoteFor(target):
      raise Exception("Attempted to vote on an existing voted content. [{}] [{}]".format(str(self), str(target)))
    vote = self.votingFunction(self, target)
    target.addVote(vote)
    self.votedTargets.add(target)
    self.votes.append(vote)
  
  def __str__(self):
    return "User: {} ({}), {}, {} trusted peers".format(self.name, self.group, self.designation, len(self._trusted))
  #
  # def __repr__(self):
  #   return "User(name={}, designation={}, votingFuntion={}, trusted={}, _votes={}, _votedTargets={})".format(repr(self.name), repr(self.designation), repr(self.votingFunction), repr(self._trusted), repr(self.votes), repr(self.votedTargets))

class Target (object):
  '''
    Target objects that users vote on
  '''
  
  def __init__(self, name, designation = 'GOOD', owner = None):
    self._votes = []
    self.votedUsers = set()
    self.name = name
    self.designation = designation
    self.owner = owner
  
  def votes(self):
    return self._votes
  
  def getVotesOf(self, user):
    return [v for v in self._votes if v.user == user][0]
  
  def hasUserVoting(self, user):
    return user in self.votedUsers
  
  def addVote(self, vote):
    if not self.hasUserVoting(vote.user):
      self._votes.append(vote)
      self.votedUsers.add(vote.user)
    return 
  
  def __str__(self):
    return "Object: {}, {}, owned by {}".format(self.name, self.designation, self.owner)
  
  def __repr__(self):
    return "Target(name={}, designation={}, owner={}, _votes={}, _votedUsers={})".format(repr(self.name), repr(self.designation), repr(self.owner), repr(self.votes), repr(self.votedUsers))

def generateRandomizedCluster(clusterSize, minDegree = 1, maxDegree = 50, designationRatios = {'GOOD': 80, 'EVIL': 20}, namePrefix = 'Group1:'):
  users = []
  if maxDegree >= clusterSize:
    raise Exception("The max degree cannot exceed cluster size k")
  
  designationSample = []
  for k, v in designationRatios.items():
    designationSample += [k] * v
  
  for name in nameGenerator(namePrefix, clusterSize):
    users.append(User(name, random.choice(designationSample)))
  
  while not all(len(user.trusted()) >= minDegree for user in users):
    # Pick two users
    a, b = random.choice(users), random.choice(users)
    if a == b or b in a.trusted() or a in b.trusted():
      continue;
    if len(a.trusted()) >= maxDegree or len(b.trusted()) >= maxDegree:
      continue;
    a.addTrusted(b)
    b.addTrusted(a)
  return users

def generateClique(cliqueSize, designation = 'GOOD', namePrefix = 'Group1'):
  users = []
  for name in nameGenerator(namePrefix, cliqueSize):
    users.append(User(name, designation))
  for user in users:
    for friend in users:
      if user != friend:
        user.addTrusted(friend)
  return users

def generateFromFile(fileName, allItems, designation = 'GOOD', namePrefix = 'Group1', designationRatios = {'GOOD': .8, 'EVIL': .2},
    malConnectivity = .1):
  with open(fileName, 'r') as f:
    f.readline()
    # first get the number of people in the cluster
    goodClusterSize = 0
    while (f.readline().strip() != "</PEOPLE>"):
      goodClusterSize +=1
    clusterSize = int(goodClusterSize / designationRatios['GOOD'])
    
    users = []
    for name in nameGenerator(namePrefix, goodClusterSize):
      users.append(User(name, 'GOOD'))
    #now read in friend connections
    while (f.readline().strip() != "<FRIENDS>"):
      continue
    for line in f:
      if line.strip() == "</FRIENDS>":
        break
      l = line.split('-')
      user = int(l[1]) - 1
      friend = int(l[2]) - 1
      users[user].addTrusted(users[friend])
      # look into this: the data is undirected
      users[friend].addTrusted(users[user])
  
  bad_users = generateBadCliques(malConnectivity, clusterSize - goodClusterSize, users, 5, allItems)
  return users + bad_users

def generateBadUsers(malConnectivity, goodClusterSize, clusterSize, users):
  # generate bad users
  bad_users = []
  numConnect = np.random.normal(malConnectivity * goodClusterSize,3,clusterSize - goodClusterSize)
  for i, name in enumerate(nameGenerator('Group2', clusterSize - goodClusterSize)):
    badUser = User(name, 'EVIL')
    for goodUser in random.sample(users, abs(int(round(numConnect[i])))):
      badUser.addTrusted(goodUser)
      goodUser.addTrusted(badUser)
    bad_users.append(badUser)
  return bad_users

def generateBadCliques(malConnectivity, num_bad, users, num_clusters, allItems):
  # numConnect = np.random.normal(malConnectivity * goodClusterSize,3,clusterSize - goodClusterSize)
  evilItems = [i for i in allItems if i.designation == 'EVIL']

     # generate bad users
  bad_users = []
  size_bad_cluster = (num_bad) / num_clusters
  for n in range(num_clusters):
    bad_users.append([])
    for i, name in enumerate(nameGenerator('Group9%i' % n, size_bad_cluster)):
      bad_users[n].append(User(name, 'EVIL'))
  for i, bad_user_group in enumerate(bad_users):
    # assign a piece of content
    evil_item = evilItems[i]
    evil_item.owner = "EvilVirus%i" % i
    # create the clique
    for user in bad_user_group:
      for friend in bad_user_group:
        if user != friend:
          user.addTrusted(friend)
      user.group = evil_item.owner
  return [user for clique in bad_users for user in clique]





  # for i, name in enumerate(nameGenerator('Group2', clusterSize - goodClusterSize)):
  #   badUser = User(name, 'EVIL')
  #   for goodUser in random.sample(users, abs(int(round(numConnect[i])))):
  #     badUser.addTrusted(goodUser)
  #     goodUser.addTrusted(badUser)
  #   bad_users.append(badUser)
  #   # Pick one evil item
  #   evilItem = [i for i in allItems if i.designation == 'EVIL'][0]
  #   evilItem.owner = "EvilVirusSomething"
  #   evilCluster = [u for u in oneBigCluster if u.designation == 'EVIL']
  #   for u in evilCluster:
  #     u.group = evilItem.owner
  #   sys.stderr.write("Evil target assigned to EVIL users...\n")

def assignEvilTargets(evilUsers, targets, targetsPerUser = 3):
  targetset = set(targets)
  for user in evilUsers:
    sample = random.sample(targetset, min(len(targetset), targetsPerUser))
    for s in sample:
      s.owner = user.name
      targetset.remove(s)

def generateContent(amount, designationRatios = {'GOOD': 80, 'EVIL': 20}, namePrefix = 'ObjectGroup:'):
  content = []
  sampler = []
  for k, v in designationRatios.items():
    sampler += [k] * v
  for name in nameGenerator(namePrefix, amount):
    content.append(Target(name, random.choice(sampler)))
  return content

def randomJoinTrust(usersA, usersB, samples = 10, depth = 1):
  aSamples = random.sample(usersA, samples)
  for u in aSamples:
    bSamples = random.sample(usersB, depth)
    for v in bSamples:
      u.addTrusted(v)
      v.addTrusted(u)

def flattenVoting(users, targets):
  for user in users:
    for vote in votes:
      print vote

def flattenGraph(users):
  output = {}
  for user in users:
    output[user.name] = {}
    output[user.name]['designation'] = user.designation
    output[user.name]['trusted'] = []
    for n in user.trusted():
      output[user.name]['trusted'].append(n.name)
  return json.dumps(output)

# Users casting votes, targets accepting votes, votes cast per user
def castVotes(users, targets, sampleSize = 50):
  for user in users:
    sample = random.sample(targets, sampleSize)
    for target in sample:
      user.vote(target)

# Always cast votes on their own content
def guaranteedCasting(users, targets):
  for target in targets:
    if target.owner == None:
      continue
    for user in users:
      if user.group == target.owner:
        if not user in target.votedUsers: 
          user.vote(target)




