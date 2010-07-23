function h=create_embedding(filename)

[q_list, partition_vectors] = parse_file(filename);
vi_matrix = calculate_vi_matrix(partition_vectors);
h = plotSpace(cca(vi_matrix,2,5000),q_list)

end