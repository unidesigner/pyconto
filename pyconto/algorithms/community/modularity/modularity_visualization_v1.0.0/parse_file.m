function [q_list, partition_vectors]=parse_file(filename)
    
file = fopen(filename,'r');

q_list = [];
partition_vectors = [];

line = fgetl(file);
while ischar(line)    
   parts = regexp(line,',','split','once');
   q = str2num(parts{1});
   sigma_cell = parts{2};
   sigma_string = sigma_cell(2:length(sigma_cell)-1);
   sigma_cell = textscan(sigma_string,'%d','delimiter',',');
   sigma = sigma_cell{:};
   
   if q > -2
        q_list = [q_list; q];
        partition_vectors = [partition_vectors; sigma'];
   end
   line = fgetl(file);
end

fclose(file);

end
