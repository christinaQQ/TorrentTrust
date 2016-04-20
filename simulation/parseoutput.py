import sys

if len(sys.argv) < 2:
  raise Exception("Need to provide the file to analyze as the single argument")

cutoff = 0 if len(sys.argv) < 3 else float(sys.argv[2])
malicious_objects = set()
virus_correct = 0
virus_incorrect = 0

#first find malicious object
with open(sys.argv[1]) as f:
  for line in f:
    if line.strip() == "":
      continue
    user, target, score, usertype, targettype = tuple(line.strip().split('\t'))
    if usertype == "EVIL" and targettype == "EVIL" and score == "1.0" and target not in malicious_objects:
      malicious_objects.add(target)
print malicious_objects
with open(sys.argv[1]) as f:
  tpgood, tngood, fpgood, fngood = 0, 0, 0, 0
  goodScore, evilScore = 0, 0
  unknown = 0
  for line in f:
    if line.strip() == "":
      continue
    user, target, score, usertype, targettype = tuple(line.strip().split('\t'))
    
    if usertype == "EVIL":
      # Ignore evil users
      continue
    
    if score == 'None':
      unknown += 1.0
      continue
    
    if targettype == "GOOD":
      goodScore += float(score)
    else:
      evilScore += float(score)
    
    label = "GOOD" if float(score) > cutoff else "EVIL"
    if targettype == "GOOD":
      if label == "GOOD":
        tpgood += 1.0
      else:
        fngood += 1.0
    else:
      if label == "GOOD":
        fpgood += 1.0
      else:
        tngood += 1.0

    if target in malicious_objects and label == "GOOD":
      virus_incorrect += 1
    elif target in malicious_objects and label == "EVIL":
      virus_correct += 1


  precision = tpgood / (tpgood + fpgood)
  recall = tpgood / (tpgood + fngood)
  tnr = tngood / (tngood + fpgood)
  acc = (tpgood + tngood) / (tpgood + tngood + fpgood + fngood)
  fscore = 2 * (precision * recall) / (precision + recall)
  
  print "Virus correct %i , incorrect %i" % (virus_correct, virus_incorrect)

  uncr = unknown / (unknown + tpgood + tngood + fpgood + fngood)
  print "Precision: {}\nRecall: {}\nTrue Negative Rate:{}\nAccuracy:{}\nF1Score: {}\n\nUnclassified count: {}\nUnclassified Rate: {}\n".format(precision, recall, tnr, acc, fscore, unknown, uncr)
  
  print "Avg Good Score:{}\nAvg Evil Score:{}\n".format(goodScore / (tpgood + fngood), evilScore / (tngood + fpgood))
