function h=plotSpace(vProj,vModul)
x=vProj(:,1);
y=vProj(:,2);
z=vModul;
nbSquare=50;
lenx=0;
leny=0;
xlimits=[min(x)-lenx,max(x)+lenx];
ylimits=[min(y)-leny,max(y)+leny];
[xi,yi]=meshgrid(linspace(xlimits(1),xlimits(2),nbSquare),linspace(ylimits(1),ylimits(2),nbSquare));
h=figure();
regular = axes('Parent',h,'Position',[0.1,0,.8,.75]);
%xlim([-1.7 3.2]);
%ylim([-2 3]);
zlabel('Modularity','FontSize',16);
view([23.5 20]);
grid('off');
hold('all');
zi=griddata(x,y,z,xi,yi,'cubic');
surf(xi,yi,zi,'Parent',regular) %can use mesh, meshc, surf, surfc surfl
plot3(x,y,z,'yo');
%set(regular,'XTickLabel',[]);
%set(regular,'YTickLabel',[]);

background = axes('Parent',h,'Position',[.069,.699,.862,.272]);
set(background,'Box','On');
set(background,'XTick',[]);
set(background,'YTick',[]);

max_zi = max(max(zi));

percent = 0.85

nbSquare=500;
lenx=0;
leny=0;
xlimits=[min(x)-lenx,max(x)+lenx];
ylimits=[min(y)-leny,max(y)+leny];
[xi,yi]=meshgrid(linspace(xlimits(1),xlimits(2),nbSquare),linspace(ylimits(1),ylimits(2),nbSquare));
zi=griddata(x,y,z,xi,yi,'cubic');

indices = find(zi > percent*max(max(zi)));

zoomedxi = xi(indices);
zoomedyi = yi(indices);

max(zoomedxi)
min(zoomedxi)

inset = axes('Parent',h,'Position',[.07,.70,.86,.30]);
mesh(xi,yi,zi,'Parent',inset);
%surf(xi,yi,zi,'Parent',inset, 'FaceColor', 'red', 'EdgeColor','none');
%camlight left; lighting phong

zlim([percent*max_zi,max_zi+1*(max_zi-percent*max_zi)]);
xlim([min(zoomedxi),max(zoomedxi)]);
ylim([min(zoomedyi),max(zoomedyi)]);

view([0 45]);

set(inset,'Visible','off');
grid('off');
set(inset,'XTick',[]);
set(inset,'YTick',[]);

hold off;
