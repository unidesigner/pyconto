function [Y,E] = cca(D0,dim,itr)
% Curvilinear Component Analysis:
%
% [Y,E,Z] = cca(D,dim,itr,sfr,kir)
%
% Inputs:
% D   : pairwise distances (symmetric matrix)
%       (use pairwisedistances.m to compute them)
% dim : embedding dimensionality
% itr : maximum number of iterations
%       (default is 100)
% 
% Outputs:
% Y   : coordinates of embedded data
% E   : values of objective function 
% 
% Copyright 2009 J.A.Lee

% default parameters
if nargin<2, dim = 2; end
if nargin<3, itr = 100; end

% check parameters
dim = ceil(abs(dim(1)));
itr = ceil(abs(itr(1)));

% size of data set
sz = size(D0);
nbr = sz(1);

% give up if matrix of pairwise distances is not square
if sz(1)~=sz(2)
    error('Matrix of pairwise distances is not square');
end

% give up if matrix of pairwise distances is not symetric
if any(any(D0~=D0'))
    error('Matrix of pairwise distances is not symetric');
end

% give up if there are more than one connected component
if any(any(D0==inf))>0
    error('More than one connected component!');
end

% make it deterministic

% sort everything according to mass center (computed from distances)
% (this makes the solution independent of the ordering of data)
[val,idx] = sort(sum(D0.^2));
D0 = D0(idx,:);
D0 = D0(:,idx);

% reinitialize the random number generator
% (all runs must give the same result)
rand('twister',5489);

% PCA initialization

% compute scalar products from squared distances
S0 = D0.^2;
sS = sum(S0,1)./nbr;
S0 = -1/2*(S0 - bsxfun(@plus, sS, sS') + sum(sS)/nbr); % double centering

% compute coordinates with classical MDS
[V,D] = eig(S0);
[D,P] = sort(diag(D),'descend');
thr = find(cumsum(D) >= 0.99*sum(D));
thr = min(dim, thr(1));
V = V(:,P(1:thr));
D = diag(sqrt(D(1:thr))./nbr);
Y = V * D;

% metaparameter ranges

% maximum and minimum lambda
xD = 4*max(max(D0));
nD = xD / itr;

% display some info
fprintf(1,'CCA: %i iterations\n',itr);
fprintf(1,'lambda in [%f, %f]\n',xD,nD);

% additional variables

% objective function values
E = zeros(nbr,itr+1);

% embedding optimization

% iterate
sD3 = zeros(size(D0));
fprintf(1,'__________\n');
cnt = 10;
for t = 1:(itr+1)
    if cnt<=floor(100*t/itr),
        cnt = cnt + 10;
        fprintf(1,'^');
    end
    
    % step size
    stp = 0.2;
    
    % neighborhood radius
    rad = -xD*nD/(nD-xD)*(100-1)/(t-(nD*100-xD*1)/(nD-xD)); %-xD*nD/(nD-xD)*(itr-1)/(t-(nD*itr-xD*1)/(nD-xD));
    
    %j = 1+rem(t-1,nbr);
    
    % for each datum (some (pseudo-)randomness is recommended here)
    for i = randperm(nbr) %[idx(j:nbr),idx(1:j-1)] %[j:nbr,1:j-1] %
        % differences
        dfs = bsxfun(@minus, Y, Y(i,:));
        
        % compute distances
        D1 = D0(:,i);
        D1(i) = 1;
        sD1 = D1.*D1;
        sD2 = sum(dfs.*dfs,2);
        sD2(i) = 1;
        D2 = sqrt(sD2);
        DD = D1 - D2;
        sDD = (sD1 - sD2);
        
        % Heaviside step function
        H = D2 <= rad;
        
        % original CCA followed by CCA for noisy data
        if t<1%itr/2
            Ei = DD.*DD;
            gY = DD./D2;
        else
            ltmp = (DD>0).*sDD./(4*sD1);
            gtmp = (DD<0).*DD;
            Ei = ltmp.*sDD + gtmp.*DD;
            gY = ltmp + gtmp./D2;
        end
        
        % objective function
        E(i,t) = sum(H.*Ei)/nbr;
        
        if t<=itr
            % gradient descent
            Y = Y + bsxfun(@times, stp*H.*gY, dfs);
            Y = bsxfun(@minus, Y, sum(Y)/nbr);
        end
    end

    % stopping criterion
    dfs = bsxfun(@minus, permute(Y,[3,1,2]), permute(Y,[1,3,2]));
    sD2 = sum(dfs.*dfs,3);
    crt = sum(sum(abs(sD2-sD3)))/sum(sum(sD2+sD3));
    if crt<0.01^2,
        E = E(:,1:t);
        break;
    end
    sD3 = sD2;
end
fprintf(1,'\n');

% PCA decorrelation (to avoid translational and rotational freedom)
Y = bsxfun(@minus, Y, sum(Y)/nbr);
[U,S] = svd(Y,0);
Y = U * S;

% rearrange in initial order
Y(idx,:) = Y;
E(idx,:) = E;
