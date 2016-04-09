import simulation as sim
import numpy as np
import argparse

def benchmarkConnectivity(graph):
  degrees = [len(user.trusted()) for user in graph]
  print """
	Average		Maximum		Minimum		Median
Deg	{}		{}		{}		{}
""".format(np.mean(degrees), np.max(degrees), np.min(degrees), np.median(degrees))

def benchmarkDesignation(graph):
  good = [user for user in graph if user.designation == 'GOOD']
  evil = [user for user in graph if user.designation == 'EVIL']
  
  print "Good Subgraph:"
  benchmarkConnectivity(good);
  
  print "Evil Subgraph:"
  benchmarkConnectivity(evil);

if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='Graph benchmarks')
  parser.add_argument('-sf', '--social_network_file', type=str, help = '''
                                                social_network_file:
                                                  name of a file from which graph is created
                                                ''', required=True)
  args = parser.parse_args()
  social_network_file = args.social_network_file
  graph = sim.generateFromFile(social_network_file, designationRatios = {'GOOD': .8, 'BAD': .2}, malConnectivity = .1)
  
  benchmarkConnectivity(graph)
  benchmarkDesignation(graph)
