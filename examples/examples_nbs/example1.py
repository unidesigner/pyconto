""" This script provides a toy example of the NBS """

import numpy as np
import nbs
from pylab import imshow, show, title

# Import test data
import scipy.io as io
Xmat = io.loadmat('X.mat')
Ymat = io.loadmat('Y.mat')
X = Xmat['X']
Y = Ymat['Y']
n = X.shape[0]

# Run the NBS with the following parameter options: 
# Set an appropriate threshold. It is difficult to provide a rule of thumb 
# to guide the choice of this threshold. Trial-and-error is always an option
# with the number of permutations generated per trial set low. 
THRESH=3; 
# Generate 10 permuations. Many more permutations are required in practice
# to yield a reliable estimate. 
K=10;
# Set TAIL to left, and thus test the alternative hypothesis that mean of 
# population X < mean of population Y
TAIL='left'; 
# Run the NBS
PVAL, ADJ, NULL = nbs.compute_nbs(X,Y,THRESH,K,TAIL); 

print "pval", PVAL
print "null", NULL

imshow(ADJ, interpolation='nearest')
title('Edges identified by the NBS')
show()

# Index of true positives
#ind_tp=[ind_set1;ind_set2]; 
ind_tp = np.vstack( (set1, set2) )

# Index of positives idenfitied by the NBS
#ind_obs=find(adj); 
ind_obs = np.array(np.where(np.triu(ADJ))).T

# False positive rate
#fp=length(setdiff(ind_obs,ind_tp))/(20*19/2);
fp_idx = nbs.setdiff2d(ind_obs, ind_tp)
fp = fp_idx.shape[0] / (n * (n-1) / 2.) # only upper triangular matrix is taken into account

# True positive rate
#tp=length(intersect(ind_tp,ind_obs))/length(ind_tp);
tp_idx = nbs.intersect2d(ind_obs, ind_tp)
tp = tp_idx.shape[0] * 1. / len(ind_tp) 

print "True positive rate # %0.3f. False positive rate: # %0.3f" % (tp, fp)
