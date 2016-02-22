#!/bin/bash

set -e
# Credence 

echo "@@@ WARNING @@@"
echo "= "
echo "= This script produces 20-30 GB of output AND runs 100% on all available threads ="
echo "= Be prepared for your computer to freeze. ="
echo "="
echo "@@@ ======= @@@"

read -rsp "Press any key to continue..." -n1 key

#  - Perfect World
python cwebsim.py -1 false 0 > "output/credPFW_$1.tsv" & 

#  - Kiddie
python cwebsim.py -1 true 0 > "output/credKiddie_$1.tsv" & 

#  - Advanced kiddie
python cwebsim.py -1 true 1 > "output/credAdv1_$1.tsv" & 
python cwebsim.py -1 true 2 > "output/credAdv2_$1.tsv" & 
python cwebsim.py -1 true 5 > "output/credAdv5_$1.tsv" & 
python cwebsim.py -1 true 10 > "output/credAdv10_$1.tsv" & 

# BFS/1
python cwebsim.py 1 false 0 > "output/bfs1PFW_$1.tsv" &

#  - Kiddie
python cwebsim.py 1 true 0 > "output/bfs1Kiddie_$1.tsv" & 

#  - Advanced kiddie
python cwebsim.py 1 true 1 > "output/bfs1Adv1_$1.tsv" & 
python cwebsim.py 1 true 2 > "output/bfs1Adv2_$1.tsv" & 
python cwebsim.py 1 true 5 > "output/bfs1Adv5_$1.tsv" & 
python cwebsim.py 1 true 10 > "output/bfs1Adv10_$1.tsv" & 

# BFS/2
python cwebsim.py 2 false 0 > "output/bfs2PFW_$1.tsv" & 

#  - Kiddie
python cwebsim.py 2 true 0 > "output/bfs2Kiddie_$1.tsv" &

#  - Advanced kiddie
python cwebsim.py 2 true 1 > "output/bfs2Adv1_$1.tsv" & 
python cwebsim.py 2 true 2 > "output/bfs2Adv2_$1.tsv" & 
python cwebsim.py 2 true 5 > "output/bfs2Adv5_$1.tsv" & 
python cwebsim.py 2 true 10 > "output/bfs2Adv10_$1.tsv" & 

# BFS/3
python cwebsim.py 3 false 0 > "output/bfs3PFW_$1.tsv" & 

#  - Kiddie
python cwebsim.py 3 true 0 > "output/bfs3Kiddie_$1.tsv" & 

#  - Advanced kiddie
python cwebsim.py 3 true 1 > "output/bfs3Adv1_$1.tsv" & 
python cwebsim.py 3 true 2 > "output/bfs3Adv2_$1.tsv" & 
python cwebsim.py 3 true 5 > "output/bfs3Adv5_$1.tsv" & 
python cwebsim.py 3 true 10 > "output/bfs3Adv10_$1.tsv" &
