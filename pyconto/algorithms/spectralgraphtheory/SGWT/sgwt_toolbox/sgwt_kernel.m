% sgwt_kernel : Compute sgwt kernel
%
% function g=sgwt_kernel(x,varargin)
%
% This function will evaluate the kernel at input x
%
% Inputs : 
% x - independent variable values
% Selectable Inputs : 
% 'type' - 'abspline' gives polynomial / spline / power law decay kernel
% alpha,beta,t1,t2 - parameters for abspline kernel
%
% Outputs :
% g - array of values of g(x)

% This file is part of the SGWT toolbox (Spectral Graph Wavelet Transform toolbox)
% Copyright (C) 2010, David K. Hammond. 
%
% The SGWT toolbox is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License, or
% (at your option) any later version.
%
% The SGWT toolbox is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with the SGWT toolbox.  If not, see <http://www.gnu.org/licenses/>.

function g=sgwt_kernel(x,varargin)
  control_params={'gtype','abspline',...
                  'a',2,...
                  'b',2,...
                  't1',1,...
                  't2',2,...
                 };
  argselectAssign(control_params);
  argselectCheck(control_params,varargin);
  argselectAssign(varargin);
  
  switch gtype
   case 'abspline'
    g=sgwt_kernel_abspline3(x,a,b,t1,t2);
   case 'mh'
    g=x.*exp(-x);
   otherwise
    error('unknown type')
  end
  