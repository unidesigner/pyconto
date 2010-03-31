#!/usr/local/bin/python
# --------------------------------------------------------------------------------------------------
#  
#	modularity_dir.py
#
#	made by mrGreg: on 3rd September 2009 (thomascorcra@gmail.com)
# 
# --------------------------------------------------------------------------------------------------
#
"""
This is a port of Sporns' toolbox Girvan-Newman algorithm for W-Dir-networks.
"""

__author__ = """multiple"""

from scipy import *
#import scipy
from scipy.linalg import eig
#from constants import _epsilon
_epsilon = 1e-10

#dot = lambda a, b: scipy.linalg.fblas.dgemm(1., a, b)

def modularity_dir(A):
	"""
	Input: weighted directed network (adjacency or weights matrix) A.
	Output: community structure Ci; maximized modularity Q.
	"""
	
	Ko = sum(A,0).reshape(-1,1).astype(float)		#out-degree
	Ki = sum(A,1).reshape(1,-1).astype(float)		#in-degree
                        
	N = len( A )                         			#number of vertices
	m = sum( Ki )                           		#number of edges
	b = A-dot(Ko,Ki).T/m
	B = b + b.T                   					#directed modularity matrix
	Ci = ones((N,1))                          		#community indices
	cn = 1                                  		#number of communities
	U = array([1, 0])                               #array of unexamined communites
	
	ind = arange(N)#1:N;
	Bg=B.copy()#.astype(int)
	Ng=N
	
	#examine community U[0]    
	while U[0]:
		#note the current return values is the reverse of MLab for eig(.)!
		D, V = eig(Bg)								
		#most positive eigenvalue of Bg
		#in Scipy.linalg it's always the first one (at the moment)
		v,i1 = D.max(0),D.argmax(0)
		#corresponding (column) eigenvector											
		v1 = V[:,i1].reshape(-1,1)		
		S = ones((Ng,1))
		S[v1<0] = -1
		#contribution to modularity
		q = dot(dot(S.T,Bg),S)
		#contribution positive: U(1) is divisible
		if q>_epsilon:	
			#maximal contribution to modularity
			qmax = q													
			#Bg is modified, to enable fine-tuning
			Bg[eye(Ng,dtype=bool)] = 0							
			indg=ones((Ng,1),dtype=bool)
			Sit=S.copy()
			while any(indg!=0):
				Qit = qmax - 4*Sit*dot(Bg,Sit)
				qmax = (Qit*indg)[indg.flatten()].max(0)
				imax = (Qit==qmax)
				Sit[imax] = -Sit[imax]
				indg[imax] = 0
				if qmax>q:
					q = qmax
					S = Sit.copy()
			if abs(sum(S))==Ng:
				#unsuccessful splitting of U(1)
				U = delete(U,0)							
			else:
				cn = cn + 1
				#split old U(1) into new U(1) and into cn
				Ci[ind[(S==1).flatten()]] = U[0]				
				Ci[ind[(S==-1).flatten()]] = cn
				U = append(cn,U)
		else:										
			#contribution nonpositive: U(1) is indivisible
			U = delete(U,0)
		ind = nonzero(Ci==U[0])[0]
		bg = B[ix_(ind,ind)]
		Bg = bg - diag(sum(bg,0))
		Ng = len(ind)
	
	#compute modularity
	s = repeat(Ci,N,1)								
	Q=(((s-s.T)==0)*B)/(2*m);
	Q=sum(Q)

	return Ci, Q

if __name__=='__main__':
	import scipy.io as io
	cat = io.loadmat('BCT/cat.mat')['CIJall']
	print modularity_dir(cat)
